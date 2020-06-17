package com.zr.blog.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.blog.domain.Comment;
import com.zr.blog.domain.Tag;
import com.zr.blog.mapper.TagMapper;
import com.zr.blog.service.CommentService;
import com.zr.blog.service.TagService;
import com.zr.blog.vo.ArticleVo;
import com.zr.repo.domain.Project;
import com.zr.repo.service.ProjectService;
import com.zr.system.common.*;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import com.zr.system.utils.AppUtil;
import com.zr.system.utils.NlpUtil;
import com.zr.system.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.blog.mapper.ArticleMapper;
import com.zr.blog.domain.Article;
import com.zr.blog.service.ArticleService;

import javax.annotation.Resource;

/**
* @Author: 张忍
* @Date: 2020-03-30 11:46
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{

    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentService commentService;

    /**
     * 发布文章
     * @param article
     * @return
     */
    @CachePut(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl",key = "#result.id")
    @CacheEvict(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl.temp",key = "#result.author")
    @Override
    public Article saveArticle(Article article) throws IOException {
        //拿到标签
        Set<String> tag = NlpUtil.getTag(article.getTitle(), article.getContent());
        String[] split = article.getTag().split(",");
        for (String s : split) {
            tag.add(s);
        }
        //保存文章
        this.saveOrUpdate(article);
        //删除之前得文章标签关系

        //保存标签与文章id
        for (String t : tag) {
            Tag tag1 = new Tag();
            tag1.setName(t);
            //保存标签
            Tag saveTag = this.tagService.saveTag(tag1);

            //保存文章标签关系
            this.tagService.saveTagArticle(article.getId(),saveTag.getId());
        }
        //更新数据统计
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.in("available",Constant.AVAILABLE_TRUE);
        int count = this.count();
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        articleQueryWrapper.eq("author",user.getId());
        int myArticleCount = this.count(articleQueryWrapper);
        WebSocketServer.sendInfo("{'blogCount':"+count+",'myBlogCount':"+myArticleCount+"}");
        return article;
    }



    /**
     * 暂存文章
     * @param article
     * @return
     */
    @CachePut(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl.temp",key = "#result.author")
    @Override
    public Article tempArticle(Article article) {
        this.saveOrUpdate(article);
        return article;
    }

    /**
     * 加载暂存文章
     * @param uid
     * @return
     */
    @Cacheable(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl.temp",key = "#uid")
    @Override
    public Article loadTempArticle(Integer uid) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper();
        queryWrapper.eq("author",uid);
        queryWrapper.eq("available",Constant.AVAILABLE_TEMP);
        Article tempArticle = this.baseMapper.selectOne(queryWrapper);
        return tempArticle;
    }

    /**
     * 加载我的博客
     * @param articleVo
     * @return
     */
    @Override
    public DataGridView loadMyArticle(ArticleVo articleVo) {
        IPage<Article> page = new Page<>(articleVo.getPage(),articleVo.getLimit());
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author",articleVo.getAuthor());
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq(articleVo.getOpen()!=null,"open",articleVo.getOpen());
        queryWrapper.like(StringUtils.isNotBlank(articleVo.getTitle()),"title",articleVo.getTitle());
        queryWrapper.eq(StringUtils.isNotBlank(articleVo.getType()),"type",articleVo.getType());
        queryWrapper.ge(articleVo.getStartTime()!=null,"modifytime",articleVo.getStartTime());
        queryWrapper.le(articleVo.getEndTime()!=null,"modifytime ",articleVo.getEndTime());
        queryWrapper.orderByDesc("modifytime");
        this.baseMapper.selectPage(page,queryWrapper);
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Article article : page.getRecords()) {
            User author = userService.getById(article.getAuthor());
            article.setName(author.getName());
            article.setImgpath(author.getImgpath());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 删除文章
     * @param id
     */
    @CacheEvict(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl",key = "#id")
    @Override
    public void removeArticleById(Integer id) throws IOException {
        //删除用户文章标星记录
        this.articleMapper.deleteStarByAid(id);
        //递减标签文章数量
        this.tagService.decreaseArticleNumByAid(id);
        //删除文章标签关联表
        this.tagService.removeArticleTagByAid(id);
        //删除文章的评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("articleid",id);
        this.commentService.remove(queryWrapper);
        //删除文章
        this.articleMapper.deleteById(id);

        //查询文章总数量
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.in("available",Constant.AVAILABLE_TRUE);
        int count = this.count();
        //查询该用户文章数量
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        articleQueryWrapper.eq("author",user.getId());
        int myArticleCount = this.count(articleQueryWrapper);
        WebSocketServer.sendInfo("{'blogCount':"+count+",'myBlogCount':"+myArticleCount+"}");
    }

    /**
     * 加载所有博客
     * @param articleVo
     * @return
     */
    @Override
    public DataGridView loadAllArticleAvailable(ArticleVo articleVo) {

        IPage<Article> page = new Page<>(articleVo.getPage(),articleVo.getLimit());
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq("open",Constant.OPEN_TRUE);
        queryWrapper.like(StringUtils.isNotBlank(articleVo.getTitle()),"title",articleVo.getTitle());
        queryWrapper.eq(StringUtils.isNotBlank(articleVo.getType()),"type",articleVo.getType());
        queryWrapper.ge(articleVo.getStartTime()!=null,"modifytime",articleVo.getStartTime());
        queryWrapper.le(articleVo.getEndTime()!=null,"modifytime ",articleVo.getEndTime());

        Integer tagid = articleVo.getTagid();
        if (null != tagid){
            List<Integer> aids = this.tagService.queryAidsByTid(tagid);
            queryWrapper.in("id",aids);
        }

        queryWrapper.orderByDesc("modifytime");
        this.articleMapper.selectPage(page,queryWrapper);
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Article article : page.getRecords()) {
            User author = userService.getById(article.getAuthor());
            article.setName(author.getName());
            article.setImgpath(author.getImgpath());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }


    @CachePut(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl",key = "#result.id")
    @Override
    public Article updateArticle(Article article) {
        this.articleMapper.updateById(article);
        Article article1 = this.articleMapper.selectById(article.getId());
        return article1;
    }


    /**
     * 查询博客发布数量信息
     * @param id
     * @param year
     * @return
     */
    @Override
    public DataGridView queryArticleSubmit(Integer id, String year) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author",id);
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> resList = new ArrayList<>();
        try {
            Date startDate = simpleDateFormat.parse(year + "-01-01");
            Date endDate = simpleDateFormat.parse(year + "-12-31");
            queryWrapper.le("createtime",endDate);
            queryWrapper.ge("createtime",startDate);
            queryWrapper.select("createtime");
            List<Article> articles = this.baseMapper.selectList(queryWrapper);
            for (Article article : articles) {
                Date createtime = article.getCreatetime();
                String format = simpleDateFormat.format(createtime);
                resList.add(format);
            }
            //统计List数据
            Map<String, Integer> stringIntegerMap = StringUtil.frequencyOfListElements(resList);
            Object[][] array = MapUtil.toObjectArray(stringIntegerMap);
            return new DataGridView(array);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询推荐文章
     * @param uid 用户id
     * @return
     */
    @Override
    public DataGridView queryRecommendArticle(Integer uid) {
        //推荐文章集合
        Set<Article> articleSet = new HashSet<>();
        //根据用户id查询文章标签
        List<Tag> tags = this.articleMapper.selectHotByUid(uid);

        if (null!=tags&&tags.size()>0){//根据标签加载文章
            //计算总热度
            Long sumHot = 0L;
            for (Tag tag : tags) {
                sumHot+=tag.getHot();
            }

            for (Tag tag : tags) {
                //计算占比
                double v = tag.getHot()*1.0 / sumHot * 10.0;
                tag.setHot((int) v);
                //获取该标签的文章数量
                IPage<Article> page = new Page<>(1,tag.getHot());
                QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
                queryWrapper.eq("open",Constant.OPEN_TRUE);
                //通过tagid得到文章id集合
                List<Integer> aids = this.tagService.queryAidsByTid(tag.getTid());
                if (null!=aids&&aids.size()>0){
                    queryWrapper.in("id",aids);
                }
                queryWrapper.orderByDesc("modifytime");
                //查询该标签下文章
                this.articleMapper.selectPage(page,queryWrapper);
                articleSet.addAll(page.getRecords());
            }

        }
        //若用户标签太少选系统热度最高的文章//阅读量
        if (articleSet.size()<10){
            //查询已有的文章id
            List<Integer> aids = new ArrayList<>();
            for (Article article : articleSet) {
                aids.add(article.getId());
            }
            Integer count = 10-articleSet.size();
            IPage<Article> page = new Page<>(1,count);
            QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
            queryWrapper.eq("open",Constant.OPEN_TRUE);
            if (null!=aids&&aids.size()>0){
                queryWrapper.notIn("id",aids);
            }
            queryWrapper.orderByDesc("readnum");
            this.articleMapper.selectPage(page,queryWrapper);
            articleSet.addAll(page.getRecords());
        }

        //添加用户名信息
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Article article : articleSet) {
            User author = userService.getById(article.getAuthor());
            article.setName(author.getName());
        }
        return new DataGridView(articleSet);
    }

    /**
     * 保存用户标签热度
     * @param uid
     * @param aid
     */
    @Override
    public void saveUserTag(Integer uid, Integer aid) {
        //根据文章id查询标签
        List<Tag> tags = this.tagService.getTagByAid(aid);
        if (null!=tags&&tags.size()>0){
            for (Tag tag : tags) {
                //查询是否存在
                Integer count = this.articleMapper.selectHotByUidAndTid(uid,tag.getId());
                if (count>0){//存在更新，热度加1
                    this.articleMapper.incrementHotByUidAndTid(uid,tag.getId());
                }else {//不存在插入，热度默认为1
                    this.articleMapper.insertHotByUidAndTid(uid,tag.getId(),Constant.DEFAULT_ARTICLEHOT);
                }
            }
        }

    }

    /**
     * 加载热门文章
     * @param articleVo
     * @return
     */
    @Override
    public DataGridView loadHotArticle(ArticleVo articleVo) {
        IPage<Article> page = new Page<>(articleVo.getPage(),articleVo.getLimit());
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq("open",Constant.OPEN_TRUE);
        queryWrapper.orderByDesc("readnum");
        this.articleMapper.selectPage(page,queryWrapper);
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Article article : page.getRecords()) {
            User author = userService.getById(article.getAuthor());
            article.setName(author.getName());
            article.setImgpath(author.getImgpath());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 加载所有博客包括不可用
     * @param articleVo
     * @return
     */
    @Override
    public DataGridView loadAllArticleIgnoreAvailable(ArticleVo articleVo) {
        IPage<Article> page = new Page<>(articleVo.getPage(),articleVo.getLimit());
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(articleVo.getTitle()),"title",articleVo.getTitle());

        if (StringUtils.isNotBlank(articleVo.getMasterName())){
            List<Integer> uids = new ArrayList<>();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.like("name",articleVo.getMasterName());
            UserService userService = AppUtil.getContext().getBean(UserService.class);
            List<User> users = userService.list(queryWrapper1);
            if (null!=users&&users.size()>0){
                for (User user : users) {
                    uids.add(user.getId());
                }
                queryWrapper.in("author",uids);
            }else {
                queryWrapper.in("author",-1);
            }
        }
        queryWrapper.eq(null!=articleVo.getAvailable(),"available",articleVo.getAvailable());
        queryWrapper.orderByDesc("modifytime");
        this.articleMapper.selectPage(page,queryWrapper);
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Article article : page.getRecords()) {
            User author = userService.getById(article.getAuthor());
            article.setName(author.getName());
            article.setImgpath(author.getImgpath());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 标星文章
     * @param uid
     * @param aid
     */
    @Override
    public void starArticle(Integer uid, Integer aid) {
        this.articleMapper.insertStar(uid,aid);
    }

    /**
     * 取消标星文章
     * @param uid
     * @param aid
     */
    @Override
    public void unStarArticle(Integer uid, Integer aid) {
        this.articleMapper.deleteStar(uid,aid);
    }

    /**
     * 查询标星状态
     * @param uid
     * @param aid
     * @return
     */
    @Override
    public ResultObj queryStar(Integer uid, Integer aid) {
        Integer count = this.articleMapper.queryStar(uid, aid);
        return count==1?ResultObj.STAR_TRUE:ResultObj.STAR_FALSE;
    }

    /**
     * 查询标星文章
     * @param articleVo
     * @return
     */
    @Override
    public DataGridView loadStarArticle(ArticleVo articleVo) {
        IPage<Article> page = new Page<>(articleVo.getPage(),articleVo.getLimit());
        Integer uid = articleVo.getAuthor();
        List<Integer> aids = this.articleMapper.queryAidsByUid(uid);

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (null!=aids&&aids.size()>0){
            queryWrapper.in("id",aids);
            queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
            queryWrapper.eq("open",Constant.OPEN_TRUE);
            queryWrapper.like(StringUtils.isNotBlank(articleVo.getTitle()),"title",articleVo.getTitle());
            this.articleMapper.selectPage(page,queryWrapper);
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 查询文章标星数
     * @param aid
     * @return
     */
    @Override
    public Integer queryStarnumById(Integer aid) {
        return this.articleMapper.queryStarNumByAid(aid);
    }


    /**
     * 查询文章详细
     * @param id
     * @return
     */
    @Cacheable(cacheNames = "com.zr.blog.service.impl.ArticleServiceImpl",key = "#id")
    @Override
    public Article getById(Serializable id) {
        Article article = super.getById(id);
        return article;
    }
}

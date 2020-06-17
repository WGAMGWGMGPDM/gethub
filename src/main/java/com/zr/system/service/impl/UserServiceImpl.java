package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.blog.domain.Article;
import com.zr.blog.domain.Comment;
import com.zr.blog.mapper.TagMapper;
import com.zr.blog.service.ArticleService;
import com.zr.blog.service.CommentService;
import com.zr.repo.domain.Repository;
import com.zr.repo.service.RepositoryService;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;
import com.zr.system.common.WebSocketServer;
import com.zr.system.mapper.RoleMapper;
import com.zr.system.service.UseraddrService;
import com.zr.system.vo.UserVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.mapper.UserMapper;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private Log log = LogFactory.getLog(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UseraddrService useraddrService;


    @Override
    public User queryUserByLoginName(String loginname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(loginname)){
            log.error("登录名不能为空");
            return null;
        }
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq("loginname",loginname);
        User user = this.userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    public User queryUserByOpenId(String openId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(openId)){
            log.error("登录名不能为空");
            return null;
        }
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq("openid",openId);
        User user = this.userMapper.selectOne(queryWrapper);
        return user;
    }

    @CachePut(cacheNames = "com.zr.system.service.impl.UserServiceImpl",key = "#result.id")
    @Override
    public User queryUserByLoginNameIgnoreAvailable(String loginname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(loginname)){
            log.error("登录名不能为空");
            return null;
        }
        queryWrapper.eq("loginname",loginname);
        User user = this.userMapper.selectOne(queryWrapper);
        return user;
    }

    /**
     * 用户管理加载所有用户
     * @param userVo
     * @return
     */
    @Override
    public DataGridView queryAllUser(UserVo userVo) {
        IPage<User> page = new Page<>(userVo.getPage(),userVo.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("type", Constant.USER_TYPE_NORMAL,Constant.USER_TYPE_OFFICIAL);
        queryWrapper.like(StringUtils.isNotBlank(userVo.getName()),"name",userVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(userVo.getAddress()),"address",userVo.getAddress());
        queryWrapper.like(null!=userVo.getId(),"id",userVo.getId());
        this.userMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @CachePut(cacheNames = "com.zr.system.service.impl.UserServiceImpl",key = "#result.id")
    @Override
    public User updateUser(User user) {
        this.userMapper.updateById(user);
        User select = this.userMapper.selectById(user.getId());
        return select;
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @CachePut(cacheNames = "com.zr.system.service.impl.UserServiceImpl",key = "#result.id")
    @Override
    public User saveUser(User user) throws IOException {
        this.userMapper.insert(user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("type",Constant.USER_TYPE_NORMAL,Constant.USER_TYPE_OFFICIAL);
        int count = this.count(queryWrapper);
        WebSocketServer.sendInfo("{'userCount':"+count+"}");
        return user;
    }

    /**
     * 加载最大排序码
     * @return
     */
    @Override
    public Integer queryUserMaxOrderNum() {
        return this.userMapper.queryUserMaxOrderNum();
    }

    /**
     * 激活用户，根据激活码查询用户是否注册
     * @param cdkey
     * @return
     */
    @Override
    public User queryUserByCdkey(String cdkey) {
        UserMapper userMapper = this.baseMapper;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(cdkey)){
            log.error("激活码不能为空");
            return null;
        }
        queryWrapper.eq("cdkey",cdkey);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    /**
     * 保存用户的角色
     * @param uid
     * @param rids
     */
    @Override
    public void savaUserRole(Integer uid, Integer[] rids) {
        //根据用户ID删除角色用户中间表的数据
        roleMapper.deleteRoleUserByUid(uid);
        if (null!=rids&&rids.length>0){
            for (Integer rid : rids) {
                this.userMapper.saveUserRole(uid,rid);
            }
        }

    }

    /**
     * 关注用户
     * @param uid
     * @param fid
     */
    @Override
    public void followUser(Integer uid, Integer fid) throws IOException {
        this.userMapper.insertFollow(uid,fid);
        int myFollowCount = this.userMapper.queryFidsByUid(uid).size();
        WebSocketServer.sendInfo("{'myFollowCount':"+myFollowCount+"}");
    }

    /**
     * 取关用户
     * @param uid
     * @param fid
     */
    @Override
    public void unFollowUser(Integer uid, Integer fid) throws IOException {
        this.userMapper.deleteFollow(uid,fid);
        int myFollowCount = this.userMapper.queryFidsByUid(uid).size();
        WebSocketServer.sendInfo("{'myFollowCount':"+myFollowCount+"}");
    }

    /**
     * 查询关注状态
     * @param uid
     * @param fid
     * @return
     */
    @Override
    public ResultObj queryFollow(Integer uid, Integer fid) {
        Integer count = this.userMapper.queryFollow(uid, fid);
        return count==1?ResultObj.FOLLOW_TRUE:ResultObj.FOLLOW_FALSE;
    }

    /**
     * 查询关注用户
     * @param userVo
     * @return
     */
    @Override
    public DataGridView loadFollowUser(UserVo userVo) {
        IPage<User> page = new Page<>(userVo.getPage(),userVo.getLimit());
        Integer uid = userVo.getId();
        List<Integer> fids = this.userMapper.queryFidsByUid(uid);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (null!=fids&&fids.size()>0){
            queryWrapper.in("id",fids);
            queryWrapper.like(StringUtils.isNotBlank(userVo.getName()),"name",userVo.getName());
            this.userMapper.selectPage(page,queryWrapper);
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 查询关注用户数量
     * @param uid
     * @return
     */
    @Override
    public Integer queryFollowCountByUid(Integer uid) {
        return this.userMapper.queryFidsByUid(uid).size();
    }


    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = "com.zr.system.service.impl.UserServiceImpl",key = "#id")
    @SneakyThrows
    @Override
    public boolean removeById(Serializable id) {
        //自减省份分布用户数量
        User user = this.userMapper.selectById(id);
        this.useraddrService.decrementAddrNum(user.getAddress().substring(0,2));
        //删除用户关注表信息
        this.userMapper.deleteFollowByUid(id);
        this.userMapper.deleteFollowByFid(id);
        //删除用户项目标星信息
        this.userMapper.deleteProjectStarByUid(id);
        //删除用户博客标星信息
        this.userMapper.deleteArticleStarByUid(id);
        //根据用户ID删除角色与用户之间的关系
        this.roleMapper.deleteRoleUserByUid(id);
        //根据用户ID删除用户标签热度关系
        this.tagMapper.deleteUserTagHotByUid(id);
        //删除用户相关项目、博客、评论
        QueryWrapper<Repository> repositoryQueryWrapperqueryWrapper = new QueryWrapper<>();
        repositoryQueryWrapperqueryWrapper.eq("master",id);
        List<Repository> repositoryList = this.repositoryService.list(repositoryQueryWrapperqueryWrapper);
        for (Repository repository : repositoryList) {
            this.repositoryService.removeById(repository.getId());
        }
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("author",id);
        List<Article> articleList = this.articleService.list(articleQueryWrapper);
        for (Article article : articleList) {
            this.articleService.removeArticleById(article.getId());
        }

        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("comment_user",id);
        List<Comment> commentList = this.commentService.list(commentQueryWrapper);
        for (Comment comment : commentList) {
            //删除评论和子评论
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pid",comment.getId());
            List<Comment> comments = this.commentService.list(queryWrapper);
            List<Integer> ids = new ArrayList<>();
            ids.add(comment.getId());
            if (null!=comments&&comments.size()>0){
                for (Comment comment1 : comments) {
                    ids.add(comment1.getId());
                }
            }
            this.commentService.removeByIds(ids);

            //自减评论数
            Article article = this.articleService.getById(comment.getArticleid());
            article.setCommentnum(article.getCommentnum()-ids.size());
            this.articleService.updateArticle(article);
        }


        boolean flag = super.removeById(id);
        if (flag){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("type",Constant.USER_TYPE_NORMAL,Constant.USER_TYPE_OFFICIAL);
            int count = this.count(queryWrapper);
            WebSocketServer.sendInfo("{'userCount':"+count+"}");
        }
        return flag;
    }



    /**
     * 批量删除用户
     * @param idList
     * @return
     */
    @CacheEvict(cacheNames = "com.zr.system.service.impl.UserServiceImpl",allEntries = true)
    @SneakyThrows
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            //自减省份分布用户数量
            User user = this.userMapper.selectById(id);
            this.useraddrService.decrementAddrNum(user.getAddress().substring(0,2));

            //删除用户关注表信息

            //删除用户标星信息

            //根据角色ID删除角色与用户之间的关系
            this.roleMapper.deleteRoleUserByUid(id);
            //根据用户ID删除用户标签热度关系
            this.tagMapper.deleteUserTagHotByUid(id);
            //删除用户相关项目、博客、评论
            QueryWrapper<Repository> repositoryQueryWrapperqueryWrapper = new QueryWrapper<>();
            repositoryQueryWrapperqueryWrapper.eq("master",id);
            List<Repository> repositoryList = this.repositoryService.list(repositoryQueryWrapperqueryWrapper);
            for (Repository repository : repositoryList) {
                this.repositoryService.removeById(repository.getId());
            }
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.eq("author",id);
            List<Article> articleList = this.articleService.list(articleQueryWrapper);
            for (Article article : articleList) {
                this.articleService.removeArticleById(article.getId());
            }

            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("comment_user",id);
            List<Comment> commentList = this.commentService.list(commentQueryWrapper);
            for (Comment comment : commentList) {
                //删除评论和子评论
                QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("pid",comment.getId());
                List<Comment> comments = this.commentService.list(queryWrapper);
                List<Integer> ids = new ArrayList<>();
                ids.add(comment.getId());
                if (null!=comments&&comments.size()>0){
                    for (Comment comment1 : comments) {
                        ids.add(comment1.getId());
                    }
                }
                this.commentService.removeByIds(ids);

                //自减评论数
                Article article = this.articleService.getById(comment.getArticleid());
                article.setCommentnum(article.getCommentnum()-ids.size());
                this.articleService.updateArticle(article);
            }

        }
        boolean flag = super.removeByIds(idList);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("type",Constant.USER_TYPE_NORMAL,Constant.USER_TYPE_OFFICIAL);
        int count = this.count(queryWrapper);
        WebSocketServer.sendInfo("{'userCount':"+count+"}");
        return flag;
    }

    @Cacheable(cacheNames = "com.zr.system.service.impl.UserServiceImpl",key = "#id")
    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }
}


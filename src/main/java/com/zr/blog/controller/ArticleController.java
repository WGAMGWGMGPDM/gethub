package com.zr.blog.controller;

import com.zr.blog.domain.Article;
import com.zr.blog.domain.Tag;
import com.zr.blog.service.ArticleService;
import com.zr.blog.service.TagService;
import com.zr.blog.vo.ArticleVo;
import com.zr.repo.vo.ProjectVo;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;

import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 11:47
 */
@RestController
@RequestMapping("api/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;

    /**
     * 添加文章
     * @param article
     * @return
     */
    @RequestMapping("addArticle")
    public ResultObj addArticle(Article article){
        try {
            Subject subject = SecurityUtils.getSubject();
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            article.setAuthor(activeUser.getUser().getId());
            article.setCreatetime(new Date());
            article.setModifytime(new Date());
            article.setStarnum(Constant.DEFAULT_STAR);
            article.setAvailable(Constant.AVAILABLE_TRUE);
            if(null==article.getReadnum()){
                article.setReadnum(Constant.DEFAULT_READNUM);
                article.setCommentnum(Constant.DEFAULT_COMMENTNUM);
            }
            this.articleService.saveArticle(article);
            return ResultObj.RELEASE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.RELEASE_ERROR;
        }
    }

    /**
     * 暂存文章
     * @param article
     * @return
     */
    @RequestMapping("tempArticle")
    public ResultObj tempArticle(Article article){
        try {
            Subject subject = SecurityUtils.getSubject();
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            article.setAuthor(activeUser.getUser().getId());
            article.setModifytime(new Date());
            article.setAvailable(Constant.AVAILABLE_TEMP);
            this.articleService.tempArticle(article);
            return ResultObj.TEMP_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.TEMP_ERROR;
        }
    }

    /**
     * 加载暂存文章
     *
     * @return
     */
    @RequestMapping("loadTempArticle")
    public Object loadTempArticle(){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        return this.articleService.loadTempArticle(activeUser.getUser().getId());
    }

    /**
     * 加载文章详细
     *
     * @return
     */
    @RequestMapping("loadArticleInfo")
    public Object loadArticleInfo(Integer aid,Integer uid){
        //修改阅读量
        Article article = this.articleService.getById(aid);
        article.setReadnum(article.getReadnum()+1);
        this.articleService.updateArticle(article);
        //修改文章热度,如果是游客，不需要此步骤
        if(null!=uid){
            this.articleService.saveUserTag(uid,aid);
        }
        //查询标星数
        Integer starnum = this.articleService.queryStarnumById(aid);
        article.setStarnum(starnum);
        //查询用户名
        User user = userService.getById(article.getAuthor());
        article.setName(user.getName());
        //查询标签
        List<Tag> tags = this.tagService.getTagByAid(article.getId());
        List<String> tagList = new ArrayList<>();
        if (null!=tags&&tags.size()>0){
            for (Tag tag : tags) {
                tagList.add(tag.getName());
            }
            article.setTags(tagList);
        }
        return article;
    }

    /**
     * 加载我的博客
     *
     * @return
     */
    @RequestMapping("loadMyArticle")
    public Object loadMyArticle(ArticleVo articleVo){
        return this.articleService.loadMyArticle(articleVo);
    }


    /**
     * 删除
     */
    @RequestMapping("/deleteArticle")
    public ResultObj deleteArticle(Integer id){
        try {
            this.articleService.removeArticleById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 加载所有可用博客
     *
     * @return
     */
    @RequestMapping("loadAllArticle")
    public Object loadAllArticle(ArticleVo articleVo){
        return this.articleService.loadAllArticleAvailable(articleVo);
    }

    /**
     * 修改博客
     *
     * @return
     */
    @RequestMapping("updateArticle")
    public Object updateArticle(Article article){
        try {
            this.articleService.updateArticle(article);
            return ResultObj.UPDATE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }


    /**
     * 加载所有博客
     *
     * @return
     */
    @RequestMapping("loadAllArticleIgnoreAvailable")
    public Object loadAllArticleIgnoreAvailable(ArticleVo articleVo){
        return this.articleService.loadAllArticleIgnoreAvailable(articleVo);
    }

    /**
     * 加载热门博客
     *
     * @return
     */
    @RequestMapping("loadHotArticle")
    public Object loadHotArticle(ArticleVo articleVo){
        return this.articleService.loadHotArticle(articleVo);
    }


    /**
     * 标星博客
     * @return
     */
    @RequestMapping("starArticle")
    public ResultObj starArticle(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.articleService.starArticle(uid,id);
            return ResultObj.STAR_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.STAR_ERROR;
        }
    }

    /**
     * 取消标星博客
     * @return
     */
    @RequestMapping("unStarArticle")
    public ResultObj unStarArticle(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.articleService.unStarArticle(uid,id);
            return ResultObj.UNSTAR_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UNSTAR_ERROR;
        }
    }

    /**
     * 查询博客是否已标星
     * @return
     */
    @RequestMapping("queryStar")
    public ResultObj queryStar(Integer id){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer uid = activeUser.getUser().getId();
        return this.articleService.queryStar(uid,id);
    }

    /**
     * 查询标星博客
     * @return
     */
    @GetMapping("loadStarArticle")
    public Object loadStarArticle(ArticleVo articleVo){
        return this.articleService.loadStarArticle(articleVo);
    }


}

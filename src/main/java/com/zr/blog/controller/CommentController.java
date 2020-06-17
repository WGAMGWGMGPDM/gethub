package com.zr.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.blog.domain.Article;
import com.zr.blog.domain.Comment;
import com.zr.blog.service.ArticleService;
import com.zr.blog.service.CommentService;
import com.zr.system.common.ResultObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-04-01 17:34
 */
@RestController
@RequestMapping("api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ArticleService articleService;

    /**
     * 根据文章id加载评论
     * @param articleid
     * @return
     */
    @RequestMapping("loadCommentByAid")
    public Object loadCommentByAid(Integer articleid){
        return this.commentService.loadCommentByAid(articleid);
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @RequestMapping("addComment")
    public ResultObj addComment(Comment comment){
        try {
            comment.setCommenttime(new Date());
            this.commentService.saveComment(comment);

            //自增评论数
            Article article = this.articleService.getById(comment.getArticleid());
            article.setCommentnum(article.getCommentnum()+1);
            this.articleService.updateArticle(article);
            return ResultObj.SEND_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.SEND_ERROR;
        }
    }

    /**
     * 删除评论
     */
    @RequestMapping("/deleteComment")
    public ResultObj deleteComment(Integer id){
        try {
            //删除评论和子评论
            Comment comment = this.commentService.getById(id);
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pid",id);
            List<Comment> comments = this.commentService.list(queryWrapper);
            List<Integer> ids = new ArrayList<>();
            ids.add(id);
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
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}

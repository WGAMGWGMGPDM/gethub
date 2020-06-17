package com.zr.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.blog.common.CommentTreeNode;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import com.zr.system.utils.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.blog.domain.Comment;
import com.zr.blog.mapper.CommentMapper;
import com.zr.blog.service.CommentService;
/**
* @Author: 张忍
* @Date: 2020-04-01 15:45
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 根据文章id加载评论
     * @param aid
     * @return
     */
    @Override
    public DataGridView loadCommentByAid(Integer aid) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(aid!=null,"articleid",aid);
        queryWrapper.orderByAsc("commenttime");
        List<Comment> comments = this.commentMapper.selectList(queryWrapper);

        //将comments渲染为commentTreeNodeList
        List<CommentTreeNode> treeNodes = new ArrayList<>();
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Comment comment : comments) {
            User commentUser = userService.getById(comment.getCommentUser());
            User atUser = userService.getById(comment.getAtUser());
            treeNodes.add(new CommentTreeNode(comment.getId(),comment.getPid(),comment.getCommentUser(),comment.getAtUser(),atUser.getName(),commentUser.getName(),comment.getCommenttime(),commentUser.getImgpath(),comment.getContent()));
        }
        //将commentTreeNodeList渲染为结构化的树
        List<CommentTreeNode> nodes = CommentTreeNode.CommentTreeNodeBuilder.build(treeNodes,0);
        return new DataGridView(Long.valueOf(nodes.size()),nodes);
    }

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @CachePut(cacheNames = "com.zr.blog.service.impl.CommentServiceImpl",key = "#result.id")
    @Override
    public Comment saveComment(Comment comment) {
        this.commentMapper.insert(comment);
        return comment;
    }

    @CacheEvict(cacheNames = "com.zr.blog.service.impl.CommentServiceImpl",allEntries = true)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }
}

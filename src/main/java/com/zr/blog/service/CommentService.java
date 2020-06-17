package com.zr.blog.service;

import com.zr.blog.domain.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.common.DataGridView;

/**
* @Author: 张忍
* @Date: 2020-04-01 15:45
*/
public interface CommentService extends IService<Comment>{


        DataGridView loadCommentByAid(Integer aid);

    Comment saveComment(Comment comment);
}

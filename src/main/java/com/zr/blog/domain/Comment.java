package com.zr.blog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
* @Author: 张忍
* @Date: 2020-04-01 15:45
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blog_comment")
public class Comment implements Serializable {
    /**
     * 评论主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评论人
     */
    @TableField(value = "comment_user")
    private Integer commentUser;

    /**
     * 回复人
     */
    @TableField(value = "at_user")
    private Integer atUser;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 评论时间
     */
    @TableField(value = "commenttime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commenttime;

    /**
     * 父级id
     */
    @TableField(value = "pid")
    private Integer pid;

    /**
     * 文章id
     */
    @TableField(value = "articleid")
    private Integer articleid;

    /**
     * 评论人姓名
     */
    @TableField(exist = false)
    private String commentName;

    /**
     * 评论人头像
     */
    @TableField(exist = false)
    private String commentImg;

    /**
     * 被评论人姓名
     */
    @TableField(exist = false)
    private String atName;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_COMMENT_USER = "comment_user";

    public static final String COL_AT_USER = "at_user";

    public static final String COL_CONTENT = "content";

    public static final String COL_COMMENTTIME = "commenttime";

    public static final String COL_PID = "pid";

    public static final String COL_ARTICLEID = "articleid";
}
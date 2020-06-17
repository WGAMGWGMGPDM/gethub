package com.zr.blog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Author: 张忍
* @Date: 2020-03-30 11:46
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blog_article")
public class Article implements Serializable {
    /**
     * 文章主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 文章内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 修改时间
     */
    @TableField(value = "modifytime")
    private Date modifytime;

    /**
     * 作者
     */
    @TableField(value = "author")
    private Integer author;

    /**
     * 文章类型【原创1转载0】
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 是否私密【私密0公开1】
     */
    @TableField(value = "open")
    private Integer open;

    /**
     * 是否可用【可用1不可用0】
     */
    @TableField(value = "available")
    private Integer available;

    /**
     * 阅读量
     */
    @TableField(value = "readnum")
    private Integer readnum;

    /**
     * 评论数
     */
    @TableField(value = "commentnum")
    private Integer commentnum;

    /**
     * 创建时间
     */
    @TableField(value = "createtime")
    private Date createtime;

    /**
     * 标星数
     */
    @TableField(exist = false)
    private Integer starnum;

    /**
     * 标签
     */
    @TableField(exist = false)
    private String tag;

    /**
     * 标签数组
     */
    @TableField(exist = false)
    private List<String> tags;

    /**
     * 作者名
     */
    @TableField(exist = false)
    private String name;

    /**
     * 作者头像
     */
    @TableField(exist = false)
    private String imgpath;



    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_TITLE = "title";

    public static final String COL_CONTENT = "content";

    public static final String COL_MODIFYTIME = "modifytime";

    public static final String COL_AUTHOR = "author";

    public static final String COL_TYPE = "type";

    public static final String COL_OPEN = "open";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_READNUM = "readnum";

    public static final String COL_COMMENTNUM = "commentnum";

    public static final String COL_STARNUM = "starnum";

}
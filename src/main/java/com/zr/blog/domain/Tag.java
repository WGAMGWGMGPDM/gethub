package com.zr.blog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 15:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blog_tag")
public class Tag implements Serializable {
    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标签名
     */
    @TableId(value = "name", type = IdType.INPUT)
    private String name;

    /**
     * 标签ID
     */
    @TableId(value = "articlenum")
    private Integer articlenum;

    /**
     * 热度
     */
    @TableField(exist = false)
    private Integer hot;
    /**
     * 中间表ID
     */
    @TableField(exist = false)
    private Integer tid;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_ARTICLENUM = "articlenum";

}
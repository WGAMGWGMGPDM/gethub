package com.zr.repo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 张忍
 * @Date: 2020-03-19 23:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "repo_repository")
public class Repository implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 仓库名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 类型【是否私有  0私有1公开】
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 拥有人id
     */
    @TableField(value = "master")
    private Integer master;

    /**
     * 创建时间
     */
    @TableField(value = "createtime")
    private Date createtime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 排序码
     */
    @TableField(value = "pronum")
    private Integer pronum;

    /**
     * 是否可用
     */
    @TableField(value = "available")
    private Integer available;

    /**
     * 最近修改时间
     */
    @TableField(value = "modifytime")
    private Date modifytime;

    /**
     * 所属人姓名
     */
    @TableField(exist = false)
    private String userName;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE = "type";

    public static final String COL_MASTER = "master";

    public static final String COL_CREATETIME = "createtime";

    public static final String COL_REMARK = "remark";

    public static final String COL_PRONUM = "pronum";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_MODIFYTIME = "modifytime";
}
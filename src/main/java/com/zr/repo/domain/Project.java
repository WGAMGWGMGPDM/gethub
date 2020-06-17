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
* @Date: 2020-03-20 14:23
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "repo_project")
public class Project implements Serializable {
    /**
     * 项目id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 所属仓库
     */
    @TableField(value = "repo")
    private Integer repo;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "createtime")
    private Date createtime;

    /**
     * 修改时间
     */
    @TableField(value = "modifytime")
    private Date modifytime;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private String version;

    /**
     * 项目地址
     */
    @TableField(value = "projectpath")
    private String projectpath;

    /**
     * 是否可用
     */
    @TableField(value = "available")
    private Integer available;

    /**
     * 排序码
     */
    @TableField(value = "faid")
    private Integer faid;

    /**
     * 语言类型
     */
    @TableField(value = "languge")
    private Integer languge;

    /**
     * 可选参数
     */
    @TableField(exist = false)
    private String opt = "";

    /**
     * 拥有人id
     */
    @TableField(value = "master")
    private Integer master;

    /**
     * 拥有人姓名
     */
    @TableField(exist = false)
    private String masterName;
//    /**
//     * 拥有人ID
//     */
//    @TableField(exist = false)
//    private Integer masterId;

    /**
     * 语言标签
     */
    @TableField(exist = false)
    private String tag;

    /**
     * 仓库名
     */
    @TableField(exist = false)
    private String repoName;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_REPO = "repo";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATETIME = "createtime";

    public static final String COL_MODIFYTIME = "modifytime";

    public static final String COL_VERSION = "version";

    public static final String COL_PROJECTPATH = "projectpath";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_FAID = "faid";

    public static final String COL_LANGUGE = "languge";

    public static final String COL_README = "master";

}
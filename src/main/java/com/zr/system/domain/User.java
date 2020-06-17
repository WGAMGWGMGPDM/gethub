package com.zr.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class User implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户姓名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 登录名
     */
    @TableField(value = "loginname")
    private String loginname;

    /**
     * 密码
     */
    @TableField(value = "pwd")
    @JsonIgnore //生成json时不序列化
    private String pwd;

    /**
     * 性别
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 年龄
     */
    @TableField(value = "age")
    private Integer age;

    /**
     * qq号码
     */
    @TableField(value = "qq")
    private Integer qq;

    /**
     * 手机号码
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 注册时间
     */
    @TableField(value = "registertime")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date registertime;

    /**
     * 盐
     */
    @TableField(value = "salt")
    @JsonIgnore //生成json时不序列化
    private String salt;

    /**
     * 头像地址
     */
    @TableField(value = "imgpath")
    private String imgpath;

    /**
     * 用户类型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 排序码
     */
    @TableField(value = "ordernum")
    private Integer ordernum;

    /**
     * 是否可用
     */
    @TableField(value = "available")
    private Integer available;

    /**
     * openid
     */
    @TableField(value = "openid")
    private String openid;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_LOGINNAME = "loginname";

    public static final String COL_PWD = "pwd";

    public static final String COL_SEX = "sex";

    public static final String COL_ADDRESS = "address";

    public static final String COL_AGE = "age";

    public static final String COL_QQ = "qq";

    public static final String COL_PHONE = "phone";

    public static final String COL_EMAIL = "email";

    public static final String COL_REMARK = "remark";

    public static final String COL_REGISTERTIME = "registertime";

    public static final String COL_SALT = "salt";

    public static final String COL_IMGPATH = "imgpath";

    public static final String COL_TYPE = "type";

    public static final String COL_ORDERNUM = "ordernum";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_OPENID = "openid";

}
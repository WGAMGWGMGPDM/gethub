package com.zr.system.domain;

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
 * @Date: 2020-04-20 23:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_complaint")
public class Complaint implements Serializable {
    /**
     * 投诉id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 投诉人id
     */
    @TableField(value = "uid")
    private Integer uid;

    /**
     * 被投诉的对象id
     */
    @TableField(value = "complaintid")
    private Integer complaintid;

    /**
     * 投诉原因
     */
    @TableField(value = "complaintreason")
    private String complaintreason;

    /**
     * 投诉对象【项目:1或博客:0】
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 投诉时间
     */
    @TableField(value = "complainttime")
    private Date complainttime;

    /**
     * 处理时间
     */
    @TableField(value = "solvedtime")
    private Date solvedtime;

    /**
     * 处理备注
     */
    @TableField(value = "solvedremark")
    private String solvedremark;

    /**
     * 投诉人姓名
     */
    @TableField(value = "uname")
    private String uname;

    /**
     * 被投诉的对象名
     */
    @TableField(value = "complaintname")
    private String complaintname;

    /**
     * 处理人id
     */
    @TableField(value = "solvedid")
    private Integer solvedid;

    /**
     * 处理人姓名
     */
    @TableField(exist = false)
    private String solvedname;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_UID = "uid";

    public static final String COL_COMPLAINTID = "complaintid";

    public static final String COL_COMPLAINTREASON = "complaintreason";

    public static final String COL_TYPE = "type";

    public static final String COL_COMPLAINTTIME = "complainttime";

    public static final String COL_SOLVEDTIME = "solvedtime";

    public static final String COL_SOLVEDREMARK = "solvedremark";

    public static final String COL_UNAME = "uname";

    public static final String COL_COMPLAINTNAME = "complaintname";

    public static final String COL_SOLVEDID = "solvedid";
}
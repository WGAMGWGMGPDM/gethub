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
* @Date: 2020-03-14 13:23
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role")
public class Role implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 是否可用
     */
    @TableField(value = "available")
    private Integer available;

    @TableField(value = "createtime")
    private Date createtime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_REMARK = "remark";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_CREATETIME = "createtime";
}
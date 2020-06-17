package com.zr.system.domain;

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
* @Date: 2020-05-19 19:18
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_useraddr")
public class Useraddr implements Serializable {
    /**
     * 用户地址
     */
    @TableField(value = "addr")
    private String addr;

    /**
     * 用户数量
     */
    @TableField(value = "num")
    private Integer num;

    private static final long serialVersionUID = 1L;

    public static final String COL_ADDR = "addr";

    public static final String COL_NUM = "num";
}
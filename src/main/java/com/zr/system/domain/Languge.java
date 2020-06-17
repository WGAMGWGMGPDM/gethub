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
* @Date: 2020-03-29 15:21
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_languge")
public class Languge implements Serializable {
    /**
     * 语言id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 语言名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 项目数量
     */
    @TableField(value = "num")
    private Integer num;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_NUM = "num";
}
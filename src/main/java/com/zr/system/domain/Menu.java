package com.zr.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_menu")
public class Menu implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级编号
     */
    @TableField(value = "pid")
    private Integer pid;

    /**
     * 类型【topmenu/leftmenu/permission】
     */
    @TableField(value = "type")
    private String type;

    /**
     * 类型代码
     */
    @TableField(value = "typecode")
    private String typecode;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 地址
     */
    @TableField(value = "href")
    private String href;

    /**
     * 是否展开 【0不展开1展开】
是否展开【0不展开1展开】
     */
    @TableField(value = "spread")
    private Integer spread;


    /**
     * 排序码
     */
    @TableField(value = "ordernum")
    private Integer ordernum;

    /**
     * 可用状态
     */
    @TableField(value = "available")
    private Integer available;

    /**
     * TARGET
     */
    @TableField(value = "target")
    private String target;




    /**
     * 构造权限
     * @param id
     * @param pid
     * @param type
     * @param typecode
     * @param title
     */
    public Menu(Integer id, Integer pid, String type, String typecode, String title,Integer ordernum,Integer available) {
        this.id = id;
        this.pid = pid;
        this.type = type;
        this.typecode = typecode;
        this.title = title;
        this.ordernum = ordernum;
        this.available = available;
    }

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PID = "pid";

    public static final String COL_TYPE = "type";

    public static final String COL_TYPECODE = "typecode";

    public static final String COL_TITLE = "title";

    public static final String COL_ICON = "icon";

    public static final String COL_HREF = "href";

    public static final String COL_SPREAD = "spread";

    public static final String COL_ORDERNUM = "ordernum";

    public static final String COL_AVAILABLE = "available";

    public static final String COL_TARGET = "target";

}
package com.zr.system.common;



public class Constant {

    /**
     * 用户类型
     */
    public static final Integer USER_TYPE_SUPER = 0;
    public static final Integer USER_TYPE_NORMAL = 1;
    public static final Integer USER_TYPE_OFFICIAL = 2;//高级用户，开通博客功能

    /**
     * 可用类型
     */
    public static final Integer AVAILABLE_TRUE = 1;
    public static final Integer AVAILABLE_FALSE = 0;
    public static final Integer AVAILABLE_TEMP = 2;
    public static final Integer AVAILABLE_ARCHIVE = 3;

    /**
     * 是否公开
     */
    public static final Object OPEN_TRUE = 1;
    public static final Object OPEN_FALSE = 0;

    /**
     * 权限类型
     */
    public static final String MENU_TYPE_TOP = "topmenu";
    public static final String MENU_TYPE_LEFT = "leftmenu";
    public static final String MENU_TYPE_PERMISSION = "permission";

    /**
     * 是否展开
     */
    public static final Integer SPREAD_TRUE = 1;
    public static final Integer SPREAD_FALSE = 0;

    /**
     * 默认备注与默认图片
     */
    public static final String DEFAULT_REMARK = "这个人是条懒狗~~~";
    public static final String DEFAULT_IMG = "";


    public static final Integer[] USER = {2};

    /**
     * 邮件类型
     */
    public static final String SEND_ACTIVATE = "activate";
    public static final String SEND_VALIDITY = "validity";

    /**
     * 默认项目版本
     */
    public static final String DEFAULT_VERSION = "0.0.0";

    public static final String OPT_README = "readme";

    /**
     * 文件类型
     */
    public static final Integer FILE_TYPE_FOLDER = 0;
    public static final Integer FILE_TYPE_NORMAL = 1;
    public static final Integer FILE_TYPE_README = 2;
    public static final Integer FILE_TYPE_IMG = 3;
    public static final Integer FILE_TYPE_AUDIO = 4;
    public static final Integer FILE_TYPE_VIDEO = 5;
    /**
     * 根目录
     */
    public static final Integer FOLDER_ROOT = 0;
    /**
     * 默认阅读量与评论数
     */
    public static final Integer DEFAULT_READNUM = 0;
    public static final Integer DEFAULT_COMMENTNUM = 0;
    /**
     * 默认文章热度
     */
    public static final Integer DEFAULT_ARTICLEHOT = 1;
    /**
     * 默认项目数
     */
    public static final Integer DEFAULT_PROJECT_NUM = 0;

    public static final String DEFAULT_FILE_README = "http://101.200.130.83/group1/M00/00/00/rBEwVV53NCKAI5F-AAAAtOzAe6s7895.md";

    /**
     * 投诉类型
     */
    public static final Integer COMPLAINT_TYPE_PROJECT = 1;
    public static final Integer COMPLAINT_TYPE_ARTICLE = 0;

    /**
     * 投诉是否处理
     */
    public static final Integer COMPLAINT_SOLVE_TRUE = 1;
    public static final Integer COMPLAINT_SOLVE_FALSE = 0;

    public static final Integer DEFAULT_STAR = 0;


}

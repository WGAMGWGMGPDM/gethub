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
import org.springframework.web.multipart.MultipartFile;

/**
* @Author: 张忍
* @Date: 2020-03-22 16:28
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "repo_file")
public class File implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文件夹或文件名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 是否文件夹
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 最近修改时间
     */
    @TableField(value = "modifytime")
    private Date modifytime;

    /**
     * 父级id
     */
    @TableField(value = "parentid")
    private Integer parentid;

    /**
     * 存放路径
     */
    @TableField(value = "filepath")
    private String filepath;

    /**
     * 所属项目id
     */
    @TableField(value = "projectid")
    private Integer projectid;

    /**
     * 文件md5唯一标识
     */
    @TableField(value = "md5code")
    private String md5code;

    /**
     * 文件夹上传时接收文件
     */
    @TableField(exist = false)
    private MultipartFile[] mfs;

    /**
     * 文件上传contenttype
     */
    @TableField(exist = false)
    private String contentType;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE = "type";

    public static final String COL_MODIFYTIME = "modifytime";

    public static final String COL_PARENTID = "parentid";

    public static final String COL_FILEPATH = "filepath";

    public static final String COL_PROJECTID = "projectid";

    public static final String COL_MD5CODE = "md5code";
}
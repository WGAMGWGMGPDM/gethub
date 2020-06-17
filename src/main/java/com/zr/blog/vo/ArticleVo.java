package com.zr.blog.vo;

import com.zr.system.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 19:59
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class ArticleVo extends BaseVo {
    private Integer author;
    private String title;
    private Integer readnum;
    private String tag;
    private String type;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer tagid;
    private String masterName;
    private Integer open;


}

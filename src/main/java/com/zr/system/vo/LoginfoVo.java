package com.zr.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-08 15:39
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class LoginfoVo extends BaseVo{
    private String loginname;
    private String loginip;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}

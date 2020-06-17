package com.zr.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:49
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class LanguageVo extends BaseVo{
    private String name;
}

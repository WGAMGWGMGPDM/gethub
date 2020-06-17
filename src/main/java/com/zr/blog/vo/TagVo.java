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
public class TagVo extends BaseVo {
    private String name;
}

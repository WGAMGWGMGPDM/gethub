package com.zr.system.vo;

import lombok.Data;

/**
 * @Author: 张忍
 * @Date: 2020-03-08 15:41
 */
@Data
public class BaseVo {
    private Integer page = 1;
    private Integer limit = 10;
    private Integer available;
}

package com.zr.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:49
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class RoleVo extends BaseVo{
   private String name;

   private String remark;

   private Integer available;

   private Integer uid;
}

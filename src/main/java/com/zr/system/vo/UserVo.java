package com.zr.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:49
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class UserVo extends BaseVo implements Serializable {
   private String name;

   private String remark;

   private String address;

   private Integer id;

   Integer available;

   private String pwd;

   private String validity;

   private String email;

   private String loginname;

   private String openid;


   private static final long serialVersionUID = 1L;
}

package com.zr.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.MenuTreeNode;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Loginfo;
import com.zr.system.domain.Menu;
import com.zr.system.domain.User;
import com.zr.system.domain.Useraddr;
import com.zr.system.service.LoginfoService;
import com.zr.system.service.MenuService;
import com.zr.system.service.UserService;
import com.zr.system.service.UseraddrService;
import com.zr.system.utils.HttpUtil;
import com.zr.system.utils.MD5Util;
import com.zr.system.utils.MailUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/registe")
public class RegisteController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UseraddrService useraddrService;


    /**
     * 用户激活账号
     */
    @RequestMapping("activate")
    public ResultObj activate(String cdkey){
        try {
            ValueOperations<String, String> opsForValue = this.redisTemplate.opsForValue();
            String loginname = opsForValue.get("activate:" + cdkey);
            User user = this.userService.queryUserByLoginNameIgnoreAvailable(loginname);
            if (null!=user){
                user.setAvailable(Constant.AVAILABLE_TRUE);
                this.userService.updateUser(user);
                return ResultObj.ACTIVATE_SUCCESS;
            }else {
                return ResultObj.ACTIVATE_ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ACTIVATE_ERROR;
        }



    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping("registeUser")
    public ResultObj registeUser(User user){
        try {
            user.setRegistertime(new Date());
            user.setType(Constant.USER_TYPE_NORMAL);
            user.setImgpath(Constant.DEFAULT_IMG);
            user.setRemark(Constant.DEFAULT_REMARK);
            user.setSalt(MD5Util.createUUID());
            user.setPwd(MD5Util.md5(user.getPwd(),user.getSalt(),2));
            user.setAvailable(Constant.AVAILABLE_FALSE);
            user.setOrdernum(this.userService.queryUserMaxOrderNum()+1);
            this.userService.saveUser(user);
            this.userService.savaUserRole(user.getId(),Constant.USER);
            this.useraddrService.incrementAddrNum(user.getAddress().substring(0,2));

            String cdkey = MD5Util.createUUID();
            ValueOperations<String, String> opsForValue = this.redisTemplate.opsForValue();
            opsForValue.set("activate:"+cdkey,user.getLoginname(), 2,TimeUnit.HOURS);
            //发送验证邮件
            new Thread(new MailUtil(Constant.SEND_ACTIVATE,user.getEmail(),cdkey)).start();


            return ResultObj.REGISTE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.REGISTE_ERROR;
        }

    }

}

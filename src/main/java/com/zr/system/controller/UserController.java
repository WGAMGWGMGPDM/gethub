package com.zr.system.controller;


import com.zr.system.common.*;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import com.zr.system.utils.MD5Util;
import com.zr.system.utils.MailUtil;
import com.zr.system.vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加载所有用户
     * @param userVo
     * @return
     */
    @RequestMapping("loadAllUser")
    public Object loadAllUser(UserVo userVo){
        return this.userService.queryAllUser(userVo);
    }


    /**
     * 修改用户
     * @param user
     * @return
     */
    @RequestMapping("updateUser")
    //@RequiresPermissions("user:update")//此处因为修改个人信息冲突
    public ResultObj updateUser(User user){
        try {
            this.userService.updateUser(user);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除用户
     */
    @RequestMapping("/deleteUser")
    @RequiresPermissions("user:delete")
    public ResultObj deleteUser(Integer id){
        try {
            this.userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     */
    @RequestMapping("/batchDeleteUser")
    @RequiresPermissions("user:batchDelete")
    public ResultObj batchDeleteUser(Integer[] ids){
        try {
            if (null!=ids&&ids.length>0){
                List<Integer> idList = new ArrayList<>();
                for (Integer id : ids) {
                    idList.add(id);
                }
                this.userService.removeByIds(idList);
                return ResultObj.DELETE_SUCCESS;
            }else {
                return new ResultObj(-1,"传入id为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 根据登录名查询用户是否存在
     * @param loginname
     * @return
     */
    @RequestMapping("userIsExist")
    public Object userIsExist(String loginname){
        User user = this.userService.queryUserByLoginName(loginname);
        if(null==user){
            return ResultObj.EXIST_FALSE;
        }else {
            return ResultObj.EXIST_TRUE;
        }
    }

    /**
     * 根据openid查询用户是否存在
     * @param openId
     * @return
     */
    @RequestMapping("userIsBound")
    public Object userIsBound(String openId){
        User user = this.userService.queryUserByOpenId(openId);
        if(null==user){
            return ResultObj.EXIST_FALSE;
        }else {
            return ResultObj.EXIST_TRUE;
        }
    }

    /**
     * 保存用户的角色关系
     * @param uid
     * @param rids
     * @return
     */
    @RequestMapping("addUserRole")
    @RequiresPermissions("user:dispatch")
    public ResultObj addUserRole(Integer uid,Integer[] rids){
        try {
            this.userService.savaUserRole(uid,rids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }

    /**
     * 查询当前登录用户
     * @return
     */
    @GetMapping("getCurrentUser")
    public Object getCurrentUser(){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = this.userService.getById(activeUser.getUser().getId());
        return new DataGridView(user);
    }

    /**
     * 根据id查询用户
     * @return
     */
    @GetMapping("getUserById")
    public Object getUserById(Integer id){
        User user = this.userService.getById(id);
        return new DataGridView(user);
    }

    /**
     * 查询关注用户
     * @return
     */
    @GetMapping("loadFollowUser")
    public Object loadFollowUser(UserVo userVo){
        return this.userService.loadFollowUser(userVo);
    }

    /**
     * 查询用户是否已关注
     * @return
     */
    @RequestMapping("queryFollow")
    public ResultObj queryFollow(Integer id){
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            return this.userService.queryFollow(uid,id);
    }

    /**
     * 关注用户
     * @return
     */
    @RequestMapping("followUser")
    public ResultObj followUser(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.userService.followUser(uid,id);
            return ResultObj.FOLLOW_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.FOLLOW_ERROR;
        }
    }

    /**
     * 取关用户
     * @return
     */
    @RequestMapping("unFollowUser")
    public ResultObj unFollowUser(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.userService.unFollowUser(uid,id);
            return ResultObj.UNFOLLOW_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UNFOLLOW_ERROR;
        }
    }

    /**
     * 发送验证码
     * @return
     */
    @RequestMapping("sendvalidity")
    public ResultObj sendvalidity(String userid){
        try {
            User user = this.userService.getById(userid);
            //生成验证码
            String cdKey = MD5Util.createRandom();
            //发送验证邮件
            new Thread(new MailUtil(Constant.SEND_VALIDITY,user.getEmail(),cdKey)).start();
            //验证码存redis数据库
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set("validity:"+user.getId(),cdKey,5*60, TimeUnit.SECONDS);
            return ResultObj.SEND_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.SEND_ERROR;
        }
    }

    /**
     * 发送忘记密码验证码
     * @return
     */
    @RequestMapping("sendvalidityForReset")
    public ResultObj sendvalidityForReset(String loginname){
        try {
            User user = this.userService.queryUserByLoginName(loginname);
            if(null!=user){
                //生成验证码
                String cdKey = MD5Util.createRandom();
                //发送验证邮件
                new Thread(new MailUtil(Constant.SEND_VALIDITY,user.getEmail(),cdKey)).start();
                //验证码存redis数据库
                ValueOperations<String, String> ops = redisTemplate.opsForValue();
                ops.set("validity:"+user.getId(),cdKey,5*60, TimeUnit.SECONDS);
                return ResultObj.SEND_SUCCESS;
            }else {
                return ResultObj.EXIST_FALSE;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.SEND_ERROR;
        }
    }

    /**
     * 修改密码
     * @return
     */
    @RequestMapping("changepwd")
    public ResultObj changepwd(UserVo userVo){
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String redisValidity = ops.get("validity:" + userVo.getId());

            if (redisValidity==null){//验证码过期或者未获得验证码
                return ResultObj.UPDATE_ERROR;
            }else {
                if(redisValidity.equals(userVo.getValidity())){
                    //修改密码
                    User user1 = new User();
                    user1.setId(userVo.getId());
                    user1.setSalt(MD5Util.createUUID());
                    user1.setPwd(MD5Util.md5(userVo.getPwd(),user1.getSalt(),2));
                    this.userService.updateUser(user1);
                    return ResultObj.UPDATE_SUCCESS;
                }else {
                    //验证码错误
                    return ResultObj.VALIDITY_ERROR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 忘记密码
     * @return
     */
    @RequestMapping("resetPwd")
    public ResultObj resetPwd(UserVo userVo){
        try {
            User user = this.userService.queryUserByLoginName(userVo.getLoginname());
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String redisValidity = ops.get("validity:" + user.getId());

            if (redisValidity==null){//验证码过期或者未获得验证码
                return ResultObj.UPDATE_ERROR;
            }else {
                if(redisValidity.equals(userVo.getValidity())){
                    //修改密码
                    User user1 = new User();
                    user1.setId(user.getId());
                    user1.setSalt(MD5Util.createUUID());
                    user1.setPwd(MD5Util.md5(userVo.getPwd(),user1.getSalt(),2));
                    this.userService.updateUser(user1);
                    return ResultObj.UPDATE_SUCCESS;
                }else {
                    //验证码错误
                    return ResultObj.VALIDITY_ERROR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 绑定QQ号
     * @return
     */
    @RequestMapping("bondQQ")
    public ResultObj bondQQ(UserVo userVo){
        try {
            User user = this.userService.queryUserByLoginName(userVo.getLoginname());
            if (null!=user){
                String s = MD5Util.md5(userVo.getPwd(), user.getSalt(), 2);
                if(s.equals(user.getPwd())){
                    //绑定QQ
                    User user1 = new User();
                    user1.setId(user.getId());
                    user1.setOpenid(userVo.getOpenid());
                    this.userService.updateUser(user1);
                    return ResultObj.BOND_SUCCESS;
                }else {
                    return ResultObj.BOND_ERROR;
                }
            }else {
                return ResultObj.EXIST_FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.BOND_ERROR;
        }
    }

    /**
     * 修改绑定邮箱
     * @return
     */
    @RequestMapping("changeEmail")
    public ResultObj changeEmail(UserVo userVo){
        try {
            //获得当前登录用户
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            User user = activeUser.getUser();
            //获得验证码
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String redisValidity = ops.get("validity:" + user.getId());

            if (redisValidity==null){//验证码过期或者未获得验证码
                return ResultObj.UPDATE_ERROR;
            }else {
                if(redisValidity.equals(userVo.getValidity())){
                    //修改邮箱
                    User user1 = new User();
                    user1.setId(user.getId());
                    user1.setEmail(userVo.getEmail());
                    this.userService.updateUser(user1);
                    return ResultObj.UPDATE_SUCCESS;
                }else {
                    //验证码错误
                    return ResultObj.VALIDITY_ERROR;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }


}

package com.zr.system.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.zr.system.common.*;
import com.zr.system.domain.Loginfo;
import com.zr.system.domain.Menu;
import com.zr.system.domain.User;
import com.zr.system.service.LoginfoService;
import com.zr.system.service.MenuService;
import com.zr.system.service.UserService;
import com.zr.system.shiro.EasyTypeToken;
import com.zr.system.utils.HttpUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("api/login")
public class LoginController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private LoginfoService loginfoService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @RequestMapping("doLogin")
    @ResponseBody
    public ResultObj doLogin(String loginname, String password,String captchaKey,String captcha, HttpServletRequest request){
        try{
            //验证登录验证码
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            String captchaValue = opsForValue.get(captchaKey);
            if (null==captchaValue){
                return new ResultObj(-1,"验证码过期");
            }else {
                if (captchaValue.equalsIgnoreCase(captcha)){
                    Subject subject = SecurityUtils.getSubject();
                    EasyTypeToken typeToken = new EasyTypeToken(loginname, password);
                    subject.login(typeToken);
                    //得到shiro的sessionid == token
                    String token = subject.getSession().getId().toString();

                    //写入登录日志
                    ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
                    User user = activeUser.getUser();
                    Loginfo loginfo = new Loginfo();
                    loginfo.setLoginname(user.getName()+"-"+user.getLoginname());
                    loginfo.setLoginip(request.getHeader("X-Real-IP"));
                    loginfo.setLogintime(new Date());
                    //得到登录地点
                    String address = HttpUtil.IptoAddress(request.getHeader("X-Real-IP"));
                    loginfo.setLoginaddress(address);
                    loginfoService.save(loginfo);

                    //将用户权限带到前端
                    List<String> permissions = activeUser.getPermissions();
                    Map<String,Object> map = new HashMap<>();
                    map.put("token",token);
                    map.put("permission",permissions);
                    map.put("usertype",user.getType());
                    map.put("username",user.getName());
                    map.put("userid",user.getId());
                    map.put("userimg",user.getImgpath());
                    return new ResultObj(200,"登录成功",map);
                }else {
                    return new ResultObj(-1,"验证码错误");
                }
            }
        }catch (AuthenticationException e){
            e.printStackTrace();
            return new ResultObj(-1,"用户名或密码不正确");
        }
    }

    /**
     * 用户QQ登录
     */
    @RequestMapping("doQQLogin")
    @ResponseBody
    public ResultObj doQQLogin(String openid,  HttpServletRequest request){
        try{
            User userByOpenId = this.userService.queryUserByOpenId(openid);
            Subject subject = SecurityUtils.getSubject();
            EasyTypeToken typeToken = new EasyTypeToken(userByOpenId.getLoginname());
            subject.login(typeToken);
            //得到shiro的sessionid == token
            String token = subject.getSession().getId().toString();

            //写入登录日志
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            User user = activeUser.getUser();
            Loginfo loginfo = new Loginfo();
            loginfo.setLoginname(user.getName()+"-"+user.getLoginname());
            loginfo.setLoginip(request.getHeader("X-Real-IP"));
            loginfo.setLogintime(new Date());
            //得到登录地点
            String address = HttpUtil.IptoAddress(request.getHeader("X-Real-IP"));
            loginfo.setLoginaddress(address);
            loginfoService.save(loginfo);

            //将用户权限带到前端
            List<String> permissions = activeUser.getPermissions();
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("permission",permissions);
            map.put("usertype",user.getType());
            map.put("username",user.getName());
            map.put("userid",user.getId());
            map.put("userimg",user.getImgpath());
            return new ResultObj(200,"登录成功",map);


        }catch (AuthenticationException e){
            e.printStackTrace();
            return new ResultObj(-1,"登录失败");
        }
    }



    /**
     * 加载所有菜单
     */
    @RequestMapping("loadIndexMenu")
    @ResponseBody
    public Object loadIndexMenu(){
        //得到当前登录信息
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
        User user = activeUser.getUser();
        if(null==user){
            return null;
        }

        List<Menu> menus = null;
        if(user.getType().equals(Constant.USER_TYPE_SUPER)){
            //超级管理员所有菜单权限
            menus = menuService.queryAllMenuForList();
        }else {
            //其他用户根据角色查询菜单权限
            menus = menuService.queryMenuForListByUserId(user.getId());
        }
        //将MenuList渲染为MenuTreeNodeList
        List<MenuTreeNode> treeNodes = new ArrayList<>();
        for (Menu menu : menus) {
            Boolean spread = menu.getSpread().equals(Constant.SPREAD_TRUE);

            //String target = "_self";
            treeNodes.add(new MenuTreeNode(menu.getId(),menu.getPid(),menu.getTitle(),menu.getHref(),menu.getIcon(),spread,menu.getTarget(),menu.getTypecode()));
        }
        //将MenuTreeNodeList渲染为有层级结构的树
        List<MenuTreeNode> nodes = MenuTreeNode.MenuTreeNodeBuilder.build(treeNodes,0);
        Map<String,Object> res = new HashMap<>();
        for (MenuTreeNode node : nodes) {
            res.put(node.getTypecode(),node);
        }
        return res;
    }

    /**
     * 验证当前token是否登录
     * @return
     */
    @RequestMapping("checkLogin")
    @ResponseBody
    public ResultObj checkLogin(){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            return ResultObj.IS_LOGIN;
        }else {
            return ResultObj.UN_LOGIN;
        }
    }

    /**
     * 验证码请求
     * @param response
     * @param captchaKey
     * @throws IOException
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletResponse response,String captchaKey) throws IOException {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(100, 38, 4, 4);
        String code = shearCaptcha.getCode();
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(captchaKey,code,60, TimeUnit.SECONDS);
        shearCaptcha.write(response.getOutputStream());
    }




}

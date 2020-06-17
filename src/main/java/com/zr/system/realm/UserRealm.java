package com.zr.system.realm;


import com.zr.repo.domain.Project;
import com.zr.repo.service.ProjectService;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.domain.User;
import com.zr.system.service.MenuService;
import com.zr.system.service.RoleService;
import com.zr.system.service.UserService;
import com.zr.system.utils.AppUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy//解决缓存问题,待spring ioc容器加载创建玩代理对象后再加载
    private MenuService menuService;


    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = authenticationToken.getPrincipal().toString();


        User user = userService.queryUserByLoginName(username);
        if (null!=user){
            ActiveUser activerUser = new ActiveUser();
            activerUser.setUser(user);
            //根据用户ID查询角色
            List<String> roleNames = this.roleService.queryRoleNamesByUid(user.getId());

            //判断是否为普通用户，仅有注册用户角色，注册用户并且项目发表数量满5
            ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
            List<Project> projects = projectService.queryProjectByUid(user.getId());
            if(user.getType().equals(Constant.USER_TYPE_NORMAL)&&roleNames.size()==1&&projects.size()>=5){
                user.setType(Constant.USER_TYPE_OFFICIAL);
                userService.updateUser(user);
                Integer[] rids = {7};
                userService.savaUserRole(user.getId(),rids);
            }


            //根据用户ID查询权限编码
            List<String> permissionNames = this.menuService.queryPermissioncodesByUid(user.getId());
            activerUser.setRoles(roleNames);
            activerUser.setPermissions(permissionNames);
            ByteSource salt = ByteSource.Util.bytes(user.getSalt());
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activerUser, user.getPwd(),salt, this.getName());
            return info;
        }else {
            return null;
        }
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        ActiveUser activerUser = (ActiveUser) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();


        List<String> roles = activerUser.getRoles();
        List<String> permission = activerUser.getPermissions();
        User user = activerUser.getUser();
        if(user.getType().equals(Constant.USER_TYPE_SUPER)){
            authorizationInfo.addStringPermission("*:*");
        }else {
            if (null!=roles&&roles.size()>0){
                authorizationInfo.addRoles(roles);
            }
            if (null!=permission&&permission.size()>0){
                authorizationInfo.addStringPermissions(permission);
            }
        }
        return authorizationInfo;
    }
}

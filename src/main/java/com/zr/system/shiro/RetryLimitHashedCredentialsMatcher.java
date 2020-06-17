package com.zr.system.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

/**
 * @Author: 张忍
 * @Date: 2020-05-08 19:43
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        EasyTypeToken tk = (EasyTypeToken) token;
        //如果是免密登录直接返回true
        if(tk.getType().equals(LoginType.NOPASSWD)){
            return true;
        }
        //不是免密登录，调用父类的方法
        return super.doCredentialsMatch(tk, info);
    }
}

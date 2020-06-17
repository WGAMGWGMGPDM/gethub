package com.zr.system.shiro;

import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.UUID;

@Configuration
public class TokenWebSessionManager extends DefaultWebSessionManager {
    public static final String TOKEN_HEADER = "TOKEN";

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //从头里获得请求token 如果不存在就生成
        String header = WebUtils.toHttp(request).getHeader(TOKEN_HEADER);
        if(StringUtils.hasText(header)){
            return header;
        }
        return UUID.randomUUID().toString();
    }
}

package com.zr.system.config;

import com.zr.system.realm.UserRealm;
import com.zr.system.shiro.RetryLimitHashedCredentialsMatcher;
import com.zr.system.shiro.ShiroLoginFilter;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.Filter;
import java.util.*;


@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroAutoConfiguration {
    @Autowired
    private ShiroProperties shiroProperties;
    @Autowired
    private RedisProperties redisProperties;

    /**
     * 创建凭证匹配器
     * @return
     */
    @Bean
    public HashedCredentialsMatcher credentialsMatcher(){
        RetryLimitHashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher();
        //HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //注入加密方式
        credentialsMatcher.setHashAlgorithmName(shiroProperties.getHashAlgorithmName());
        //注入散列次数
        credentialsMatcher.setHashIterations(shiroProperties.getHashIterations());
        return credentialsMatcher;
    }
    /**
     * 创建realm
     */
    @Bean
    public UserRealm userRealm(CredentialsMatcher credentialsMatcher){
        UserRealm userRealm = new UserRealm();

        //注入凭证匹配器
        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }


    /**
     * 声明安全管理器
     *
     */
    @Bean("securityManager")
    public SecurityManager securityManager(DefaultWebSessionManager defaultWebSessionManager, SessionDAO redisSession,UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        defaultWebSessionManager.setSessionDAO(redisSession);
        securityManager.setSessionManager(defaultWebSessionManager);
        return securityManager;
    }
    /**
     * 配置过滤器Shiro和Web过滤器 id 必须和web.xml里的shiroFilter的targetBeanName的值一样
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //注入安全管理器
        bean.setSecurityManager(securityManager);
        //注入登录页面
        bean.setLoginUrl(shiroProperties.getLoginUrl());
        //注入未授权的页面地址
        bean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());

        //创建自定义filter
        Map<String, Filter> map = new HashMap<>();
        map.put("authc",new ShiroLoginFilter());
        bean.setFilters(map);

        //注入过滤器,注意LinkedHashMap可按插入顺序排序
        LinkedHashMap<String,String> filterChainDefinition = new LinkedHashMap<>();

        //注入放行地址
        if (shiroProperties.getAnonUrls()!=null&&shiroProperties.getAnonUrls().length>0){
            String[] anonUrls = shiroProperties.getAnonUrls();

            for (String anonUrl:anonUrls) {
                filterChainDefinition.put(anonUrl,"anon");
            }
        }
        //注入登出的地址
        if (shiroProperties.getLogoutUrl()!=null){
            filterChainDefinition.put(shiroProperties.getLogoutUrl(),"logout");
        }

        //注入拦截的地址
        String[] authcUrls = shiroProperties.getAuthcUrls();
        if (authcUrls!=null&&authcUrls.length>0){
            for (String authcUrl:authcUrls) {
                filterChainDefinition.put(authcUrl,"authc");
            }
        }
        bean.setFilterChainDefinitionMap(filterChainDefinition);
        return bean;
    }

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBeanDelegatingFilterProxy(){
        FilterRegistrationBean<DelegatingFilterProxy> bean = new FilterRegistrationBean<>();
        //创建过滤器
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetBeanName("shiroFilter");
        proxy.setTargetFilterLifecycle(true);
        bean.setFilter(proxy);

        List<String> servletNames = new ArrayList<>();
        servletNames.add(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
        bean.setServletNames(servletNames);
        return bean;
    }

    /**
     * 加入注解使用开始
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return  advisorAutoProxyCreator;
    }
    /*加入注解使用结束*/


    @Bean
    public SessionDAO redisSessionDAO(IRedisManager redisManager){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);//操作那个redis
        redisSessionDAO.setExpire(7*24*3688);//用户登录信息保存7天
        //       redisSessionDAO.setKeySerializer(keySerializer); jdk
        //       redisSessionDAO.setValueSerializer(valueSerializer);jdk
        return redisSessionDAO ;
    }

    @Bean
    public IRedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());//连接池最大量20个
        jedisPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());//连接池最大剩余15个
        jedisPoolConfig.setMinIdle(redisProperties.getJedis().getPool().getMinIdle());//连接池初始10个
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,redisProperties.getHost(),redisProperties.getPort(),2000);
        redisManager.setJedisPool(jedisPool);
        return redisManager;

    }


}

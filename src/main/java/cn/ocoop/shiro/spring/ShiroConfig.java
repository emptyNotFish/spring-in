package cn.ocoop.shiro.spring;

import cn.ocoop.redis.SpringDataRedisConfig;
import cn.ocoop.shiro.FilterChainService;
import cn.ocoop.shiro.ShiroFilterFactoryBean;
import cn.ocoop.shiro.authc.realm.*;
import cn.ocoop.shiro.authz.SingleModularRealmAuthorizer;
import cn.ocoop.shiro.filter.AjaxAuthenticationFilter;
import cn.ocoop.shiro.filter.AutoAuthenticationFilter;
import cn.ocoop.shiro.filter.HttpBasicAuthenticationFilter;
import cn.ocoop.shiro.filter.LogoutFilter;
import cn.ocoop.shiro.filter.authc.CaptchaGeneratorFilter;
import cn.ocoop.shiro.filter.authc.RateLimitWithCaptchaFilter;
import cn.ocoop.shiro.filter.authz.AnyRolesAuthorizationFilterAdapter;
import cn.ocoop.shiro.filter.authz.PermissionsAuthorizationFilterAdapter;
import cn.ocoop.shiro.filter.authz.RolesAuthorizationFilterAdapter;
import cn.ocoop.shiro.session.mgt.ShiroCacheManager;
import cn.ocoop.shiro.session.mgt.ShiroCacheManagerRedisAdapter;
import cn.ocoop.shiro.session.mgt.ShiroSessionDao;
import cn.ocoop.shiro.session.mgt.ShiroSessionDaoRedisAdapter;
import cn.ocoop.shiro.utils.RequestUtil;
import cn.ocoop.spring.App;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.Map;
import java.util.Optional;

@Configuration
@DependsOn(value = {"app"})
@Import(SpringDataRedisConfig.class)
public class ShiroConfig {
    private static final Logger log = LoggerFactory.getLogger(ShiroConfig.class);

    @Autowired
    Environment environment;

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public SingleModularRealmAuthorizer singleModularRealmAuthorizer() {
        return new SingleModularRealmAuthorizer();
    }

    @Bean
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(environment.getProperty("shiro.credentials.algorithmName", "md5"));
        credentialsMatcher.setHashIterations(environment.getProperty("shiro.credentials.iterations", int.class, 2));
        credentialsMatcher.setStoredCredentialsHexEncoded(environment.getProperty("shiro.credentials.hexEncoded", boolean.class, true));
        return credentialsMatcher;
    }

    @Bean
    public ApiAuthorizingRealm apiAuthorizingRealm() {
        ApiAuthorizingRealm apiAuthorizingRealm = new ApiAuthorizingRealm();
        apiAuthorizingRealm.setCredentialsMatcher(credentialsMatcher());
        apiAuthorizingRealm.setAuthenticationCachingEnabled(false);
        apiAuthorizingRealm.setAuthorizationCachingEnabled(true);
        apiAuthorizingRealm.setName(environment.getProperty("shiro.realm.apiAuthorizingRealm.name", "api"));
        return apiAuthorizingRealm;
    }

    @Bean
    public AutoDetectedAuthorizingRealm autoDetectedAuthorizingRealm() {
        AutoDetectedAuthorizingRealm autoDetectedAuthorizingRealm = new AutoDetectedAuthorizingRealm();
        autoDetectedAuthorizingRealm.setCredentialsMatcher(credentialsMatcher());
        autoDetectedAuthorizingRealm.setAuthenticationCachingEnabled(false);
        autoDetectedAuthorizingRealm.setAuthorizationCachingEnabled(true);
        autoDetectedAuthorizingRealm.setName(environment.getProperty("shiro.realm.autoDetectedAuthorizingRealm.name", "autoDetected"));
        return autoDetectedAuthorizingRealm;
    }

    @Bean
    public HttpBasicAuthorizingRealm httpBasicAuthorizingRealm() {
        HttpBasicAuthorizingRealm httpBasicAuthorizingRealm = new HttpBasicAuthorizingRealm();
        httpBasicAuthorizingRealm.setCredentialsMatcher(credentialsMatcher());
        httpBasicAuthorizingRealm.setAuthenticationCachingEnabled(false);
        httpBasicAuthorizingRealm.setAuthorizationCachingEnabled(false);
        return httpBasicAuthorizingRealm;
    }

    @Bean
    public MobileCaptchaAuthorizingRealm mobileCaptchaAuthorizingRealm() {
        MobileCaptchaAuthorizingRealm mobileCaptchaAuthorizingRealm = new MobileCaptchaAuthorizingRealm();
        mobileCaptchaAuthorizingRealm.setCredentialsMatcher(credentialsMatcher());
        mobileCaptchaAuthorizingRealm.setAuthenticationCachingEnabled(false);
        mobileCaptchaAuthorizingRealm.setAuthorizationCachingEnabled(true);
        mobileCaptchaAuthorizingRealm.setName(environment.getProperty("shiro.realm.mobileCaptchaAuthorizingRealm.name", "mobileCaptcha"));
        return mobileCaptchaAuthorizingRealm;
    }

    @Bean
    public UsernamePasswordRealm usernamePasswordRealm() {
        UsernamePasswordRealm usernamePasswordRealm = new UsernamePasswordRealm();
        usernamePasswordRealm.setCredentialsMatcher(credentialsMatcher());
        usernamePasswordRealm.setAuthenticationCachingEnabled(false);
        usernamePasswordRealm.setAuthorizationCachingEnabled(true);
        usernamePasswordRealm.setName(environment.getProperty("shiro.realm.usernamePasswordRealm.name", "usernamePassword"));
        return usernamePasswordRealm;
    }

    @Bean
    public ShiroSessionDaoRedisAdapter shiroSessionDaoRedisAdapter() {
        ShiroSessionDaoRedisAdapter shiroSessionDaoRedisAdapter = new ShiroSessionDaoRedisAdapter();
        shiroSessionDaoRedisAdapter.setSessionPrefix(environment.getProperty("shiro.session.prefix", "shiro:session:"));
        shiroSessionDaoRedisAdapter.setRedisTemplate(App.getBean(RedisTemplate.class));
        return shiroSessionDaoRedisAdapter;
    }

    @Bean
    public ShiroSessionDao sessionDao() {
        ShiroSessionDao shiroSessionDao = new ShiroSessionDao();
        shiroSessionDao.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
        shiroSessionDao.setShiroSessionRepositoryAware(shiroSessionDaoRedisAdapter());
        return shiroSessionDao;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setGlobalSessionTimeout(environment.getProperty("shiro.session.globalSessionTimeout", long.class, 86400000l));
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(false);
        defaultWebSessionManager.setSessionDAO(sessionDao());
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        SimpleCookie sessionIdCookie = new SimpleCookie(environment.getProperty("shiro.session.cookie.name", "sid"));
        sessionIdCookie.setMaxAge(-1);
        sessionIdCookie.setPath(environment.getProperty("shiro.session.cookie.domain", "/"));
        sessionIdCookie.setHttpOnly(true);
        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie);
        return defaultWebSessionManager;
    }

    @Bean
    public ShiroCacheManager cacheManager() {
        ShiroCacheManager shiroCacheManager = new ShiroCacheManager();

        ShiroCacheManagerRedisAdapter shiroCacheManagerRedisAdapter = new ShiroCacheManagerRedisAdapter();
        shiroCacheManagerRedisAdapter.setCachePrefix(environment.getProperty("shiro.cache.prefix", "shiro:cache:"));
        shiroCacheManagerRedisAdapter.setRedisTemplate(App.getBean(RedisTemplate.class));

        shiroCacheManager.setShiroCacheManagerAware(shiroCacheManagerRedisAdapter);
        return shiroCacheManager;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthorizer(singleModularRealmAuthorizer());
        securityManager.setRealms(
                Lists.newArrayList(
                        autoDetectedAuthorizingRealm(),
                        mobileCaptchaAuthorizingRealm(),
                        usernamePasswordRealm(),
                        httpBasicAuthorizingRealm(),
                        apiAuthorizingRealm()
                )
        );
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());

        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        String cipherKey = environment.getProperty("shiro.authc.rememberMe.cipherKey", "shiro.authc.rememberMe.cipherKey.defaultValue");
        cookieRememberMeManager.setCipherKey(Base64.decode(Base64.encode(cipherKey.getBytes())));
        SimpleCookie rememberMeCookie = new SimpleCookie();
        rememberMeCookie.setName(environment.getProperty("shiro.authc.rememberMe.cookie.name", "rememberMe"));
        rememberMeCookie.setMaxAge(environment.getProperty("shiro.authc.rememberMe.cookie.maxAge", int.class, 604800));
        rememberMeCookie.setPath(environment.getProperty("shiro.session.cookie.domain", "/"));
        cookieRememberMeManager.setCookie(rememberMeCookie);
        securityManager.setRememberMeManager(cookieRememberMeManager);
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    private AjaxAuthenticationFilter getAjaxAuthenticationFilterAdapter(Integer statusCode) {
        AjaxAuthenticationFilter ajaxAuthenticationFilter = new AjaxAuthenticationFilter();
        ajaxAuthenticationFilter.setUsernameParam(environment.getProperty("shiro.authc.usernameParam", "username"));
        ajaxAuthenticationFilter.setPasswordParam(environment.getProperty("shiro.authc.passwordParam", "password"));
        ajaxAuthenticationFilter.setRememberMeParam(environment.getProperty("shiro.authc.rememberMe.param", "rememberMeParam"));
        Optional.ofNullable(statusCode).ifPresent(ajaxAuthenticationFilter::setUnLoginStatusCode);
        return ajaxAuthenticationFilter;
    }

    @Bean
    public AjaxAuthenticationFilter ajaxAuthenticationFilter() {
        return getAjaxAuthenticationFilterAdapter(null);
    }

    @Bean
    public AjaxAuthenticationFilter ajaxAuthenticationFilter419() {
        return getAjaxAuthenticationFilterAdapter(RequestUtil.SC_UNLOGIN_1);
    }

    @Bean
    public AjaxAuthenticationFilter ajaxAuthenticationFilter421() {
        return getAjaxAuthenticationFilterAdapter(RequestUtil.SC_UNLOGIN_2);
    }

    private PermissionsAuthorizationFilterAdapter getPermissionsAuthorizationFilterAdapter(Integer unLoginStatusCode, Integer permsInvalidStatusCode) {
        PermissionsAuthorizationFilterAdapter permissionsAuthorizationFilterAdapter = new PermissionsAuthorizationFilterAdapter();
        Optional.ofNullable(unLoginStatusCode).ifPresent(permissionsAuthorizationFilterAdapter::setUnLoginStatusCode);
        Optional.ofNullable(permsInvalidStatusCode).ifPresent(permissionsAuthorizationFilterAdapter::setPermsInvalidStatusCode);
        return permissionsAuthorizationFilterAdapter;
    }

    @Bean
    public PermissionsAuthorizationFilterAdapter permissionsAuthorizationFilter() {
        return getPermissionsAuthorizationFilterAdapter(null, null);
    }

    @Bean
    public PermissionsAuthorizationFilterAdapter permissionsAuthorizationFilter_authc419_perms450() {
        return getPermissionsAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_1, RequestUtil.SC_UNAUTHORIZED_1);
    }

    @Bean
    public PermissionsAuthorizationFilterAdapter permissionsAuthorizationFilter_authc421_perms451() {
        return getPermissionsAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_2, RequestUtil.SC_UNAUTHORIZED_2);
    }

    private RolesAuthorizationFilterAdapter getRolesAuthorizationFilterAdapter(Integer unLoginStatusCode, Integer permsInvalidStatusCode) {
        RolesAuthorizationFilterAdapter rolesAuthorizationFilterAdapter = new RolesAuthorizationFilterAdapter();
        Optional.ofNullable(unLoginStatusCode).ifPresent(rolesAuthorizationFilterAdapter::setUnLoginStatusCode);
        Optional.ofNullable(permsInvalidStatusCode).ifPresent(rolesAuthorizationFilterAdapter::setPermsInvalidStatusCode);
        return rolesAuthorizationFilterAdapter;
    }

    @Bean
    public RolesAuthorizationFilterAdapter rolesAuthorizationFilter() {
        return getRolesAuthorizationFilterAdapter(null, null);
    }

    @Bean
    public RolesAuthorizationFilterAdapter rolesAuthorizationFilter_authc419_perms450() {
        return getRolesAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_1, RequestUtil.SC_UNAUTHORIZED_1);
    }

    @Bean
    public RolesAuthorizationFilterAdapter rolesAuthorizationFilter_authc421_perms451() {
        return getRolesAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_2, RequestUtil.SC_UNAUTHORIZED_2);
    }

    private AnyRolesAuthorizationFilterAdapter getAnyRolesAuthorizationFilterAdapter(Integer unLoginStatusCode, Integer permsInvalidStatusCode) {
        AnyRolesAuthorizationFilterAdapter anyRolesAuthorizationFilterAdapter = new AnyRolesAuthorizationFilterAdapter();
        Optional.ofNullable(unLoginStatusCode).ifPresent(anyRolesAuthorizationFilterAdapter::setUnLoginStatusCode);
        Optional.ofNullable(permsInvalidStatusCode).ifPresent(anyRolesAuthorizationFilterAdapter::setPermsInvalidStatusCode);
        return anyRolesAuthorizationFilterAdapter;
    }

    @Bean
    public AnyRolesAuthorizationFilterAdapter anyRolesAuthorizationFilter() {
        return getAnyRolesAuthorizationFilterAdapter(null, null);
    }

    @Bean
    public AnyRolesAuthorizationFilterAdapter anyRolesAuthorizationFilterAdapter_authc419_perms450() {
        return getAnyRolesAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_1, RequestUtil.SC_UNAUTHORIZED_1);
    }

    @Bean
    public AnyRolesAuthorizationFilterAdapter anyRolesAuthorizationFilterAdapter_authc421_perms451() {
        return getAnyRolesAuthorizationFilterAdapter(RequestUtil.SC_UNLOGIN_2, RequestUtil.SC_UNAUTHORIZED_2);
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setManager(new DefaultFilterChainManager());
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        shiroFilterFactoryBean.setLoginUrl(environment.getProperty("shiro.authc.loginURL", "/login"));
        shiroFilterFactoryBean.setUnauthorizedUrl(environment.getProperty("shiro.authc.unauthorizedURL", "/unauthorized"));
        shiroFilterFactoryBean.setFilters(getFilters());

        return shiroFilterFactoryBean;
    }

    private Map<String, Filter> getFilters() {
        Map<String, Filter> filters = Maps.newHashMap();
        filters.put("auth", ajaxAuthenticationFilter());
        filters.put("auth419", ajaxAuthenticationFilter419());
        filters.put("auth421", ajaxAuthenticationFilter421());

        filters.put("perm", permissionsAuthorizationFilter());
        filters.put("perm419", permissionsAuthorizationFilter_authc419_perms450());
        filters.put("perm421", permissionsAuthorizationFilter_authc421_perms451());

        filters.put("role", rolesAuthorizationFilter());
        filters.put("role419", rolesAuthorizationFilter_authc419_perms450());
        filters.put("role421", rolesAuthorizationFilter_authc421_perms451());

        filters.put("anyRole", anyRolesAuthorizationFilter());
        filters.put("anyRole419", anyRolesAuthorizationFilterAdapter_authc419_perms450());
        filters.put("anyRole421", anyRolesAuthorizationFilterAdapter_authc421_perms451());
        filters.put("httpBasicAuth", new HttpBasicAuthenticationFilter());
        filters.put("logout", new LogoutFilter());
        filters.put("autoAuth", new AutoAuthenticationFilter());
        filters.put("captcha", new CaptchaGeneratorFilter());
        filters.put("rateLimit", new RateLimitWithCaptchaFilter(
                environment.getProperty("shiro.captcha.maxRate", long.class, 10l),
                environment.getProperty("shiro.captcha.rateLifeTime", long.class, 3600000l)
        ));

        Map<String, Filter> userFilters = null;
        try {
            userFilters = App.getBean(FilterChainService.class).getFilters();
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("FilterChainService未配置");
        }
        if (MapUtils.isNotEmpty(userFilters)) filters.putAll(userFilters);
        return filters;
    }


}

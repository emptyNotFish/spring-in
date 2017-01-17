package cn.ocoop.spring;

import cn.ocoop.shiro.spring.ShiroConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Filter[] getServletFilters() {
        DelegatingFilterProxy securityFilterChain = new DelegatingFilterProxy("shiroFilter");
        securityFilterChain.setTargetFilterLifecycle(true);
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[]{
                characterEncodingFilter,
                securityFilterChain
        };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class, ShiroConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}

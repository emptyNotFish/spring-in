package cn.ocoop.spring;

import cn.ocoop.spring.annotation.MvcConfiguration;
import cn.ocoop.spring.annotation.SpringConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@SpringConfiguration
@PropertySource("classpath:app.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "cn.ocoop,${spring.basePackages}",
        excludeFilters = @ComponentScan.Filter(
                value = {
                        MvcConfiguration.class,
                        Controller.class,
                        RestController.class,
                        ControllerAdvice.class
                }
        )
)
public class SpringConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}

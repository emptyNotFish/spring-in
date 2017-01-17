package cn.ocoop.spring;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@PropertySource("classpath:app.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "cn.ocoop",
        excludeFilters = @ComponentScan.Filter(
                value = {
                        Configuration.class,
                        Controller.class,
                        RestController.class,
                        ControllerAdvice.class
                }
        )
)
@Import(SpringSubConfig.class)
public class SpringConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}

package cn.ocoop.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan(basePackages = "${spring.basePackages}",
        excludeFilters = @ComponentScan.Filter(
                value = {
                        Controller.class,
                        RestController.class,
                        ControllerAdvice.class
                }
        )
)
public class SpringSubConfig {
}

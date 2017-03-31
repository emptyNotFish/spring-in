package cn.ocoop.spring.annotation;

import java.lang.annotation.*;

/**
 * Created by liolay on 2017/3/31.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MvcConfiguration {
}

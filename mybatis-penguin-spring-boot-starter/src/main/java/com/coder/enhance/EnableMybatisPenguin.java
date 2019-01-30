package com.coder.enhance;

import java.lang.annotation.*;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author jeffy
 * @date 2019/1/25
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ImportAutoConfiguration(MybatisAutoConfiguration.class)
public @interface EnableMybatisPenguin {
}

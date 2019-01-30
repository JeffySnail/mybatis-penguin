package com.coder.enhance;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

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

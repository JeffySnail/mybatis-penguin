package com.coder.enhance.annotation;

import java.lang.annotation.*;

/**
 * order by commend
 *
 * @author jeffy
 * @email jeffysnail@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OrderBy {

    String value();
}

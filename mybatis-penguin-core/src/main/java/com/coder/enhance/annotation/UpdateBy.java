package com.coder.enhance.annotation;

import java.lang.annotation.*;

/**
 * update by commend
 *
 * @author jeffy
 * @email jeffysnail@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface UpdateBy {
    String[] value();
}

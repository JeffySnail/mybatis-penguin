package com.coder.enhance.annotation;

import java.lang.annotation.*;

/**
 * table name and comment
 * @author jeffy
 * @email jeffysnail@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Table {

    /**
     * table name
     */
    String name();

    /**
     * comment
     */
    String comment();
}

package com.coder.enhance.annotation;

import java.lang.annotation.*;

/**
 * database column name ,comment and so properties
 * @author jeffy
 * @email jeffysnail@gmail.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Column {

    /**
     * field name
     */
    String name();

    /**
     * field type
     */
    String type();

    /**
     * field length
     */
    int length() default 255;

    /**
     * field comment
     */
    String comment() default "";

    /**
     * nullable
     */
    boolean nullable() default false;

    /**
     * is primary key
     */
    boolean isKey() default false;

    /**
     * default value
     */
    String defaultValue() default "";

    /**
     * is unique
     */
    boolean isUnique() default false;

}

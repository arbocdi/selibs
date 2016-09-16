/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author root
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Cascade {
    public String  action() default " ON DELETE CASCADE ON UPDATE CASCADE ";
}

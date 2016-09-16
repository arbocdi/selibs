package net.sf.selibs.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author selibs
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JoinField {
    
    public String name() ;

    public String referencedColumnName();
    
    public Class referencedClass();

}

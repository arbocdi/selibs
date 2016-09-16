package net.sf.selibs.messaging.beans;

import java.io.Serializable;
import lombok.Data;

@Data
public class BeanMessage implements Serializable {

    public String beanName;
    public String methodName;
    public Class[] paramClasses;
    public Serializable payload;
}

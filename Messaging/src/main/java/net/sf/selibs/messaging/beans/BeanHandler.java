package net.sf.selibs.messaging.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.graph.Init;
import net.sf.selibs.utils.graph.Node;
import net.sf.selibs.utils.misc.MethodInvoker;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Slf4j
@Root
public class BeanHandler implements MessageHandler {

    @Node
    @Getter
    @ElementMap
    protected Map<String, Object> beans = new ConcurrentHashMap();

    @Override
    public void setReady(boolean ready) {
    }

    @Override
    public void init() {
        try {
            GraphUtils.invokeAnnotatedMethods(beans.values(), Init.class);
        } catch (Exception ex) {
            log.warn("Cant properly init beans", ex);
        }
    }

    @Override
    public Message exchange(Message request) throws Exception {
        BeanMessage bRequest = (BeanMessage) request.payload;
        Object bean = beans.get(bRequest.beanName);
        BeanMessage bResponse = new BeanMessage();
        bResponse.payload = (Serializable) MethodInvoker.invoke(bean, bRequest.methodName, bRequest.paramClasses, (Object[]) bRequest.payload);
        Message response = request.createOKResponce();
        response.payload = bResponse;
        return response;
    }

}

package net.sf.selibs.messaging.beans;

import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.MessageExchanger;
import net.sf.selibs.messaging.MessageStatus;
import net.sf.selibs.utils.misc.MethodInvoker;

public class BeanME {

    public MessageExchanger me;
    public String source = "beanClient";
    public String dest = "beanHandler";

    public Object exchange(String beanName, String methodName,Class[] paramClasses,Object[] params) throws Exception {

        Message request = new Message();
        request.source = source;
        request.destination = dest;
        request.status = MessageStatus.OK;

        BeanMessage bRequest = new BeanMessage();
        bRequest.beanName = beanName;
        bRequest.methodName = methodName;
        bRequest.paramClasses = paramClasses;
        bRequest.payload = params;

        request.payload = bRequest;
        try {
            Message response = this.me.exchange(request);
            BeanMessage bResponse = (BeanMessage) response.payload;
            return bResponse.payload;
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
    
}

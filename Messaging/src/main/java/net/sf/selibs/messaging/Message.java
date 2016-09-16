package net.sf.selibs.messaging;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Message implements Serializable {

    private static final long serialVersionUID = 1l;

    public String source;
    public String destination;
    public MessageStatus status;
    public Serializable payload;
    
    public Message createOKResponce(){
        Message responce = new Message();
        responce.destination = this.source;
        responce.source = this.destination;
        responce.status = MessageStatus.OK;
        return responce;
    }
    public Message createErrorResponce(Exception ex){
        Message error = this.createOKResponce();
        error.payload = ex;
        error.status = MessageStatus.ERROR;
        return error;
    }
}

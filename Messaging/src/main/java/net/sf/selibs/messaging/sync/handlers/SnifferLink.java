package net.sf.selibs.messaging.sync.handlers;

import lombok.Data;
import net.sf.selibs.messaging.Message;

@Data
public class SnifferLink extends HandlerChain {
    
    protected MessageDestination dest= new EmptyDestination();

    @Override
    public void setReady(boolean ready) {
    }

    @Override
    public Message exchange(Message request) throws Exception {
        dest.addRequest(request);
        try {
            Message response = this.next.exchange(request);
            dest.addResponse(response);
            return response;
        } catch (Exception ex) {
            this.dest.addException(ex);
            throw ex;
        }
    }

    public static interface MessageDestination {

        void addRequest(Message m);

        void addResponse(Message m);

        void addException(Exception ex);
    }
    
    public static class EmptyDestination implements MessageDestination{

        @Override
        public void addRequest(Message m) {
        }

        @Override
        public void addResponse(Message m) {
        }

        @Override
        public void addException(Exception ex) {
        }
        
    }

    public static class StdoutDestination implements MessageDestination {

        @Override
        public void addRequest(Message m) {
            System.out.println("#REQUEST#");
            System.out.println(m);
        }

        @Override
        public void addResponse(Message m) {
            System.out.println("#RESPONSE#");
            System.out.println(m);
        }

        @Override
        public void addException(Exception ex) {
            ex.printStackTrace();
        }

    }

}

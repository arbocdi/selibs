package net.sf.selibs.utils.sm;

import java.util.LinkedList;
import java.util.List;

public class MessageContext extends Context {

    protected List<String> values = new LinkedList();

    public MessageContext() {

    }

    public void addStates() {
        this.states.add(new MesageState("message1"));
        this.states.add(new MesageState("message2"));
        this.states.add(new MesageState("message3"));
        this.states.add(new EndState("end"));
    }

    public static class MesageState implements State<MessageContext> {

        protected String message;

        public MesageState(String message) {
            this.message = message;
        }

        @Override
        public void goNext(MessageContext c) throws Exception {
            c.values.add(message);
            c.goNext();
        }

    }

    public static class EndState implements State<MessageContext> {

        protected String message;

        public EndState(String message) {
            this.message = message;
        }

        @Override
        public void goNext(MessageContext c) throws Exception {
            c.values.add(message);
        }

    }

}

package net.sf.selibs.messaging;

public interface MessageExchanger {
    Message exchange(Message request) throws Exception;
}

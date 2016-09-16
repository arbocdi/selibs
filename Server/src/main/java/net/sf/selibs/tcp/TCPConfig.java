package net.sf.selibs.tcp;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Data
public class TCPConfig {

    @Element(required = false)
    public String ip;
    @Element
    public int port;
    @Element(required = false)
    public Integer backlog;
    @Element(required = false)
    public Integer timeout;

    public TCPConfig() {
    }

    public TCPConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public TCPConfig(String ip, int port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    public TCPConfig(String ip, int port, int timeout, int backlog) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
        this.backlog = backlog;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.tcp.links;

import lombok.Getter;
import net.sf.selibs.utils.chain.HLink;
import net.sf.selibs.utils.misc.SyncCounter;
import org.simpleframework.xml.Root;

@Root
public class ConnectionCounterLink extends HLink<TCPMessage, Void> {

    @Getter
    protected SyncCounter connected = new SyncCounter();
    @Getter
    protected SyncCounter disconnected = new SyncCounter();

    @Override
    public Void handle(TCPMessage i) throws Exception {
        connected.increment();
        try {
            this.next.handle(i);
        } finally {
            disconnected.increment();
        }
        return null;
    }

}

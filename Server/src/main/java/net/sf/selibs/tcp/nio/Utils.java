/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.tcp.nio;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import net.sf.selibs.utils.misc.UHelper;

/**
 *
 * @author root
 */
public class Utils {

    public static void close(Connection con, Selector selector) {
        selector.wakeup();
        SocketChannel sc = con.getChannel();
        if (sc == null) {
            return;
        }
        SelectionKey key = sc.keyFor(selector);
        if (key != null) {
            key.cancel();
        }
        UHelper.close(sc);
    }

    public static void closeAllConnections(Selector selector) {
        selector.wakeup();
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            close((Connection) key.attachment(), selector);
        }
    }
}

package net.sf.selibs.messaging.beans;

import java.util.LinkedList;
import java.util.List;

public class Bean2 {

    public List<String> actions = new LinkedList();

    public void doAction(String action) {
        actions.add(action);
    }
}

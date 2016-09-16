package net.sf.selibs.utils.graph;

import java.util.LinkedList;
import java.util.List;

public class SomeHandler {

    public List<String> events = new LinkedList();

    @Init
    private void initMethod1() {
        System.out.println("init1 completed");
        events.add("init1");
    }

    @Init
    void initMethod2() {
        System.out.println("init2 completed");
        events.add("init2");
    }
}

package net.sf.selibs.utils.service;

import java.util.LinkedList;
import java.util.List;


public class TService extends Service {
    
    public List<String> actions;

    @Override
    protected void doStuff() {
        actions.add("stuff");
    }
    @Override
    protected void preAction(){
        actions = new LinkedList();
    }
    @Override
    protected void postAction(){
        actions.add("post");
    }
    
}

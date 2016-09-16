package net.sf.selibs.utils.sm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Context implements Serializable{

    @Getter
    @ElementList
    protected List<State> states=new ArrayList();
    private int stateNum = -1;

    public void goNext() throws Exception {
        stateNum++;
        State current = states.get(stateNum);
        current.goNext(this);
    }
    public void addState(State state){
        states.add(state);
    }

}

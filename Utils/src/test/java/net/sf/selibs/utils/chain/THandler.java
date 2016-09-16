package net.sf.selibs.utils.chain;

import lombok.Data;
import net.sf.selibs.utils.graph.Init;

@Data
public class THandler implements Handler {
    
    public boolean initCompleted = false;
    
    protected String name;

    public THandler(String name) {
        this.name = name;
    }

    @Override
    public Object handle(Object i) throws HException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Init
    public void init() throws HException {
        initCompleted = true;
    }
}

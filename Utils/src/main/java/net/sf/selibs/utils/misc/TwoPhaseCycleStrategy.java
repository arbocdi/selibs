package net.sf.selibs.utils.misc;

import java.util.HashMap;
import java.util.Map;
import org.simpleframework.xml.strategy.CycleException;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.Node;
import org.simpleframework.xml.stream.NodeMap;

public class TwoPhaseCycleStrategy extends CycleStrategy {

    String id;
    String ref;
    public static boolean debug = false;

    public TwoPhaseCycleStrategy(String id, String ref) {
        super(id, ref);
        this.id = id;
        this.ref = ref;
    }

    public Map<String, Value> lookup = new HashMap<String, Value>();

    @SuppressWarnings("rawtypes")
    @Override
    public Value read(Type type, NodeMap node, Map map) throws Exception {
        System.out.println("================================");
        System.out.println(type);
        System.out.println(node);
        System.out.println(map);
        System.out.println(lookup);
        
        Value value = null;
        Node refNode = node.get(ref);
        Node keyNode = node.get(id);
        try {
            value = super.read(type, node, map);
            if (keyNode != null) {
                String key = keyNode.getValue();
                if (value != null) {
                    lookup.put(key, value);
                }
            }
        } catch (CycleException ce) {
            if (ce.getMessage().contains("Invalid reference")) {
                if (refNode != null) {
                    String key = refNode.getValue();
                    if (lookup.containsKey(key)) {
                        value = lookup.get(key);
                    }
                }
            } else {
                throw ce;
            }
        }
        System.out.println(value);
        System.out.println("================================");
        return value;
    }

}

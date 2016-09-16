package net.sf.selibs.utils.inject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CarGraph {

    public Car car;
    public List<Wheel> wheels = new LinkedList();
    public Map<String, Bolt> bolts = new HashMap();
    public Driver driver;
    public Manufacturer m;
    public Manufacturer bm;

    public CarGraph() {
        bm = new Manufacturer("bolt manufacturer");
        m = new Manufacturer("wheel manufacturer");
        driver = new Driver("arboc");

        bolts.put("bolt1", new Bolt());
        bolts.put("bolt2", new Bolt());
        bolts.put("bolt3", new Bolt());

        Wheel w1 = new Wheel();
        w1.bolts = bolts;
        Wheel w2 = new Wheel();
        w2.bolts = bolts;

        car = new Car();
        car.wheels.add(w1);
        car.wheels.add(w2);

    }

}

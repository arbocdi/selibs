package net.sf.selibs.tcp.nio.module;

import junit.framework.Assert;
import net.sf.selibs.tcp.nio.module.SpeedCalculator.SpeedData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpeedCalculatorTest {

    public SpeedCalculatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSpeedDataGetSpeed() {
        System.out.println("=========SpeedCalculatorTest:testSpeedDataGetSpeed==========");
        SpeedData sd = new SpeedData();
        sd.counter = 900;
        sd.intervalStart = 10;
        sd.intervalEnd = 100;

        double speed = sd.getSpeed();
        System.out.println(speed);
        Assert.assertEquals(10000.0, speed, 1);
    }

    @Test
    public void testGeneral() throws InterruptedException {
        System.out.println("=========SpeedCalculatorTest:testGeneral==========");
        SpeedCalculator sc = new SpeedCalculator();
        sc.setInterval(200);
        sc.setStartTime(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(2);
            sc.increment();
        }
        double speed = sc.getAverageSpeed(System.currentTimeMillis());

        System.out.println("total count = " + sc.getValue());
        System.out.println("average speed = " + speed);

        Assert.assertEquals(1000, sc.getValue());
        Assert.assertTrue(speed >= 400);

        for (SpeedData sd : sc.getSpeedData()) {
            System.out.println(sd);
            System.out.println(sd.getSpeed());
            Assert.assertTrue(sd.getSpeed() >= 400);
        }

    }

}

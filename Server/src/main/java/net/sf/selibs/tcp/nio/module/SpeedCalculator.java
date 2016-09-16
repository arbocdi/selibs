package net.sf.selibs.tcp.nio.module;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.Getter;

public class SpeedCalculator {

    //config============
    protected int interval = 10000;
    protected long startTime;
    //work==============
    protected int value = 0;
    @Getter
    protected List<SpeedData> speedData = new LinkedList();
    protected SpeedData currentSpeedData;

    public synchronized int increment() {
        value++;
        this.currentSpeedData.counter++;
        long now = System.currentTimeMillis();
        if (now - this.currentSpeedData.intervalStart >= this.interval) {
            this.currentSpeedData.intervalEnd = System.currentTimeMillis();
            speedData.add(currentSpeedData);
            this.currentSpeedData = new SpeedData();
            this.currentSpeedData.intervalStart = now;
        }
        return value;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized long getStartTime() {
        return startTime;
    }

    public synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
        this.currentSpeedData = new SpeedData();
        this.currentSpeedData.intervalStart = this.startTime;
    }

    public synchronized int getInterval() {
        return interval;

    }

    public synchronized void setInterval(int interval) {
        this.interval = interval;
    }

    public synchronized double getAverageSpeed(long now) {
        double speed = (this.value * 1000.0) / (now - this.startTime);
        return Math.round(speed * 100) / 100.0;
    }

    @Data
    public static class SpeedData {

        protected long intervalStart;
        protected long intervalEnd;
        protected int counter;

        public double getSpeed() {
            double speed = (this.counter * 1000.0) / (this.intervalEnd - this.intervalStart);
            return Math.round(speed * 100) / 100.0;
        }
    }

}

package net.sf.selibs.utils.misc;

public class SyncCounter {

    private int value = 0;

    public synchronized int increment(int incr) {
        value += incr;
        return value;
    }

    public synchronized int increment() {
        value++;
        return value;
    }
    public synchronized int decrement() {
        value--;
        return value;
    }
     public synchronized int decrement(int val) {
        value-=val;
        return value;
    }

    public synchronized int get() {
        return this.value;
    }

    public synchronized void reset() {
        this.value = 0;
    }

    @Override
    public synchronized String toString() {
        return String.format("value = %s", this.value);
    }
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.http.nio;

import java.util.Arrays;

/**
 * @author ashevelev
 */
public class ByteStack {

    protected byte[] stack;
    protected int counter = 0;

    public ByteStack(int size) {
        this.stack = new byte[size];
    }

    public void addByte(byte value) {
        counter++;
        this.shiftLeft();
        if (counter < stack.length) {
            stack[counter] = value;
        } else {
            stack[stack.length - 1] = value;
        }
    }

    protected void shiftLeft() {
        for (int i = 1; i < stack.length; i++) {
            stack[i - 1] = stack[i];
        }
    }

    public byte[] getStack() {
        return this.stack;
    }

    public boolean equalsArray(byte[] array) {
        return Arrays.equals(this.stack, array);
    }
}

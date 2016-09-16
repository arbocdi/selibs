package com.megacom.ashevelev.utils.line;

public interface LineProcessor<V> {
    void init();
    V processLine(String line,int lineNum) throws Exception;
    void close();
}

package com.megacom.ashevelev.utils.line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@Root
public class LineReader {

    //=================
    @Element
    protected String encoding = "UTF-8";
    @Element(required = false)
    protected LineProcessor lineProcessor;
    //=================
    protected BufferedReader rd;

    public LineReader(LineProcessor lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public void read(File f1) throws Exception {
        try {
            rd = new BufferedReader(new InputStreamReader(new FileInputStream(f1), encoding));
            String line = null;
            int lineNum = 0;
            this.lineProcessor.init();
            while ((line = rd.readLine()) != null) {
                lineNum++;
                this.lineProcessor.processLine(line, lineNum);
            }
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (Exception ex) {
                }
            }
            this.lineProcessor.close();
        }

    }
}

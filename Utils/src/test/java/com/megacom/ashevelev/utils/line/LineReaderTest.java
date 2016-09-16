package com.megacom.ashevelev.utils.line;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author ashevelev
 */
public class LineReaderTest {

    public LineReaderTest() {
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

    @Test(expected = IOException.class)
    public void testRead() throws Exception {
        System.out.println("==========testRead==============");
        String testFile = "testData/lineReader.txt";
        LineCounter cnt = Mockito.spy(new LineCounter());
        LineReader rd = new LineReader(cnt);
        rd.read(new File(testFile));
        System.out.println(cnt);
        Assert.assertEquals(5, cnt.counter);
        rd.rd.read();
        Mockito.verify(cnt, Mockito.times(1)).init();
        Mockito.verify(cnt, Mockito.times(1)).close();
    }

    public static class LineCounter implements LineProcessor {

        public int counter = 0;

        @Override
        public Object processLine(String line, int lineNum) throws Exception {
            this.counter++;
            return null;
        }

        @Override
        public String toString() {
            return "LineCounter{" + "counter=" + counter + '}';
        }

        @Override
        public void init() {
        }

        @Override
        public void close() {
        }

    }
}

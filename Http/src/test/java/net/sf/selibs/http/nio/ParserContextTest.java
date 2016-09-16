package net.sf.selibs.http.nio;

import java.util.Arrays;
import net.sf.selibs.http.constants.HTTPSamples;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParserContextTest {

    ParserContext ctx;

    public ParserContextTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ctx = new ParserContext();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseBytes method, of class ParserContext.
     */
    @Test
    public void testParseByteContentLen() throws Exception {
        System.out.println("==============ParserContext:testParseByteContentLen============");
        byte[] data = HTTPSamples.message.getBytes("UTF-8");
        for (int i = 0; i < data.length; i++) {
            boolean result = ctx.parseByte(data[i]);
            if (i == data.length - 1) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
            }
        }
        System.out.println(ctx.message);
        Assert.assertEquals(HTTPSamples.message, ctx.message.toString());
    }

    @Test
    public void testParseNoBody() throws Exception {
        System.out.println("==============ParserContext:testParseNoBody============");
        byte[] data = HTTPSamples.OK_HEADER.getBytes("UTF-8");
        for (int i = 0; i < data.length; i++) {
            boolean result = ctx.parseByte(data[i]);
            if (i == data.length - 1) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
            }
        }
        System.out.println(ctx.message);
        Assert.assertEquals(HTTPSamples.OK_HEADER, ctx.message.toString());
    }

    @Test
    public void testParseChunked() throws Exception {
        System.out.println("==============ParserContext:testParseChunked============");
        byte[] data = HTTPSamples.chunkedMessage.getBytes("UTF-8");
        for (int i = 0; i < data.length; i++) {
            boolean result = ctx.parseByte(data[i]);
            if (i == data.length - 1) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
            }
        }
        System.out.println(ctx.message);
        Assert.assertEquals(HTTPSamples.chunkedMessage, ctx.message.toString());
    }

    @Test
    public void testParseZeroLenReply() throws Exception {
        System.out.println("==============ParserContext:testParseZeroLenReply============");
        byte[] data = HTTPSamples.ZERO_LEN_REPLY.getBytes("UTF-8");
        for (int i = 0; i < data.length; i++) {
            boolean result = ctx.parseByte(data[i]);
            if (i == data.length - 1) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
            }
        }
        System.out.println(ctx.message);
        Assert.assertEquals(HTTPSamples.ZERO_LEN_REPLY, ctx.message.toString());
    }

    @Test(expected = ParserException.class)
    public void testParseContentLenAdditionalBytes() throws Exception {
        System.out.println("==============ParserContext:testParseContentLenAdditionalBytes============");
        try {
            byte[] data = HTTPSamples.message.getBytes("UTF-8");
            data = Arrays.copyOf(data, data.length + 1);
            ctx.parseBytes(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}

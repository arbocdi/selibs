package net.sf.selibs.http;

import java.net.URI;
import junit.framework.Assert;
import net.sf.selibs.http.RequestLine.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class RequestLineTest {
    
    
    
    public RequestLineTest() {
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
    public void testToFromString() throws Exception {
        System.out.println("===========RequestLine.Query:testToFromString==========");
        Query q = new Query();
        q.data.put("key1", "val1");
        q.data.put("key2", "val2");
        
        Query q2 = Query.fromString(q.toString());
        System.out.println(q2);
        Assert.assertEquals(q2.toString(), q.toString());
    }
    @Test
    public void testParseQuery() throws Exception{
        System.out.println("================RequestLine.testParseQuery====================");
        RequestLine rl = new RequestLine();
        Query q = new Query();
        q.data.put("key1", "val1");
        q.data.put("key2", "val2");
        rl.url = new URI("http://myhost/test.html?"+q);
        System.out.println(rl.url.getQuery());
        Query q2 = rl.parseQuery();
        Assert.assertEquals(q2.toString(), q.toString());
    }

    
    
}

package net.sf.selibs.utils.io;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileUtilsTest {

    File dir1;
    File dir2;
    File file1;

    public FileUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        dir1 = new File("dir1");
        dir2 = new File("dir1/dir2");
        file1 = new File("dir1/dir2/test.txt");

        dir1.mkdir();
        dir2.mkdir();
        file1.createNewFile();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.delete(dir1, false);
    }

    @Test
    public void testDeleteKeepDirs() throws IOException {
        System.out.println("========FileUtilsTest:testDeleteKeepDirs===========");
        FileUtils.delete(dir1, true);
        Assert.assertTrue(dir1.exists());
        Assert.assertTrue(dir2.exists());
        Assert.assertFalse(file1.exists());
    }
     @Test
    public void testDeleteAll() throws IOException {
        System.out.println("========FileUtilsTest:testDeleteAll===========");
        FileUtils.delete(dir1, false);
        Assert.assertFalse(dir1.exists());
        Assert.assertFalse(dir2.exists());
        Assert.assertFalse(file1.exists());
    }
    @Test
    public void testReadFromCP() throws Exception{
         System.out.println("========FileUtilsTest:testReadFromCP===========");
         byte[]data=FileUtils.readFromCP(FileUtils.class, "/net/sf/selibs/utils/velocity/hello.vm");
         String dataStr = new String(data,"UTF-8");
         System.out.println(dataStr);
         Assert.assertTrue(dataStr.contains("Hello"));
    }

}

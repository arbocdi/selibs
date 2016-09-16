/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.megacom.ashevelev.utils.ftp;

import com.megacom.ashevelev.utils.ftp.AFTPClient.ProgressMonitor;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ashevelev
 */
public class AFTPClientTest {

    FTPConfig cfg;
    static FtpServer server;
    File file1;

    public AFTPClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FtpException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);

        FtpServerFactory serverFactory = new FtpServerFactory();

        UserManager uman = new PropertiesUserManager(new ClearTextPasswordEncryptor(), new File("ftpUsers.properties"), "admin");
        serverFactory.setUserManager(uman);

        ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(2354);
        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        // start the server
        server = serverFactory.createServer();
        server.start();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        cfg = new FTPConfig();
        cfg.setHost("127.0.0.1");
        cfg.setPort(2354);
        cfg.setUsername("admin");
        cfg.setPassword("admin");

        for (File f : (new File("ftproot")).listFiles()) {
            f.delete();
        }
        for (File f : (new File("cltroot")).listFiles()) {
            f.delete();
        }
        file1 = new File("cltroot/file1");
        FileUtils.write(file1, "Hello World!", "UTF-8");

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testUploadDownload() throws Exception {
        System.out.println("========AFTPClientTest:testUploadDownload===========");
        AFTPClient clt = new AFTPClient(this.cfg);
        try {
            clt.connect();
            clt.uploadFile(file1.getAbsolutePath(), "file1");
            clt.downloadFile("cltroot/file2", "file1", false);
            File file2 = new File("cltroot/file2");
            String expected = FileUtils.readFileToString(new File("cltroot/file1"), "UTF-8");
            String result = FileUtils.readFileToString(file2, "UTF-8");
            Assert.assertEquals(expected, result);
        } finally {
            clt.disconnect();
        }
    }

    @Test
    @Ignore
    public void testUploadDownloadMonitor() throws Exception {
        System.out.println("========AFTPClientTest:testUploadDownloadMonitor===========");
        AFTPClient clt = new AFTPClient(this.cfg);
        try {
            clt.connect();
            boolean success = clt.uploadFile(file1.getAbsolutePath(), "file1", new SoutPM());
            System.out.println(success);
            System.out.println(clt.getFtpClient().getReplyString());
            clt.downloadFile("cltroot/file2", "file1", false, new SoutPM());
            File file2 = new File("cltroot/file2");
            String expected = FileUtils.readFileToString(new File("cltroot/file1"), "UTF-8");
            String result = FileUtils.readFileToString(file2, "UTF-8");
            Assert.assertEquals(expected, result);
        } finally {
            clt.disconnect();
        }
    }

    public static class SoutPM implements ProgressMonitor {

        @Override
        public void progress(long size, long processed) {
            System.out.println(String.format("Total size is %s bytes, processed %s bytes", size, processed));
        }

    }
}

package com.megacom.ashevelev.utils.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.misc.UHelper;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author ashevelev
 */
@Root
/**
 * boolean retrieveFile(String remote, OutputStream local): This method
 * retrieves a remote file whose path is specified by the parameter remote, and
 * writes it to the OutputStream specified by the parameter local. The method
 * returns true if operation completed successfully, or false otherwise. This
 * method is suitable in case we don’t care how the file is written to disk,
 * just let the system use the given OutputStream to write the file. We should
 * close OutputStream the after the method returns.
 *
 * InputStream retrieveFileStream(String remote): This method does not use an
 * OutputStream, instead it returns an InputStreamwhich we can use to read bytes
 * from the remote file. This method gives us more control on how to read and
 * write the data. But there are two important points when using this method:
 * The method completePendingCommand() must be called afterward to finalize file
 * transfer and check its return value to verify if the download is actually
 * done successfully. We must close the InputStream explicitly.
 *
 * FTP uses separate control and data connections between the client and the
 * server.
 *
 * control(21) |-----| ------> |-----| | CLT | | SRV | |-----| <-----> |-----|
 * data
 *
 * In active mode, the client establishes the command channel to server port 21
 * and the server establishes the data channel (from server port 20(b) to client
 * port Y, where Y has been supplied by the client).
 *
 * In passive mode, the client establishes both channels. In that case, the
 * server tells the client which port should be used for the data channel.
 *
 */

public class AFTPClient {

    @Element(name = "ftpConfig")
    @Getter
    @Setter
    protected FTPConfig ftpConfig;
    @Getter
    protected FTPClient ftpClient;

    public AFTPClient(@Element(name = "ftpConfig") FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    /**
     * Подключимся и авторизуемся.
     *
     * @return
     * @throws IOException
     */
    public FTPClient connect() throws IOException {
        ftpClient = new FTPClient();
        if (this.ftpConfig.getTimeout() != null) {
            //connect timeout
            ftpClient.setDefaultTimeout(ftpConfig.getTimeout());
            //timeout when reading data
            ftpClient.setDataTimeout(ftpConfig.getTimeout());
        }

        ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
        // After connection attempt, you should check the reply code to verify
        // success.
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            throw new IOException(String.format("Wrong reply code %s %s", reply, ftpClient.getReplyString()));
        }
        //The thing is you want to enter passive mode after you connect, but before you log in. 
        ftpClient.enterLocalPassiveMode();
        if (this.ftpConfig.getTimeout() != null) {
            //read timeout for opened socket connection
            this.ftpClient.setSoTimeout(this.ftpConfig.getTimeout());
            //send periodically NOOP commands to keep control connection alive, in seconds
            this.ftpClient.setControlKeepAliveTimeout(this.ftpConfig.getTimeout() / 2000);
        }
        boolean auth;
        if (ftpConfig.getUsername() == null) {
            auth = ftpClient.login("anonymous", "guest@gmail.com");
        } else {
            auth = ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        }
        if (!auth) {
            throw new IOException("Username/password are not valid");
        }
        //set buffer size here or download will be slow
        //Set the internal buffer size for data sockets. 
        ftpClient.setBufferSize(1024 * 1024);
        return ftpClient;
    }

    public void disconnect() throws IOException {
        if (this.ftpClient.isConnected()) {
            this.ftpClient.logout();
            this.ftpClient.disconnect();
        }
    }

    /**
     * Получим список файлов в определенном каталоге.
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public FTPFile[] getFileList(String dir) throws IOException {
        return ftpClient.listFiles(dir);
    }

    /**
     * Зальем файл на сервер.
     *
     * @param localFile локальное имя
     * @param remoteFile имя на сервере
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String localFile, String remoteFile) throws IOException {
        //set binary transfer mode
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        boolean uploaded = false;
        BufferedInputStream bin = null;;
        try {
            bin = new BufferedInputStream(new FileInputStream(localFile));
            uploaded = ftpClient.storeFile(remoteFile, bin);
        } finally {
            UHelper.close(bin);
        }
        return uploaded;
    }

    /**
     * Загрузим файл с фтп-сервера.
     *
     * @param localFile
     * @param remoteFile
     * @return
     * @throws IOException
     */
    public boolean downloadFile(String localFile, String remoteFile, boolean append) throws IOException {
        //set binary transfer mode
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        boolean downloaded = false;
        BufferedOutputStream bout = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(localFile, append));
            downloaded = ftpClient.retrieveFile(remoteFile, bout);
        } finally {
            UHelper.close(bout);
        }
        return downloaded;
    }

    public static interface ProgressMonitor {

        void progress(long size, long processed);
    }

    public boolean downloadFile(String localFile, String remoteFile, boolean append, ProgressMonitor pm) throws IOException {
        OutputStream local = null;
        InputStream remote = null;
        try {
            local = new FileOutputStream(localFile, append);
            remote = ftpClient.retrieveFileStream(remoteFile);
            if (!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())
                    || remote == null) {
                return false;
            }
            FTPFile[] files = this.ftpClient.listFiles();
            for (FTPFile file : files) {
                System.out.println(file);
            }
            long fileSize = ftpClient.mlistFile(remoteFile).getSize();
            long bytesTransferred = 0;
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = remote.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                local.write(buffer, 0, bytesRead);
                bytesTransferred += bytesRead;
                pm.progress(fileSize, bytesTransferred);
            }
            return this.ftpClient.completePendingCommand();
        } finally {
            UHelper.close(local);
            UHelper.close(remote);
        }
    }

    public boolean uploadFile(String localFile, String remoteFile, ProgressMonitor pm) throws IOException {
        InputStream local = null;
        OutputStream remote = null;
        try {
            local = new FileInputStream(localFile);
            remote = ftpClient.storeFileStream(remoteFile);
            if (!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())
                    || remote == null) {
                return false;
            }
            long fileSize = (new File(localFile)).length();
            long bytesTransferred = 0;
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = local.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                remote.write(buffer, 0, bytesRead);
                bytesTransferred += bytesRead;
                pm.progress(fileSize, bytesTransferred);
            }
            return this.ftpClient.completePendingCommand();
        } finally {
            UHelper.close(local);
            UHelper.close(remote);
        }
    }

    /**
     * Удалим файл с фтп-сервера.
     *
     * @param remoteFile
     * @return
     * @throws IOException
     */
    public boolean deleteFile(String remoteFile) throws IOException {
        return ftpClient.deleteFile(remoteFile);
    }

    /**
     * Создадим каталог на сервере.
     *
     * @param remoteDir
     * @return
     * @throws IOException
     */
    public boolean mkdir(String remoteDir) throws IOException {
        ftpClient.changeToParentDirectory();
        return ftpClient.makeDirectory(remoteDir);
    }

    /**
     * Вернуть список имен файлов.
     *
     * @param files
     * @return
     */
    public static List<String> toStringList(FTPFile[] files) {
        List<String> filesStr = new LinkedList();
        for (FTPFile file : files) {
            filesStr.add(file.getName());
        }
        return filesStr;
    }

}

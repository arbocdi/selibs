/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.megacom.ashevelev.utils.ftp;

import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author root
 */
public class FTPUtils {

    public static FTPFile getFTPFile(FTPClient ftp, String fileName) throws IOException {
        for (FTPFile file : ftp.listFiles()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }
}

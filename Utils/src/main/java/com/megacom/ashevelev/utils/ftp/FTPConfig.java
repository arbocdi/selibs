/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.megacom.ashevelev.utils.ftp;

import lombok.Data;
import lombok.ToString;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author ashevelev
 */
@Root
@Data
public class FTPConfig {

    @Element
    private String host;
    @Element
    private int port = 21;
    @Element(required = false)
    private Integer timeout = 60000;
    @Element(required = false)
    private String username;
    @Element(required = false)
    private String password;

    @Override
    public String toString() {
        return "FTPConfig{" + "host=" + host + ", port=" + port + ", username=" + username + '}';
    }

}

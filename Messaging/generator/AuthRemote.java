package com.megacom.wcse.clt;

import net.sf.selibs.messaging.beans.generator.Implement;

@Implement
public interface AuthRemote {

    public String Login(String sessionID, final String signature, final int browser) throws Exception;

    public short CheckAuth(String sessionID) throws Exception;

    public void Logout(String sessionID) throws Exception;

    public long ChangePass(String sessionID, String oldPass, final String newPass) throws Exception;

}

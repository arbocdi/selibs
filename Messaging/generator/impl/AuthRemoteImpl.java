package com.megacom.wcse.clt.impl;

import net.sf.selibs.messaging.beans.generator.Implement;
import com.megacom.wcse.clt.*;

@org.simpleframework.xml.Root
public class AuthRemoteImpl implements AuthRemote {

    @javax.inject.Inject
    public net.sf.selibs.messaging.beans.BeanME bme;

    @org.simpleframework.xml.Element
    public String beanName;

    @Override
    public String Login(String sessionID, final String signature, final int browser) throws Exception {
        Object[] params = new Object[] { sessionID, signature, browser };
        Class[] paramClasses = new Class[] { String.class, String.class, int.class };
        Object result = bme.exchange(beanName,"Login",paramClasses,params);
        return (String)result;
    }

    @Override
    public short CheckAuth(String sessionID) throws Exception {
        Object[] params = new Object[] { sessionID };
        Class[] paramClasses = new Class[] { String.class };
        Object result = bme.exchange(beanName,"CheckAuth",paramClasses,params);
        return (Short)result;
    }

    @Override
    public void Logout(String sessionID) throws Exception {
        Object[] params = new Object[] { sessionID };
        Class[] paramClasses = new Class[] { String.class };
        Object result = bme.exchange(beanName,"Logout",paramClasses,params);
    }

    @Override
    public long ChangePass(String sessionID, String oldPass, final String newPass) throws Exception {
        Object[] params = new Object[] { sessionID, oldPass, newPass };
        Class[] paramClasses = new Class[] { String.class, String.class, String.class };
        Object result = bme.exchange(beanName,"ChangePass",paramClasses,params);
        return (Long)result;
    }
}

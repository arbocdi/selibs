package com.megacom.wcse.clt.impl;

import java.util.ArrayList;
import net.sf.selibs.messaging.beans.generator.Implement;
import com.megacom.wcse.clt.*;

@org.simpleframework.xml.Root
public class DealerRemoteImpl implements DealerRemote {

    @javax.inject.Inject
    public net.sf.selibs.messaging.beans.BeanME bme;

    @org.simpleframework.xml.Element
    public String beanName;

    @Override
    public ArrayList<String> GetKassas(String sessionID) throws Exception {
        Object[] params = new Object[] { sessionID };
        Class[] paramClasses = new Class[] { String.class };
        Object result = bme.exchange(beanName,"GetKassas",paramClasses,params);
        return (ArrayList<String>)result;
    }

    @Override
    public Long GetSchedule(String login) throws Exception {
        Object[] params = new Object[] { login };
        Class[] paramClasses = new Class[] { String.class };
        Object result = bme.exchange(beanName,"GetSchedule",paramClasses,params);
        return (Long)result;
    }

    @Override
    public ArrayList<Long> GetSchedules(String sessionID) throws Exception {
        Object[] params = new Object[] { sessionID };
        Class[] paramClasses = new Class[] { String.class };
        Object result = bme.exchange(beanName,"GetSchedules",paramClasses,params);
        return (ArrayList<Long>)result;
    }

    @Override
    public void logout() throws Exception {
        Object[] params = new Object[] {  };
        Class[] paramClasses = new Class[] {  };
        Object result = bme.exchange(beanName,"logout",paramClasses,params);
    }
}

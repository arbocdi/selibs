package com.megacom.wcse.clt;

import java.util.ArrayList;
import net.sf.selibs.messaging.beans.generator.Implement;

@Implement
public interface DealerRemote {

    public ArrayList<String> GetKassas(String sessionID) throws Exception;

    public Long GetSchedule(String login) throws Exception;

    public ArrayList<Long> GetSchedules(String sessionID) throws Exception;

    public void logout() throws Exception;
}

package net.sf.selibs.tcp.links;

import net.sf.selibs.http.HHeader;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.HName;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.servlet.HServlet;
import net.sf.selibs.utils.chain.HException;

public class OKServlet implements HServlet{

    @Override
    public HMessage handle(HMessage i) throws HException {
        HMessage response = HMessage.createResponse(HCodes.OK, "OK");
        HHeader con = new HHeader();
        con.name  = new HName(HNames.CONNECTION);
        con.value = HValues.CLOSE;
        response.addHeader(con);
        return response;
    }

    
    
}

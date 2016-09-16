package net.sf.selibs.http.servlet;

import net.sf.selibs.http.HMessage;
import net.sf.selibs.utils.chain.Handler;
import org.simpleframework.xml.Root;
@Root


public interface HServlet extends Handler<HMessage,HMessage>{
}

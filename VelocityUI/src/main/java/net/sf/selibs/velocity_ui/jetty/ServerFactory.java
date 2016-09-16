package net.sf.selibs.velocity_ui.jetty;

import java.io.File;
import java.io.FileInputStream;
import net.sf.selibs.utils.inject.Injector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ServerFactory {

    @Element
    public File cfgDir;

    public Server createServer() throws Exception {
        XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(this.getJettyXML()));
        Server srv = (Server) configuration.configure();
        for (File file : this.cfgDir.listFiles()) {
            if (!file.equals(this.getJettyXML())) {
                configuration = new XmlConfiguration(new FileInputStream(file));
                configuration.configure(srv);
            }
        }

        return srv;
    }

    public File getJettyXML() {
        return new File(this.cfgDir, "jetty.xml");
    }
}

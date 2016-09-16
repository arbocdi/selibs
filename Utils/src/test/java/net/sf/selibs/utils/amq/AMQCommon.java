package net.sf.selibs.utils.amq;

import java.net.URI;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.log4j.BasicConfigurator;

public class AMQCommon {

    private static BrokerService broker;

    public static String brokerURI = "tcp://localhost:61617";

    public static void createSingletonBroker() throws Exception {
        if (broker == null) {
            BasicConfigurator.configure();
            broker = new BrokerService();
            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI(brokerURI));
            broker.addConnector(connector);
            broker.setUseShutdownHook(true);
            broker.start();
        }
    }
}

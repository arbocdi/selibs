package net.sf.selibs.velocity_ui;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

@Slf4j
public class VelocityLogger implements LogChute {
    

    @Override
    public void init(RuntimeServices rs) throws Exception {
        log.info("Velocity slf4j logger initialized");
    }

    @Override
    public void log(int i, String string) {
        log.debug(string);
    }

    @Override
    public void log(int i, String string, Throwable thrwbl) {
        log.debug(string,thrwbl);
    }

    @Override
    public boolean isLevelEnabled(int i) {
        return true;
    }
    
}

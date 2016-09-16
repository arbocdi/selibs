package net.sf.selibs.utils.inject;

import org.jboss.weld.environment.se.WeldContainer;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author root
 */
public class QuartzWeldJobFactory implements JobFactory {

    protected WeldContainer container;

    public QuartzWeldJobFactory(WeldContainer container) {
        this.container = container;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        try {
            return container.select(bundle.getJobDetail().getJobClass()).get();
        } catch (Exception ex) {
            throw new SchedulerException(ex);
        }
    }

}

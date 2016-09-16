package net.sf.selibs.utils.inject;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzInjectJobFactory implements JobFactory {

    protected Injector i;

    public QuartzInjectJobFactory(Injector i) {
        this.i = i;
    }

    @Override
    public Job newJob(TriggerFiredBundle tfb, Scheduler schdlr) throws SchedulerException {
        try {
            Job job = tfb.getJobDetail().getJobClass().newInstance();
            i.injectInto(job);
            return job;
        } catch (Exception ex) {
            throw new SchedulerException(ex);
        }
    }
    
    protected static class CalcJob implements Job {

        @Inject
        public CalcI calc;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }

    }

    protected static interface CalcI {

        String getResult();
    }

    @Default
    protected static class StaticCalc implements CalcI {

        @Override
        public String getResult() {
            return "5";
        }

    }
}

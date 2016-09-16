/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.inject;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import net.sf.selibs.utils.inject.QuartzInjectJobFactory.CalcJob;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author root
 */
public class QuartzWeldJobFactoryTest {

    public QuartzWeldJobFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNewJob() throws Exception {
        System.out.println("=========QuartzWeldJobFactoryTest:testNewJob===========");
        JobDetail jd = JobBuilder.newJob(CalcJob.class).withIdentity("j1", "g1").build();
        TriggerFiredBundle bundle = new TriggerFiredBundle(jd, null, null, true, null, null, null, null);
        Weld weld = new Weld();
        weld.disableDiscovery();
        weld.addPackage(true, QuartzWeldJobFactory.class);
        WeldContainer container = null;
        try {
            container = weld.initialize();
            QuartzWeldJobFactory factory = new QuartzWeldJobFactory(container);
            CalcJob job = (CalcJob) factory.newJob(bundle, null);
            System.out.println(job.calc.getResult());
            Assert.assertEquals("5", job.calc.getResult());
        } finally {
            container.close();
        }
    }

}

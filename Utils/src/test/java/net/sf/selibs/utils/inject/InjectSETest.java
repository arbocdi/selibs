package net.sf.selibs.utils.inject;

import lombok.Data;
import net.sf.selibs.utils.graph.Node;
import org.junit.Assert;
import org.junit.Test;

public class InjectSETest {

    @Data
    public static class Password {

        protected String value;
    }

    @Data
    public static class User {

        protected String name;
        @InjectSE
        protected Password password;
    }

    @Test
    public void testInjectSE() throws Exception {
        System.out.println("=========InjectSETest:testInjectSE==========");
        Password pwd = new Password();
        pwd.setValue("hello");
        User u = new User();
        Injector inj = new Injector();
        inj.addBinding(Password.class, pwd);
        inj.injectIntoSE(u);
        System.out.println(u);
        Assert.assertSame(pwd, u.getPassword());
    }

    @Data
    public static class User2 {

        protected String name;
        @InjectSE
        @NamedSE(value = "arboc")
        protected Password password;
    }

    @Test
    public void testInjectNamedSE() throws Exception {
        System.out.println("=========InjectSETest:testInjectNamedSE==========");
        Password pwd = new Password();
        pwd.setValue("hello");
        User2 u = new User2();
        Injector inj = new Injector();
        inj.addBinding(Password.class, "arboc", pwd);
        inj.injectIntoSE(u);
        System.out.println(u);
        Assert.assertSame(pwd, u.getPassword());
    }

    @Data
    public static class User3 {

        @Node
        protected User3 u3;

        protected String name;
        @InjectSE
        protected Password password;
    }

    @Test
    public void testInjectGraphSE() throws Exception {
        System.out.println("=========InjectSETest:testInjectGraphSE==========");
        //what to inject===========
        Password pwd = new Password();
        pwd.setValue("hello");
        Injector inj = new Injector();
        inj.addBinding(Password.class, pwd);
        //graph=====================
        User3 u = new User3();
        u.setU3(new User3());
        //injectinh=================
        inj.injectIntoGraphSE(u);
        System.out.println(u);
        Assert.assertSame(pwd, u.getPassword());
        Assert.assertSame(pwd, u.getU3().getPassword());
    }
}

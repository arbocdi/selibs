package net.sf.selibs.velocity_ui;

import java.io.IOException;
import java.io.InputStream;
import net.sf.selibs.utils.io.IOUtils;
import net.sf.selibs.utils.misc.UHelper;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

public class VelocityUtils {
    
    public static final String REPO_NAME="repo";

    public static VelocityEngine createEngine() throws Exception {
        VelocityEngine ve = new VelocityEngine();
        //set slf4j logger
        ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new VelocityLogger());
        //For loading templates from strings we should specify the resource loader as string 
        //and the class for string.resource.loader
        ve.setProperty("resource.loader", "string");
        ve.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
//        If you have concerns about memory leaks or for whatever reason do not 
//        wish to have your string repository stored statically as a class member, 
//        then you should set 'string.resource.loader.repository.static = false' in your properties.
//        This will tell the resource loader that the string repository should be stored in 
//        the Velocity application attributes. To retrieve the repository, do:
//        StringResourceRepository repo = velocityEngine.getApplicationAttribute("foo");

        ve.setProperty("string.resource.loader.repository.static", "false");
//        If there will be multiple StringResourceLoaders used in an application, 
//        you should consider specifying a 'string.resource.loader.repository.name = foo' 
//        property in order to keep you string resources in a non-default repository. 
//        This can help to avoid conflicts between different frameworks or components 
//        that are using StringResourceLoader
        ve.setProperty("string.resource.loader.repository.name", REPO_NAME);
//        Whether your repository is stored statically or in Velocity's
//        application attributes, you can also manually create and set it prior to 
//        Velocity initialization.
        StringResourceRepository repo = new StringResourceRepositoryImpl();
        ve.setApplicationAttribute(REPO_NAME, repo);
        ve.init();
        //read hello template
        addTemplate(VelocityUtils.class, "hello", "hello.vm",ve);
        return ve;
    }
    /**
     * Return velocity template repository from velocity engine.
     * @param ve
     * @return 
     */
    public static StringResourceRepository getRepo(VelocityEngine ve){
        return (StringResourceRepository) ve.getApplicationAttribute(REPO_NAME);
    }
    /**
     * Read template from classpath as string and add it to StringResourceLoader`s repo.
     * @param clazz
     * @param name
     * @param file
     * @param ve
     * @throws IOException 
     */
    public static void addTemplate(Class clazz, String name, String file, VelocityEngine ve) throws IOException {
        InputStream in = null;
        try {
            in = clazz.getResourceAsStream(file);
            String template = new String(IOUtils.readFully(in), "UTF-8");
            getRepo(ve).putStringResource(name, template);
        } finally {
            UHelper.close(in);
        }
    }
}

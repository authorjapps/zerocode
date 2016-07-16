package org.jsmart.smarttester.core.listener;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class EmbeddedTomcatJunitListener {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmbeddedTomcatJunitListener.class);

    private Tomcat tomcat;
    private final int port;
    private final String warLocation;
    private final String context;

    public EmbeddedTomcatJunitListener(int port, String warLocation, String context) {
        super();
        this.port = port;
        this.warLocation = warLocation;
        this.context = context;
    }

    public void startNow() throws Exception {
        System.out.println("### Preparing tomcat");
        logger.info("### Preparing tomcat");
        URL logbackLocation = getClass().getClassLoader().getResource("logback.xml");

        // TODO Take these system properties into a properties file and put them here via a for loop
        System.setProperty("logback.configurationFile", logbackLocation.getPath());
        // TO permit '%2F' and '%5C' as path delimiters
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "*.jar");
        // KAFKA to use KafkatestModule for testing
        System.setProperty("WRITE_QUEUE", "false");

        tomcat = new Tomcat();
        File baseDir = new File(".");
        tomcat.setPort(port);
        /*
         * Added dependency : org.apache.tomcat.embed:tomcat-embed-logging-juli:jar:8.0.28:test
         * for tomcat to start gracefully which otherwise throws error "NoClassDef" @org.apache.catalina.core.StandardHost
         */
        tomcat.addContext("", baseDir.getAbsolutePath());
        File war = new File(warLocation);
        tomcat.addWebapp(context, war.getCanonicalPath());

        tomcat.start();

        logger.info(String.format("### Tomcat Started with WebApp:%s, @Port:%s, @Context:%s", warLocation, port, context));
    }

    public void stopNow() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
            logger.info(String.format("### Tomcat Stopped with WebApp:%s, @Port:%s, @Context:%s", warLocation, port, context));
            tomcat = null;
        }
    }

}

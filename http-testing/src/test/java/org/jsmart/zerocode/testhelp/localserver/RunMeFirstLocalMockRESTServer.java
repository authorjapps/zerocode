package org.jsmart.zerocode.testhelp.localserver;

import org.jsmart.simulator.annotations.ApiRepo;
import org.jsmart.simulator.impl.JsonBasedSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created by Siddha on 27/04/2015.
 *
 * Are you done with the web server ? To stop this REST server, simply press Ctrl+c or Stop button on your IDE
 *
 */
@ApiRepo("localhost_stubs")
public class RunMeFirstLocalMockRESTServer extends JsonBasedSimulator {
    private static final Logger logger = LoggerFactory.getLogger(RunMeFirstLocalMockRESTServer.class);

    public static final int PORT = 9999;

    public RunMeFirstLocalMockRESTServer(int port) {
        super(port);
    }

    public static void main(String[] args) {
        logger.info("\n### REST Helper web-service starting...");

        new RunMeFirstLocalMockRESTServer(PORT).start();

        logger.info("\n### REST Helper web-service started.");

        System.out.println("\n------ Done? To stop this REST server, simply press Ctrl+c or Stop button on your IDE -------");

    }
}

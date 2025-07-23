package org.jsmart.zerocode.testhelp.tests;


import org.jsmart.zerocode.testhelp.localserver.RunMeFirstLocalMockRESTServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class MockServerTest {
    private RunMeFirstLocalMockRESTServer mockRESTServer;

    @Before
    public void start(){
        mockRESTServer = new RunMeFirstLocalMockRESTServer(RunMeFirstLocalMockRESTServer.PORT);
        mockRESTServer.start();
    }

    @After
    public void stop(){
        mockRESTServer.stop();
    }

    @Test
    public void testMockServerRunning(){
        Assert.assertTrue(mockRESTServer.isRunning());
    }

}
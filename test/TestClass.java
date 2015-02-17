/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import chatClient.ChatClient;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Henrik
 */
public class TestClass {

    static ChatClient client;

    public TestClass() throws IOException {

    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        client = new ChatClient("localhost", 9090);

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() throws IOException {

    }

    @After
    public void tearDown() {
        client.send("CLOSE#");
    }

    @Test
    public void testConnect() throws IOException, InterruptedException {

        client.send("CONNECT#TEST");
        Thread.sleep(2000);
        assertEquals("MESSAGE TEST has logged on", client.getMessageTest());
    }

    @Test
    public void testSendToAll() throws InterruptedException {
        ChatClient client2 = new ChatClient("localhost", 9090);
        client.send("CONNECT#TEST");
        Thread.sleep(2000);
        client2.send("CONNECT#TEST2");
        client.send("SEND#*#HEJ TEST2");
        Thread.sleep(2000);
        assertEquals("MESSAGE TEST HEJ TEST2", client2.getMessageTest());
        assertEquals("MESSAGE TEST HEJ TEST2", client.getMessageTest());
        client2.send("CLOSE#");
    }

    @Test
    public void testOnline() throws InterruptedException {
        client.send("CONNECT#TEST");
        Thread.sleep(3000);
        assertEquals("ONLINE TEST", client.getOnlineTest());
    }

    @Test
    public void testClose() throws InterruptedException {
        client.send("CONNECT#TEST");
        Thread.sleep(3000);
        client.send("CLOSE#");
    }

}

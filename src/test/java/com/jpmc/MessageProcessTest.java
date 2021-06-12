package com.jpmc;

import com.jpmc.server.MessageServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Message processing logic test cases
 *
 * @author Stanly
 */
public class MessageProcessTest {

    private MessageServer server = null;
    private Thread thread = null;

    @Before
    public void stop() throws InterruptedException {
        startServer();
    }

    @After
    public void start() throws InterruptedException {
        stopServer();
    }

    @Test
    public void testInput50Msg() throws IOException, InterruptedException {
        pushMessageToServer("src/test/resources/input50.txt");
        assertEquals("Apple Quantity", 23, server.getMessageReceiver().getQuantityByName("apple"));
    }


    @Test
    public void testInput10Msg() throws IOException, InterruptedException {
        pushMessageToServer("src/test/resources/input10.txt");
        assertEquals("Orange Quantity", 20, server.getMessageReceiver().getQuantityByName("orange"));
    }

    @Test
    public void testInput5Msg() throws IOException, InterruptedException {
        pushMessageToServer("src/test/resources/input5.txt");
        assertEquals("Mango Quantity", 11, server.getMessageReceiver().getQuantityByName("mango"));
    }

    @Test
    public void testInput0ReportableMsg() throws IOException, InterruptedException {
        pushMessageToServer("src/test/resources/input0ReportableMsg.txt");
        assertEquals(0f, server.getMessageReceiver().getTotalSalePriceByName("apple"), 0f);
    }

    @Test
    public void testInputInvalideMsg() throws IOException, InterruptedException {
        pushMessageToServer("src/test/resources/inputInvalidMsg.txt");
        assertEquals("Orange Sales", 0f, server.getMessageReceiver().getTotalSalePriceByName("orange"), 0f);
    }

    private void startServer() {
        server = new MessageServer();
        thread = new Thread() {
            public void run() {
                try {
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void stopServer() throws InterruptedException {
        if (thread != null && server != null) {
            server.stopServer();
            thread.interrupt();
            thread = null;
            server = null;
        }
    }

    private void pushMessageToServer(String absolutePath) throws InterruptedException {
        Thread.sleep(4000);
        TCPMsgClient mClient = new TCPMsgClient();
        try {
            mClient.connectToServer();
            mClient.readFileAndPush(absolutePath);
            mClient.close();
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
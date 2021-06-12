package com.jpmc.server;

import com.jpmc.domain.ApplicationEnum;
import com.jpmc.receiver.MessageReceiver;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class acts Messaging Server listen to any client connected.
 *
 * @author Stanly
 */
public class MessageServer {

    private final MessageReceiver receiver = new MessageReceiver();
    private ServerSocket server = null;

    /**
     * Messaging Server listen to any client connected.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        MessageReceiver rec = new MessageReceiver();
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new MessageWorker(listener.accept(), rec).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * Start the server
     *
     * @throws IOException
     */
    public void startServer() throws IOException {

        try {
            server = new ServerSocket(Integer.parseInt(ApplicationEnum.TCP_PORT.getEnumType()));
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                try {
                    new MessageWorker(server.accept(), receiver).start();
                } catch (IOException e) {
                }
            }
        } finally {
            server.close();
        }
    }

    /**
     * Stop the server
     */
    public void stopServer() {
        try {
            if (server != null)
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is used to find the message
     *
     * @return
     */
    public MessageReceiver getMessageReceiver() {
        return receiver;
    }

    /**
     * Server close logic
     *
     * @return
     */
    public boolean isServerClosed() {
        if (server != null)
            return server.isClosed();
        else
            return true;
    }
}
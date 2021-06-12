package com.jpmc.server;

import com.jpmc.receiver.MessageReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Message worker class used to ge the message from server.
 *
 * @author Stanly
 */
public class MessageWorker extends Thread {

    private final Socket socket;
    private MessageReceiver receiver = null;

    public MessageWorker(Socket socket, MessageReceiver receiver) {
        this.socket = socket;
        this.receiver = receiver;
    }

    public void run() {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter a line with only a period to quit\n");

            while (true) {
                String input = in.readLine();
                if (input == null) {
                    break;
                }
                receiver.receive(input);
            }
        } catch (IOException e) {

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
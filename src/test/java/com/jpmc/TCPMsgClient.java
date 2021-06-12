package com.jpmc;

import com.jpmc.domain.ApplicationEnum;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


/**
 * To test the message from server.
 *
 * @author Stanly
 */
public class TCPMsgClient {

    private PrintWriter out;
    private Socket socket = null;

    public static void main(String[] args) throws Exception {
        TCPMsgClient client = new TCPMsgClient();
        client.connectToServer();
        client.readFileAndPush("src/test/resources/input50.txt");
    }

    public void connectToServer() throws IOException {

        socket = new Socket(ApplicationEnum.TCP_HOST.getEnumType(), Integer.parseInt(ApplicationEnum.TCP_PORT.getEnumType()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void close() throws IOException {
        out.close();
        socket.close();
    }

    public void readFileAndPush(String fileAbsolutePath) {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileAbsolutePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                out.println(line);
                out.flush();
            }
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}

package com.proyecto.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 2121;
    private static ServerSocket ss;

    public static void init() {
        try {
            System.out.println("Inicializando servidor...");
            ss = new ServerSocket(port);
            System.out.println("\t[OK]");
            int idSession = 0;
            while (true) {
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: " + socket);
                new ServerThread(socket, idSession).start();
                idSession++;
            }
        } catch (IOException ex) {

        }
    }
}

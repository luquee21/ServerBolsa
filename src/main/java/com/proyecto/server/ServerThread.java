package com.proyecto.server;

import com.proyecto.dao.CorredorDAO;
import com.proyecto.model.Corredor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;
    private final int idSession;
    private final CorredorDAO cdao = new CorredorDAO();
    private DataOutputStream dos;
    private DataInputStream dis;
    private Corredor aux;

    public ServerThread(Socket socket, int id) {
        this.socket = socket;
        this.idSession = id;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            disconnect();
        }
    }

    public void disconnect() {
        if (aux != null) {
            System.out.println("Corredor " + aux.getName() + " desconectado");
        } else {
            System.out.println("Desconectado");
        }
        try {
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        while (!checkLogin() && !socket.isClosed()) {
            System.out.println(checkLogin() + " login");
            System.out.println(socket.isConnected() + " socket");
        }
        operate();
        //querys con menu
    }

    public void operate() {
        boolean flag = false;
        String option = "";
        do {
            flag = false;
            try {
                option = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!option.isEmpty()) {
                switch (option) {
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    case "5":
                        break;
                    case "6":
                        break;
                    case "7":
                        try {
                            dos.writeUTF("ER JEJE 28");
                        } catch (IOException e) {
                            e.printStackTrace();
                            disconnect();
                        }
                        break;
                    case "8":
                        disconnect();
                        break;
                    default:
                        break;

                }
            }

        } while (!option.equals("8") && !socket.isClosed());
    }


    public boolean checkLogin() {
        String login = "";
        String pass = "";
        boolean flag = false;
        try {
            login = dis.readUTF();
            if (!login.isEmpty()) {
                dos.writeUTF("Login data OK");
            }
            pass = dis.readUTF();
            if (!pass.isEmpty()) {
                dos.writeUTF("Password data OK");
            }
            if (!login.isEmpty() && !pass.isEmpty()) {
                Corredor c = new Corredor(login, pass);
                aux = cdao.login(c);
                if (aux != null) {
                    dos.writeUTF("Login OK");
                    dos.writeUTF(aux.getName());
                    flag = true;
                } else {
                    dos.writeUTF("Login NOT OK");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            disconnect();

        }
        return flag;
    }
}

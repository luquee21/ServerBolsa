package com.proyecto.server;

import com.proyecto.dao.ClientDAO;
import com.proyecto.dao.CorredorDAO;
import com.proyecto.dao.EnterpriseDAO;
import com.proyecto.model.Client;
import com.proyecto.model.Corredor;
import com.proyecto.model.Enterprise;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {
    private final Socket socket;
    private final int idSession;
    private final CorredorDAO cdao = new CorredorDAO();
    private final EnterpriseDAO edao = new EnterpriseDAO();
    private final ClientDAO ctdao = new ClientDAO();
    private DataOutputStream dos;
    private DataInputStream dis;
    private Corredor aux;
    private Enterprise enterpriseAux;
    private List<Enterprise> enterprisesList;
    private Client clientAux;
    private List<Client> clientsList;

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
                        enterprisesList = edao.selectAllEnterprise();
                        try {
                            if (!enterprisesList.isEmpty()) {
                                dos.writeUTF(enterprisesList.toString());
                            } else {
                                dos.writeUTF("No se encuentran empresas...");
                            }
                        } catch (IOException e) {
                            disconnect();
                        }
                        break;
                    case "2":
                        clientsList = ctdao.selectAllClient();
                        try {
                            if (!clientsList.isEmpty()) {
                                dos.writeUTF(clientsList.toString());
                            } else {
                                dos.writeUTF("No se encuentran clientes...");
                            }
                        } catch (IOException e) {
                            disconnect();
                        }
                        break;
                    case "3":
                        break;
                    case "4":
                        try {
                            dos.writeUTF("Introduzca id: ");
                            String response = dis.readUTF();
                            int id = Integer.parseInt(response);
                            clientAux = ctdao.selectActionsOfClient(id);
                            if (clientAux != null) {
                                dos.writeUTF(clientAux.toString());
                            } else {
                                dos.writeUTF("No se encuentra el cliente o las acciones...");
                            }
                        } catch (IOException e) {
                            disconnect();
                        }
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

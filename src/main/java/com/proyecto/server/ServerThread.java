package com.proyecto.server;

import com.proyecto.connection.ConnectionUtils;
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
import java.sql.SQLException;
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
            ConnectionUtils.closeConnection();
        } catch (IOException | SQLException ex) {
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
                disconnect();
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
                        boolean ok = false;
                        try {
                            clientsList = ctdao.selectAllClient();
                            dos.writeUTF(clientsList.toString());
                            if (dis.readUTF().equals("OK")) {
                                enterprisesList = edao.selectAllEnterprise();
                                dos.writeUTF(enterprisesList.toString());
                                if (dis.readUTF().equals("OK")) {
                                    dos.writeUTF("Introduzca id del cliente que va a realizar la compra: ");
                                    String response = dis.readUTF();
                                    int id_client = Integer.parseInt(response);

                                    dos.writeUTF("Introduzca id de la empresa a comprar acciones: ");
                                    String response_enterprise = dis.readUTF();
                                    int id_enterprise = Integer.parseInt(response_enterprise);

                                    dos.writeUTF("Introduzca el número de acciones a comprar: ");
                                    String response_actions = dis.readUTF();
                                    int actions = Integer.parseInt(response_actions);

                                    enterpriseAux = edao.getEnterprise(id_enterprise);
                                    clientAux = ctdao.getClient(id_client);
                                    if (clientAux != null && enterpriseAux != null) {
                                        if (enterpriseAux.getN_actions() >= actions) {
                                            dos.writeUTF("AVAILABLE");
                                            ok = edao.purchaseActions(id_enterprise, id_client, actions);
                                        } else {
                                            dos.writeUTF("NO AVAILABLE");
                                            if (dis.readUTF().equals("OK")) {
                                                dos.writeUTF("El numero de acciones que deseas comprar es mayor a la acciones disponibles, quedan " + enterpriseAux.getN_actions() + " ¿Cuántas desea comprar? ");
                                                int n_action = Integer.parseInt(dis.readUTF());
                                                if (enterpriseAux.getN_actions() >= n_action) {
                                                    ok = edao.purchaseActions(id_enterprise, id_client, n_action);
                                                }
                                            }
                                        }
                                    } else {
                                        dos.writeUTF("Comprueba que el cliente y la empresa existen");
                                    }

                                    if (ok) {
                                        dos.writeUTF("Compra realizada con éxito");
                                    } else {
                                        dos.writeUTF("No se ha podido realizar la compra");
                                    }
                                    break;
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            disconnect();
                        }
                        break;
                    case "4":
                        try {
                            dos.writeUTF("Introduzca id del usuario: ");
                            String response = dis.readUTF();
                            int id = Integer.parseInt(response);
                            clientsList = ctdao.selectActionsOfClient(id);
                            if (clientsList != null && !clientsList.isEmpty()) {
                                dos.writeUTF(clientsList.toString());
                            } else {
                                dos.writeUTF("No se encuentra el cliente o las acciones...");
                            }
                        } catch (IOException e) {
                            disconnect();
                        }
                        break;
                    case "5":
                        try {
                            dos.writeUTF("Introduzca id de la empresa: ");
                            String response = dis.readUTF();
                            int id = Integer.parseInt(response);
                            clientsList = ctdao.selectClientsByActions(id);
                            if (clientsList != null && !clientsList.isEmpty()) {
                                dos.writeUTF(clientsList.toString());
                            } else {
                                dos.writeUTF("No se encuentran los clientes o las acciones...");
                            }
                        } catch (IOException e) {
                            disconnect();
                        }
                        break;
                    case "6":
                        try {
                            enterprisesList = edao.selectAllEnterprise();
                            dos.writeUTF(enterprisesList.toString());
                            if (dis.readUTF().equals("OK")) {
                                dos.writeUTF("Introduzca id de la empresa a eliminar: ");
                                String response = dis.readUTF();
                                int id = Integer.parseInt(response);
                                boolean aux = edao.deleteEnterprise(id);
                                if (aux) {
                                    dos.writeUTF("Empresa borrada con éxito");
                                } else {
                                    dos.writeUTF("No se ha podido borrar la empresa");
                                }
                            }

                        } catch (IOException e) {
                            disconnect();
                        }
                        break;
                    case "7":
                        try {
                            clientsList = ctdao.selectAllClient();
                            dos.writeUTF(clientsList.toString());
                            if (dis.readUTF().equals("OK")) {
                                dos.writeUTF("Introduzca id del cliente a eliminar: ");
                                String response = dis.readUTF();
                                int id = Integer.parseInt(response);
                                boolean aux = ctdao.deleteClient(id);
                                if (aux) {
                                    dos.writeUTF("Cliente borrado con éxito");
                                } else {
                                    dos.writeUTF("No se ha podido borrar el cliente");
                                }
                            }

                        } catch (IOException e) {
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

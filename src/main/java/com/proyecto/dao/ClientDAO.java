package com.proyecto.dao;

import com.proyecto.connection.ConnectionUtils;
import com.proyecto.model.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private final EnterpriseDAO edao = new EnterpriseDAO();
    private final String SELECTALLCLIENTS = "SELECT * FROM cliente";
    private final String GETCLIENT = "SELECT * FROM cliente WHERE cliente.codigo=?";
    private final String SELECTACTIONS = "select * from cliente INNER JOIN cliente_empresa ON" +
            " cliente_empresa.codigo_cliente = cliente.codigo INNER JOIN empresa ON" +
            " cliente_empresa.codigo_empresa = empresa.codigo WHERE cliente.codigo=?";
    private final String SELECTCLIENTSBYACTIONS = "select * from cliente INNER JOIN cliente_empresa ON" +
            " cliente_empresa.codigo_cliente = cliente.codigo INNER JOIN empresa ON" +
            " cliente_empresa.codigo_empresa = empresa.codigo WHERE empresa.codigo=?";
    private final String DELETECLIENT = "DELETE FROM cliente WHERE codigo=?";

    public List<Client> selectAllClient() {
        List<Client> aux = new ArrayList<>();
        Client cli;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECTALLCLIENTS);
            ResultSet s = ps.executeQuery();

            while (s != null && s.next()) {
                cli = new Client(s.getInt("codigo"), s.getString("nombre"), s.getString("dni"), s.getString("apellidos"), s.getString("email"), s.getString("telefono"), s.getTimestamp("fecha_nac"));
                aux.add(cli);
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return aux;
    }

    public Client getClient(int id) {
        Client cli = null;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(GETCLIENT);
            ps.setInt(1, id);
            ResultSet s = ps.executeQuery();
            while (s != null && s.next()) {
                cli = new Client(s.getInt("codigo"), s.getString("nombre"), s.getString("dni"), s.getString("apellidos"), s.getString("email"), s.getString("telefono"), s.getTimestamp("fecha_nac"));
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return cli;
    }


    public List<Client> selectActionsOfClient(int id) {
        List<Client> clients = new ArrayList<>();
        Client client = null;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECTACTIONS);
            ps.setInt(1, id);
            ResultSet s = ps.executeQuery();

            while (s != null && s.next()) {
                client = new Client(s.getInt("cliente.codigo"), s.getString("cliente.nombre"),
                        s.getString("dni"), s.getString("apellidos"),
                        s.getString("email"), s.getString("telefono"),
                        s.getTimestamp("fecha_nac"), s.getInt("numero_acciones"),
                        s.getString("empresa.nombre"), s.getString("fecha_hora_compra"), s.getInt("empresa.codigo"));
                clients.add(client);
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return clients;
    }

    public List<Client> selectClientsByActions(int id) {
        List<Client> clients = new ArrayList<>();
        Client client = null;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECTCLIENTSBYACTIONS);
            ps.setInt(1, id);
            ResultSet s = ps.executeQuery();

            while (s != null && s.next()) {
                client = new Client(s.getInt("cliente.codigo"), s.getString("cliente.nombre"),
                        s.getString("dni"), s.getString("apellidos"),
                        s.getString("email"), s.getString("telefono"),
                        s.getTimestamp("fecha_nac"), s.getInt("numero_acciones"),
                        s.getString("empresa.nombre"), s.getString("fecha_hora_compra"), s.getInt("empresa.codigo"));
                clients.add(client);
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return clients;
    }

    public synchronized boolean deleteClient(int id) {
        boolean flag = false;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            List<Client> clients = selectActionsOfClient(id);
            if (clients != null) {
                for (Client c : clients) {
                    int n_actions = c.getN_actions();
                    int enterprise = c.getCode_enterprise();
                    edao.updateActions(enterprise, n_actions);
                }
            }
            PreparedStatement ps = conn.prepareStatement(DELETECLIENT);
            ps.setInt(1, id);
            int s = ps.executeUpdate();
            if (s > 0) {
                flag = true;
                ps.close();
            }

        } catch (SQLException ex) {

        }
        return flag;
    }

}

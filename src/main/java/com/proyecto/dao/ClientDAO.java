package com.proyecto.dao;

import com.proyecto.connection.ConnectionUtils;
import com.proyecto.model.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    final String SELECTALLCLIENTS = "SELECT * FROM cliente";
    final String SELECTACTIONS = "select * from cliente INNER JOIN cliente_empresa ON" +
            " cliente_empresa.codigo_cliente = cliente.codigo INNER JOIN empresa ON" +
            " cliente_empresa.codigo_empresa = empresa.codigo WHERE cliente.codigo=?";


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

    public Client selectActionsOfClient(int id) {
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
                        s.getString("empresa.nombre"), s.getString("fecha_hora_compra"));
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return client;
    }

}

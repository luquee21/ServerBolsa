package com.proyecto.dao;

import com.proyecto.connection.ConnectionUtils;
import com.proyecto.model.Enterprise;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnterpriseDAO {
    private final String GETENTERPRISE = "SELECT * FROM empresa WHERE empresa.codigo=?";
    private final String SELECTALLENTRERPRISE = "SELECT * FROM empresa";
    private final String DELETEENTERPRISE = "DELETE FROM empresa WHERE codigo=?";
    private final String UPDATEACTIONS = "UPDATE empresa SET acciones_disponibles=(acciones_disponibles+?) WHERE empresa.codigo=?";
    private final String PURCHASEACTIONS = "INSERT INTO cliente_empresa (codigo_cliente,codigo_empresa,numero_acciones) VALUES (?,?,?)";
    private final String CHECKACTIONS = "SELECT acciones_disponibles FROM empresa WHERE empresa.codigo=?";
    private final String SUBTRACTACTIONS = "UPDATE empresa SET acciones_disponibles=? WHERE empresa.codigo=?";
    private int actions;

    public int checkActions(int id) {
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(CHECKACTIONS);
            ps.setInt(1, id);
            ResultSet s = ps.executeQuery();
            while (s != null && s.next()) {
                actions = s.getInt("acciones_disponibles");
            }
            if (s != null) {
                s.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return actions;
    }

    public synchronized boolean purchaseActions(int id_enterprise, int id_client, int actions) {
        boolean flag = false;
        if (checkActions(id_enterprise) >= actions) {
            if (deleteActions(id_enterprise, actions)) {
                try {
                    java.sql.Connection conn = ConnectionUtils.getConnection();
                    PreparedStatement ps = conn.prepareStatement(PURCHASEACTIONS);
                    ps.setInt(1, id_client);
                    ps.setInt(2, id_enterprise);
                    ps.setInt(3, actions);
                    int s = ps.executeUpdate();
                    if (s > 0) {
                        flag = true;
                        ps.close();
                    }

                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        }
        return flag;
    }

    public List<Enterprise> selectAllEnterprise() {
        List<Enterprise> aux = new ArrayList<>();
        Enterprise enterprise;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECTALLENTRERPRISE);
            ResultSet s = ps.executeQuery();
            while (s != null && s.next()) {
                enterprise = new Enterprise(s.getInt("codigo"), s.getString("nombre"), s.getInt("acciones_disponibles"));
                aux.add(enterprise);
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return aux;
    }

    public Enterprise getEnterprise(int id) {
        Enterprise enterprise = null;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(GETENTERPRISE);
            ps.setInt(1, id);
            ResultSet s = ps.executeQuery();
            while (s != null && s.next()) {
                enterprise = new Enterprise(s.getInt("codigo"), s.getString("nombre"), s.getInt("acciones_disponibles"));
            }
            if (s != null) {
                s.close();
            }

        } catch (SQLException ex) {

        }
        return enterprise;
    }

    public boolean updateActions(int id, int actions) {
        boolean flag = false;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATEACTIONS);
            ps.setInt(1, actions);
            ps.setInt(2, id);
            int s = ps.executeUpdate();
            if (s > 0) {
                flag = true;
                ps.close();
            }

        } catch (SQLException ex) {

        }
        return flag;
    }

    public synchronized boolean deleteActions(int id, int n_actions) {
        boolean flag = false;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(SUBTRACTACTIONS);
            ps.setInt(2, id);
            int result = actions - n_actions;
            if (result < 0) {
                return false;
            }
            ps.setInt(1, result);
            int s = ps.executeUpdate();
            if (s > 0) {
                flag = true;
                ps.close();
            }

        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return flag;
    }

    public synchronized boolean deleteEnterprise(int id) {
        boolean flag = false;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETEENTERPRISE);
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

package com.proyecto.dao;

import com.proyecto.connection.ConnectionUtils;
import com.proyecto.model.Enterprise;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnterpriseDAO {

    private final String SELECTALLENTRERPRISE = "SELECT * FROM empresa";

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
}

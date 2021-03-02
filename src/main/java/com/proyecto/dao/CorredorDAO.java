package com.proyecto.dao;

import com.proyecto.connection.ConnectionUtils;
import com.proyecto.model.Corredor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CorredorDAO {
    private final String LOGIN = "SELECT * FROM corredor WHERE login=? AND password=?";

    public Corredor login(Corredor corredor) {
        boolean flag = false;
        Corredor aux = null;
        try {
            java.sql.Connection conn = ConnectionUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(LOGIN);
            ps.setString(1, corredor.getLogin());
            ps.setString(2, corredor.getPassword());
            ResultSet rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                aux = new Corredor(rs.getInt("codigo"), rs.getString("nombre"), rs.getString("login"), rs.getString("password"));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return aux;
    }


}

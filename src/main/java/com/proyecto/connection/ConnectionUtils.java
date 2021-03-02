package com.proyecto.connection;


import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {

    private static java.sql.Connection _conn = null;

    /**
     * Conecta con la base datos
     *
     * @param c recibe la conexion
     * @return devuelve la conexion
     * @throws ClassNotFoundException
     * @throws SQLException
     */

    public static java.sql.Connection connect(Connection c) throws ClassNotFoundException, SQLException {
        java.sql.Connection conn = null;

        if (c == null) {
            return null;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://" + c.getHost() + "/" + c.getDb()
                + "?useLegacyDatetimeCode=false&serverTimezone=UTC", c.getUser(), c.getPassword());

        return conn;
    }

    /**
     * Crea una nueva conexion
     *
     * @return devuelve una conexion
     */
    public static java.sql.Connection getConnection() {
        if (_conn == null) {
            Connection c = new Connection();
            c = new Connection("localhost", "Bolsa", "root", "");
            try {
                _conn = connect(c);
            } catch (ClassNotFoundException ex) {
            } catch (SQLException ex) {
            }
        }
        return _conn;
    }

    /**
     * Cierra la conexion
     *
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        if (_conn != null) {
            _conn.close();
        }

    }

}
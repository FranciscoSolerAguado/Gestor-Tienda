package org.fran.gestortienda.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Connection {
    private static Connection conn;

    private static final String URL = "jdbc:h2:./data/tienda;MODE=MySQL;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    private H2Connection() {
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión a H2 establecida.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}

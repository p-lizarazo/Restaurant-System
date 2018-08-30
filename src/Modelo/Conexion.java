/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;

/**
 *
 * @author Santi
 */
public class Conexion {
    
    public static Connection getConnection() throws SQLException {
        String username = "db111514";
        String password = "V7VJLLTY8L";
        String thinConn = "jdbc:oracle:thin:@orion.javeriana.edu.co:1521:PUJDISOR";
        DriverManager.registerDriver(new OracleDriver());
        Connection conn = DriverManager.getConnection(thinConn,
        username, password);
        conn.setAutoCommit(true);
        return conn;
    }
    
}

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnector extends Database {

    static {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PostgreSQLConnector(String user, String password, String host, int port, String dbName) {
        super(user, password, host, port, dbName);
    }

    @Override
    public Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://" + host + ":" + port + "/" + dbName,
                    user,
                    password);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }
}

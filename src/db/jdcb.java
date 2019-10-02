package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class jdcb {
Connection conn;

    public jdcb(String username, String password, String dbName) {
        Database db = new MySQLConnector(
                username,
                password,
                "192.168.65.15",
                3306,
                dbName);

        conn = db.getConnection();

    }

    public ResultSet executeQuery(String sql) {

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(String sql){
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

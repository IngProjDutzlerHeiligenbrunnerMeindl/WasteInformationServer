package db;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class jdcb {
    String username;
    String password;
    String dbName;

    public jdcb(String username, String password, String dbName) {
        this.username = username;
        this.password=password;
        this.dbName = dbName;
    }

    public ResultSet executeQuery(String sql) {
        Database db = new MySQLConnector(
                username,
                password,
                "192.168.65.15",
                3306,
                dbName);

        Connection c = db.getConnection();
        try {
            PreparedStatement stmt =
                    c.prepareStatement(sql);

            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

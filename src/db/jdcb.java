package db;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class jdcb {
    public void executeQuery(String sql) {
        Database db = new MySQLConnector(
                "users",
                "kOpaIJUjkgb9ur6S",
                "127.0.0.1",
                3306,
                "wasteinformation");

        Connection c = db.getConnection();
        try {
            PreparedStatement stmt =
                    c.prepareStatement(sql);

            ResultSet r = stmt.executeQuery();

            DefaultTableModel model = Database.logToTable(r);

//            this.resultTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

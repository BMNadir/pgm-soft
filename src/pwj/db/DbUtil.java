package pwj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbUtil {
    
    public static Connection conn;
    public static Statement stat;
    
    public static void dbConnection ()
    {
        try {
            Class.forName("org.h2.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:h2:./db/pgmDB", "root", "pgmDbPassword");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            Logger.getLogger(DbUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    // Executes SELECT statements 
    public static ResultSet execQuery(String qu)
     {
        try {
            stat = conn.createStatement();
            return stat.executeQuery(qu);
        } catch (SQLException ex) {
            Logger.getLogger(DbUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
     }
}

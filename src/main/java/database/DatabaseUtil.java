package database;

import java.sql.*;

public class DatabaseUtil {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "postgres");
    }
}

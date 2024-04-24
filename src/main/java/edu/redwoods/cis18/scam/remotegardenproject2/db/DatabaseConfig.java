package edu.redwoods.cis18.scam.remotegardenproject2.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Connection connection;

    private List<Observer> observers = new ArrayList<>();

    private DatabaseConfig() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection to the database
            this.connection = DriverManager.getConnection(
                    //Change to your own port, username, and password
                    "jdbc:mysql://localhost:3306/yourDatabase", "root", "root");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }


    public void modifyDatabase(String updateQuery) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(updateQuery);
            System.out.println("Database updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void queryDatabase(String query) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Data from database: " + rs.getString("columnName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
}

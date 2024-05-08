package edu.redwoods.cis18.scam.remotegardenproject2.db;

import java.sql.*;

public class DatabaseManager {

	public String dbUrl;
	public String user;
	public String password;
	private ResultSet resultSet;

	public DatabaseManager(String url, String user, String password) {
		this.dbUrl = url;
		this.user = user;
		this.password = password;
	}

	public static void insert(String formattedDateTime, String sensorType, int sensorValue, double percentage) {
		//skeleton code
	}

	public void performQuery(String query) {
		try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				// Process the result set.
				System.out.println(rs.getString("columnName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ResultSet getResultSet() {

		return resultSet;
	}

	private void  insertDataIntoDB(String timestamp, String sensorType, int moistureLevel, double percentage) {
		String sql = "INSERT INTO SoilMoisture (timestamp, sensor_type, moisture_level, percentage) VALUES (?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
		     PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, timestamp);
			pstmt.setString(2, sensorType);
			pstmt.setInt(3, moistureLevel);
			pstmt.setDouble(4, percentage);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error inserting data: " + e.getMessage());
		}
	}


	//needs more methods for data processing and database interfacing and information collation and other such thingies
}



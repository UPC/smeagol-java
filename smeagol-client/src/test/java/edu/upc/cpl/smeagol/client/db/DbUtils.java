package edu.upc.cpl.smeagol.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbUtils {

	private Connection connection;

	public DbUtils() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + "/home/angel/smeagol/var/smeagol.db" /*
																												 * System
																												 * .
																												 * getenv
																												 * (
																												 * "SMEAGOL_DB_PATH"
																												 * )
																											 */);
			this.connection.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetDataBase() {
		try {
			Statement s = connection.createStatement();

			s.executeUpdate("delete from t_tag");
			s.executeUpdate("delete from t_resource");
			s.executeUpdate("delete from t_event");
			s.executeUpdate("delete from t_booking");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rollback() {
		try {
			connection.rollback();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

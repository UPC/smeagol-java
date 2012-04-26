package edu.upc.cpl.smeagol.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.junit.rules.ExternalResource;

public class DbUtils extends ExternalResource {

	private static final Logger logger = Logger.getLogger(DbUtils.class);

	/**
	 * The name of the environment variable containing the Sméagol server to use
	 * for running tests: {@value}
	 */
	public static final String ENV_SMEAGOL_URL_NAME = "SMEAGOL_URL";

	/**
	 * The name of the environment variable containing the path to the database
	 * file used by the Sméagol server to use for running tests: {@value}
	 */
	public static final String ENV_SMEAGOL_DB_PATH_NAME = "SMEAGOL_DB_PATH";

	private Connection connection;

	public DbUtils() {
	}

	private void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the absolute path to the database file used by the server.
	 * 
	 * @return
	 */
	public String getDatabasePath() {
		// return "/home/angel/smeagol/var/smeagol.db";
		return System.getenv(ENV_SMEAGOL_DB_PATH_NAME);
	}

	/**
	 * Get the server url where the Smeagol server is listening.
	 * 
	 * @return
	 */
	public String getServerUrl() {
		// return "http://localhost:3000";
		return System.getenv(ENV_SMEAGOL_URL_NAME);
	}

	/**
	 * Method invoked automatically by jUnit, before each Test
	 */
	@Override
	protected void before() throws Throwable {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
			this.connection.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initDB();
	}

	/**
	 * Method invoked automatically by jUnit, after each Test
	 */
	@Override
	protected void after() {
		disconnect();
	}

	/**
	 * Set the Sméagol database contents to a known initial state, ready for
	 * tests.
	 */
	private void initDB() {
		try {
			Statement s = connection.createStatement();

			s.executeUpdate("delete from t_tag");
			s.executeUpdate("delete from t_resource");
			s.executeUpdate("delete from t_event");
			s.executeUpdate("delete from t_booking");

		} catch (Exception e) {
			logger.error("Error refreshing database", e);
		}
	}

}

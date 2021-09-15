package dev.db;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static dev.constants.Constants.DEFAULT_COMMIT_NUMBER;

/**
 * Carries out work with the database
 */
public class DBWorker {
    /** Database connection */
    private Connection connection;

    /** Number of operations performed */
    private BigInteger countOperations = BigInteger.ZERO;

    /**
     * Constructor - initializes the connection to the database
     */
    public DBWorker(String login, String password, String url) {
        try {
            this.connection = DriverManager.getConnection(url, login, password);
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the connection
     *
     * @return - connection {@link Connection}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Commit when a certain number of operations are reached {@link dev.constants.Constants#DEFAULT_COMMIT_NUMBER}
     *
     * @throws SQLException
     */
    public void customCommit() throws SQLException {
        countOperations = countOperations.add(BigInteger.ONE);
        if (countOperations.mod(DEFAULT_COMMIT_NUMBER).equals(BigInteger.ZERO)) {
            System.out.println(countOperations.divide(DEFAULT_COMMIT_NUMBER)
                    + " DB commit: " + DEFAULT_COMMIT_NUMBER + " rows");
            connection.commit();
        }
    }


}
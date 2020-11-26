package com.example.pole_vault_tracker.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Remote {
    private final Connection connection;
    private static Remote instance;

    public static synchronized Remote getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) instance = new Remote("","","");
        return instance;
    }

    private Remote(String connectionURL, String username, String password ) throws SQLException {
        connection = DriverManager.getConnection(connectionURL,username,password);
    }
}

package com.authguard.dal.jdbc;

import com.authguard.config.ConfigContext;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class ConnectionProvider {
    private final Connection connection;

    @Inject
    public ConnectionProvider(@Named("jdbc") final ConfigContext config) {
        this(config.asConfigBean(ImmutableJdbcConfig.class));
    }

    public ConnectionProvider(final ImmutableJdbcConfig config) {
        try {
            connection = DriverManager.getConnection(config.getConnectionString(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Connection getConnection() {
        return connection;
    }
}

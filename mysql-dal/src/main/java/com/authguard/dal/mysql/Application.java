package com.authguard.dal.mysql;

import com.authguard.dal.jdbc.ConnectionProvider;
import com.authguard.dal.jdbc.TablesBootstrap;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;

public class Application {

    public static void main(String[] args) {
        // create the connection
        final ImmutableJdbcConfig jdbcConfig = ImmutableJdbcConfig.builder()
                .connectionString("jdbc:mysql://127.0.0.1:3306/test")
                .username("root")
                .password("my-secret-pw")
                .build();

        final ConnectionProvider connectionProvider = new ConnectionProvider(jdbcConfig);

        // create the tables
        final TablesBootstrap tablesBootstrap = new TablesBootstrap(jdbcConfig);

        tablesBootstrap.run();
    }
}

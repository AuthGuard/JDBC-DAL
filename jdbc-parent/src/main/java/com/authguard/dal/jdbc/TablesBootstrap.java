package com.authguard.dal.jdbc;

import com.authguard.bootstrap.BootstrapStep;
import com.authguard.config.ConfigContext;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class TablesBootstrap implements BootstrapStep {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JdbcQueryRunner queryRunner;
    private final ConnectionProvider connectionProvider;

    @Inject
    public TablesBootstrap(@Named("jdbc") final ConfigContext jdbcConfig) {
        this(jdbcConfig.asConfigBean(ImmutableJdbcConfig.class));
    }

    public TablesBootstrap(final ImmutableJdbcConfig jdbcConfig) {
        this.connectionProvider = new ConnectionProvider(jdbcConfig);
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());
    }

    @Override
    public void run() {
        final Properties properties = loadTablesProperties();
        final Map<String, String> tables = parseMap(properties, "tables");

        try {
            for (final Map.Entry<String, String> table : tables.entrySet()) {
                log.info("Creating table {}", table.getKey());
                queryRunner.execute(table.getValue());
                log.info("Table {} was successfully created", table.getKey());
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connectionProvider.getConnection().close();
            } catch (SQLException e) {
                log.error("Failed to close connection", e);
            }
        }
    }

    public void bootstrapTable(final String table) {
        final Properties properties = loadTablesProperties();
        final Map<String, String> tables = parseMap(properties, "tables");

        Optional.ofNullable(tables.get(table))
                .ifPresentOrElse(tableQuery -> {
                    log.info("Creating table {}", table);

                    try {
                        queryRunner.execute(tableQuery);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    log.info("Table {} was successfully created", table);
                }, () -> log.info("No table bootstrap statement for table {}", table));
    }

    private Properties loadTablesProperties() {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader().getResourceAsStream("tables.properties");

            Properties properties = new Properties();

            properties.load(inputStream);

            return properties;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parseMap(final Properties properties, final String property) {
        final String regex = String.format("%s\\[.*\\]", property);

        return properties.entrySet().stream()
                .filter(entry -> ((String) entry.getKey()).matches(regex))
                .collect(Collectors.toMap(
                        entry -> tableName((String) entry.getKey()),
                        entry -> (String) entry.getValue()
                ));
    }

    private String tableName(final String propertyKey) {
        final int startBracket = propertyKey.indexOf('[');
        final int endBracket = propertyKey.indexOf(']');

        return propertyKey.substring(startBracket + 1, endBracket);
    }
}

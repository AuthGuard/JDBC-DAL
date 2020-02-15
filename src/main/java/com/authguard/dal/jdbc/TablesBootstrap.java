package com.authguard.dal.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class TablesBootstrap {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JdbcQueryRunner queryRunner;

    public TablesBootstrap(final ConnectionProvider connectionProvider) {
        queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());
    }

    public void bootstrap() throws SQLException {
        final Properties properties = loadTablesProperties();
        final Map<String, String> tables = parseMap(properties, "tables");

        for (final Map.Entry<String, String> table : tables.entrySet()) {
            log.info("Creating table {}", table.getKey());
            queryRunner.execute(table.getValue());
            log.info("Table {} was successfully created", table.getKey());
        }
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

package com.authguard.dal.jdbc;

import com.authguard.dal.jdbc.util.ResultSetHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

class JdbcQueryRunner {
    private final Connection connection;
    private final ObjectMapper objectMapper;

    JdbcQueryRunner(final Connection connection) {
        this.connection = connection;
        this.objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    <T> List<T> execute(final PreparedStatement preparedStatement,
                        final Class<T> targetClass) throws SQLException {
        final ResultSetHandler resultSetHandler = new ResultSetHandler();
        final ResultSet resultSet = preparedStatement.executeQuery();

        return resultSetHandler.handle(resultSet)
                .stream()
                .map(map -> objectMapper.convertValue(map, targetClass))
                .collect(Collectors.toList());
    }

    void execute(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.execute();
    }

    void execute(final String query) throws SQLException {
        connection.prepareStatement(query).execute();
    }
}

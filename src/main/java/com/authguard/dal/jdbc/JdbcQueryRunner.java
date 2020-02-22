package com.authguard.dal.jdbc;

import com.authguard.dal.jdbc.util.FieldMapper;
import com.authguard.dal.jdbc.util.ResultSetHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    <T> List<T> execute(final PreparedStatement preparedStatement,
                        final Class<T> targetClass, final FieldMapper... specialMappers) throws SQLException {
        final Map<String, FieldMapper> fieldMappers = Stream.of(specialMappers)
                .collect(Collectors.toMap(FieldMapper::getFieldName, Function.identity()));

        final ResultSetHandler resultSetHandler = new ResultSetHandler();
        final ResultSet resultSet = preparedStatement.executeQuery();

        return resultSetHandler.handle(resultSet)
                .stream()
                .map(map -> normalizeMap(map, fieldMappers))
                .map(map -> objectMapper.convertValue(map, targetClass))
                .collect(Collectors.toList());
    }

    void execute(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.execute();
    }

    void execute(final String query) throws SQLException {
        connection.prepareStatement(query).execute();
    }

    private Map<String, Object> normalizeMap(final Map<String, Object> map, final Map<String, FieldMapper> fieldMappers) {
        final List<String> specialFields = map.keySet().stream()
                .map(this::getTopFieldName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(fieldMappers::containsKey)
                .collect(Collectors.toList());

        specialFields.forEach(fieldName -> map.putAll(fieldMappers.get(fieldName).normalize(map)));

        return map;
    }

    private Optional<String> getTopFieldName(final String name) {
        return Optional.ofNullable(name)
                .map(str -> str.split("\\."))
                .filter(arr -> arr.length > 1)
                .map(arr -> arr[0]);
    }
}

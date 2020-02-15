package com.authguard.dal.jdbc.util;

import com.authguard.dal.model.AbstractDO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateStatement {
    private final String template;
    private final Map<String, List<Integer>> variableIndices;
    private PreparedStatement preparedStatement;

    public TemplateStatement(final String template, final Map<String, List<Integer>> variableIndices) {
        this.template = template;
        this.variableIndices = variableIndices;
    }

    public TemplateStatement prepare(final Connection connection) throws SQLException {
        this.preparedStatement = connection.prepareStatement(template);
        return this;
    }

    public <T> PreparedStatement build(final T object) throws SQLException {
        final Map<String, Object> subs = substitutionMap(object);
        return build(subs);
    }

    public PreparedStatement build(Map<String, Object> subs) throws SQLException {
        this.preparedStatement.clearParameters();

        for (final Map.Entry<String, List<Integer>> entry : variableIndices.entrySet()) {
            final String variable = entry.getKey();
            final List<Integer> indices = entry.getValue();

            for (final Integer index : indices) {
                this.preparedStatement.setObject(index + 1, subs.get(variable));
            }
        }

        return this.preparedStatement;
    }

    private <T> Map<String, Object> substitutionMap(final T object) {
        return Stream.of(object.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !field.getType().isAssignableFrom(AbstractDO.class)) // every DO is has its own repository
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, field -> getValue(field, object)));
    }

    private <T> Object getValue(final Field field, final T object) {
        try {
            if (field.getType().isAssignableFrom(List.class)) {
                return field.get(object).toString();
            } else {
                return field.get(object);
            }
        } catch (final IllegalAccessException e) {
            return new RuntimeException(e);
        }
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, List<Integer>> getVariableIndices() {
        return variableIndices;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }
}

package com.authguard.dal.jdbc.util;

import java.util.List;

public class TemplateBuilder {
    public enum Operation {
        SELECT,
        INSERT
    }

    private Operation operation;
    private String table;
    private List<String> columns;
    private List<String> values;
    private String whereStatement;
    private String joinedStatement;

    private TemplateBuilder(final Operation operation) {
        this.operation = operation;
    }

    public static TemplateBuilder select() {
        return new TemplateBuilder(Operation.SELECT);
    }

    public static TemplateBuilder insert() {
        return new TemplateBuilder(Operation.INSERT);
    }

    public TemplateBuilder table(final String table) {
        this.table = table;
        return this;
    }

    public TemplateBuilder columns(final List<String> columns) {
        this.columns = columns;
        return this;
    }

    public TemplateBuilder values(final List<String> values) {
        this.values = values;
        return this;
    }

    public TemplateBuilder whereStatement(final String whereStatement) {
        this.whereStatement = whereStatement;
        return this;
    }

    public TemplateBuilder joinedStatement(final String joinedStatement) {
        this.joinedStatement = joinedStatement;
        return this;
    }

    public String build() {
        switch (operation) {
            case SELECT: return buildSelect();
            case INSERT: return buildInsert();
            default: throw new UnsupportedOperationException();
        }
    }

    private String buildSelect() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT ");

        if (columns == null) {
            stringBuilder.append("* ");
        } else {
            stringBuilder.append(String.join(", ", columns)).append(' ');
        }

        stringBuilder.append("FROM ").append(table).append(" ");

        if (joinedStatement != null) {
            stringBuilder.append("LEFT JOIN ").append(joinedStatement).append(" ");
        }

        if (whereStatement != null) {
            stringBuilder.append("WHERE ").append(whereStatement).append(" ");
        }

        return stringBuilder.toString().trim();
    }

    private String buildInsert() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("INSERT INTO ").append(table).append(" ");

        if (columns != null) {
            stringBuilder.append("COLUMNS (")
                    .append(String.join(", ", columns))
                    .append(") ");
        }

        stringBuilder.append("VALUES ").append("(").
                append(String.join(", ", values))
                .append(") ");

        return stringBuilder.toString().trim();
    }
}

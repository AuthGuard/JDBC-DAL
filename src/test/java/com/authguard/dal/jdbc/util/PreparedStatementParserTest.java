package com.authguard.dal.jdbc.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreparedStatementParserTest {

    @Test
    void parse() {
        final String template = "SELECT * FROM ${table} WHERE id = ${id} OR id = ${id}";
        final PreparedStatementParser parser = new PreparedStatementParser();

        parser.parse(template);

        final TemplateStatement actual = parser.parse(template);
        final String expected = "SELECT * FROM ? WHERE id = ? OR id = ?";

        assertThat(actual.getTemplate()).isEqualTo(expected);
        assertThat(actual.getVariableIndices().get("table")).containsExactly(0);
        assertThat(actual.getVariableIndices().get("id")).containsExactly(1, 2);
    }
}
package com.authguard.dal.jdbc.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TemplateBuilderTest {

    @Test
    void select() {
        final String actual = TemplateBuilder.select()
                .table("accounts")
                .build();

        final String expected = "SELECT * FROM accounts";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void selectColumns() {
        final String actual = TemplateBuilder.select()
                .table("accounts")
                .columns(Arrays.asList("column-1", "column-2"))
                .build();

        final String expected = "SELECT column-1, column-2 FROM accounts";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void selectWhere() {
        final String actual = TemplateBuilder.select()
                .table("accounts")
                .whereStatement("column-1 = 5")
                .build();

        final String expected = "SELECT * FROM accounts WHERE column-1 = 5";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void selectJoin() {
        final String actual = TemplateBuilder.select()
                .table("accounts")
                .joinedStatement("column-1 = 5")
                .build();

        final String expected = "SELECT * FROM accounts JOIN column-1 = 5";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void insert() {
        final String actual = TemplateBuilder.insert()
                .table("accounts")
                .values(Arrays.asList("'id'", "'deleted'"))
                .build();

        final String expected = "INSERT INTO accounts VALUES ('id', 'deleted')";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void insertWithColumns() {
        final String actual = TemplateBuilder.insert()
                .table("accounts")
                .columns(Arrays.asList("id", "deleted"))
                .values(Arrays.asList("'id'", "'deleted'"))
                .build();

        final String expected = "INSERT INTO accounts COLUMNS (id, deleted) VALUES ('id', 'deleted')";

        assertThat(actual).isEqualTo(expected);
    }
}
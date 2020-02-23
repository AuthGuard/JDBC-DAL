package com.authguard.dal.jdbc.statements;

import com.authguard.dal.jdbc.util.TemplateBuilder;

import java.util.Arrays;

public class CredentialsStatements {
    private static final String TABLE = "credentials";

    public static final String INSERT_TEMPLATE = TemplateBuilder.insert()
            .table(TABLE)
            .values(Arrays.asList("${id}", "${deleted}", "${accountId}", "${username}", "${hashedPassword.password}", "${hashedPassword.salt}"))
            .build();

    public static final String GET_BY_USERNAME_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("username = ${username}")
            .build();

    public static final String GET_BY_ID_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("id = ${id}")
            .build();
}

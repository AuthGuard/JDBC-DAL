package com.authguard.dal.jdbc.statements;

import com.authguard.dal.jdbc.util.TemplateBuilder;

import java.util.Arrays;

public class RolesStatements {
    public final static String GET_STATEMENT = TemplateBuilder.select()
            .table("roles")
            .whereStatement("id = ${id}")
            .build();

    public final static String GET_BY_NAME_STATEMENT = TemplateBuilder.select()
            .table("roles")
            .whereStatement("name = ${name}")
            .build();

    public final static String INSERT_STATEMENT = TemplateBuilder.insert()
            .table("roles")
            .values(Arrays.asList("${id}", "${deleted}", "${name}"))
            .build();
}

package com.authguard.dal.jdbc.statements;

import com.authguard.dal.jdbc.util.TemplateBuilder;

import java.util.Arrays;

public class PermissionsStatements {
    public final static String GET_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("id = ${id}")
            .build();

    public final static String SEARCH_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("`group` = ${group} AND name = ${name}")
            .build();

    public final static String SEARCH_BY_GROUP_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("`group` = ${group}")
            .build();

    public final static String INSERT_PERMISSION_STATEMENT = TemplateBuilder.insert()
            .table("permissions")
            .values(Arrays.asList("${id}", "${deleted}", "${group}", "${name}"))
            .build();
}

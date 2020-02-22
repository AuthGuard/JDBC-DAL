package com.authguard.dal.jdbc.statements;

import com.authguard.dal.jdbc.util.TemplateBuilder;

import java.util.Arrays;

public class AccountsStatements {
    public final static String GET_STATEMENT = TemplateBuilder.select()
            .table("accounts")
            .whereStatement("id = ${id}")
            .joinedStatement("account_permissions ON account_permissions.accountId = accounts.id LEFT JOIN account_roles ON account_roles.accountId = accounts.id")
            .build();

    public final static String INSERT_STATEMENT = TemplateBuilder.insert()
            .table("accounts")
            .values(Arrays.asList("${id}", "${deleted}", "${active}", "${scopes}"))
            .build();

    public static final String INSERT_PERMISSION_STATEMENT = TemplateBuilder.insert()
            .table("account_permissions")
            .values(Arrays.asList("${accountId}", "${permissionId}"))
            .build();
}

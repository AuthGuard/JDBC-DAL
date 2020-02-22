package com.authguard.dal.jdbc.statements;

import com.authguard.dal.jdbc.util.TemplateBuilder;

import java.util.Arrays;

public class CredentialsAuditStatements {
    private static final String TABLE = "credentials_audit";

    public static final String INSERT_TEMPLATE = TemplateBuilder.insert()
            .table(TABLE)
            .values(Arrays.asList("${id}", "${deleted}", "${action}", "${credentialId}", "${username}", // TODO credentialId is a typo in the DO, change when it's fixed
                    "${password.password}", "${password.salt}"))
            .build();

    public static final String GET_BY_CREDENTIALS_ID_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("credentialId = ${credentialId}")
            .build();

    public static final String GET_BY_ID_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("id = ${id}")
            .build();
}

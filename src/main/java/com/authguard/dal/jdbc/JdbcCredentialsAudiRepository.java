package com.authguard.dal.jdbc;

import com.authguard.dal.CredentialsAuditRepository;
import com.authguard.dal.jdbc.util.FieldMappers;
import com.authguard.dal.jdbc.util.PreparedStatementParser;
import com.authguard.dal.jdbc.util.TemplateBuilder;
import com.authguard.dal.jdbc.util.TemplateStatement;
import com.authguard.dal.model.CredentialsAuditDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JdbcCredentialsAudiRepository implements CredentialsAuditRepository {
    private static final String TABLE = "credentials_audit";

    private static final String GET_BY_ID_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("id = ${id}")
            .build();

    private static final String GET_BY_CREDENTIALS_ID_TEMPLATE = TemplateBuilder.select()
            .table(TABLE)
            .whereStatement("credentialId = ${credentialId}")
            .build();

    private static final String INSERT_TEMPLATE = TemplateBuilder.insert()
            .table(TABLE)
            .values(Arrays.asList("${id}", "${deleted}", "${action}", "${credentialId}", "${username}", // TODO credentialId is a typo in the DO, change when it's fixed
                    "${password.password}", "${password.salt}"))
            .build();

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement getByCredentialsIdStatement;
    private final TemplateStatement saveStatement;

    @Inject
    public JdbcCredentialsAudiRepository(final ConnectionProvider connectionProvider) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());

        final PreparedStatementParser parser = new PreparedStatementParser();

        this.getByIdStatement = parser.parse(GET_BY_ID_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.getByCredentialsIdStatement = parser.parse(GET_BY_CREDENTIALS_ID_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser.parse(INSERT_TEMPLATE)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public CredentialsAuditDO save(final CredentialsAuditDO credentialsAuditDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(credentialsAuditDO,
                    FieldMappers.shallow(credentialsAuditDO, "password"));

            queryRunner.execute(preparedStatement);

            return credentialsAuditDO;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CredentialsAuditDO> getById(final String id) {
        try {
            final PreparedStatement preparedStatement = getByIdStatement.build(ImmutableMap.of("id", id));
            final List<CredentialsAuditDO> rows = queryRunner.execute(preparedStatement, CredentialsAuditDO.class,
                    FieldMappers.shallow(CredentialsAuditDO.class, "password"));

            return rows.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CredentialsAuditDO> findByCredentialsId(final String credentialsId) {
        try {
            final PreparedStatement preparedStatement = getByCredentialsIdStatement.build(ImmutableMap.of("credentialId", credentialsId));
            final List<CredentialsAuditDO> rows = queryRunner.execute(preparedStatement, CredentialsAuditDO.class,
                    FieldMappers.shallow(CredentialsAuditDO.class, "password"));

            return rows;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.authguard.dal.jdbc;

import com.authguard.dal.CredentialsAuditRepository;
import com.authguard.dal.jdbc.statements.CredentialsAuditStatements;
import com.authguard.dal.jdbc.util.*;
import com.authguard.dal.model.CredentialsAuditDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcCredentialsAudiRepository implements CredentialsAuditRepository {

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement getByCredentialsIdStatement;
    private final TemplateStatement saveStatement;

    private final FieldMapper passwordFieldMapper = FieldMappers.shallow(CredentialsAuditDO.class, "password");

    @Inject
    public JdbcCredentialsAudiRepository(final ConnectionProvider connectionProvider) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());

        final TemplateStatementParser parser = new TemplateStatementParser();

        this.getByIdStatement = parser.parse(CredentialsAuditStatements.GET_BY_ID_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.getByCredentialsIdStatement = parser.parse(CredentialsAuditStatements.GET_BY_CREDENTIALS_ID_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser.parse(CredentialsAuditStatements.INSERT_TEMPLATE)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public CredentialsAuditDO save(final CredentialsAuditDO credentialsAuditDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(credentialsAuditDO, passwordFieldMapper);

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
            final List<CredentialsAuditDO> rows = queryRunner.execute(preparedStatement, CredentialsAuditDO.class, passwordFieldMapper);

            return rows.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CredentialsAuditDO> findByCredentialsId(final String credentialsId) {
        try {
            final PreparedStatement preparedStatement = getByCredentialsIdStatement.build(ImmutableMap.of("credentialId", credentialsId));
            final List<CredentialsAuditDO> rows = queryRunner.execute(preparedStatement, CredentialsAuditDO.class, passwordFieldMapper);

            return rows;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

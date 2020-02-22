package com.authguard.dal.jdbc;

import com.authguard.dal.CredentialsRepository;
import com.authguard.dal.jdbc.statements.CredentialsStatements;
import com.authguard.dal.jdbc.util.*;
import com.authguard.dal.model.CredentialsDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcCredentialsRepository implements CredentialsRepository {

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement getByUsernameStatement;
    private final TemplateStatement saveStatement;

    private final FieldMapper hashedPasswordFieldMapper = FieldMappers.shallow(CredentialsDO.class, "hashedPassword");

    @Inject
    public JdbcCredentialsRepository(final ConnectionProvider connectionProvider) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());

        final TemplateStatementParser parser = new TemplateStatementParser();

        this.getByIdStatement = parser.parse(CredentialsStatements.GET_BY_ID_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.getByUsernameStatement = parser.parse(CredentialsStatements.GET_BY_USERNAME_TEMPLATE)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser.parse(CredentialsStatements.INSERT_TEMPLATE)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public CredentialsDO save(final CredentialsDO credentialsDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(credentialsDO, hashedPasswordFieldMapper);

            queryRunner.execute(preparedStatement);

            return credentialsDO;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CredentialsDO> getById(final String id) {
        try {
            final PreparedStatement preparedStatement = getByIdStatement.build(ImmutableMap.of("id", id));
            final List<CredentialsDO> rows = queryRunner.execute(preparedStatement, CredentialsDO.class, hashedPasswordFieldMapper);

            return rows.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CredentialsDO> findByUsername(final String username) {
        try {
            final PreparedStatement preparedStatement = getByUsernameStatement.build(ImmutableMap.of("username", username));
            final List<CredentialsDO> rows = queryRunner.execute(preparedStatement, CredentialsDO.class, hashedPasswordFieldMapper);

            return rows.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CredentialsDO> update(final CredentialsDO credentialsDO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<CredentialsDO> delete(final String s) {
        throw new UnsupportedOperationException();
    }
}

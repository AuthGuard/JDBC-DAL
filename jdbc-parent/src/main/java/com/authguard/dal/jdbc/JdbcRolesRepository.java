package com.authguard.dal.jdbc;

import com.authguard.dal.RolesRepository;
import com.authguard.dal.jdbc.statements.RolesStatements;
import com.authguard.dal.jdbc.util.TemplateStatementParser;
import com.authguard.dal.jdbc.util.TemplateStatement;
import com.authguard.dal.model.RoleDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JdbcRolesRepository implements RolesRepository {

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement getByNameStatement;
    private final TemplateStatement saveStatement;

    @Inject
    public JdbcRolesRepository(final ConnectionProvider connectionProvider) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());

        final TemplateStatementParser parser = new TemplateStatementParser();

        this.getByIdStatement = parser
                .parse(RolesStatements.GET_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.getByNameStatement = parser
                .parse(RolesStatements.GET_BY_NAME_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser
                .parse(RolesStatements.INSERT_STATEMENT)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public RoleDO save(final RoleDO roleDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(roleDO);

            queryRunner.execute(preparedStatement);

            return roleDO;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RoleDO> getById(final String id) {
        try {
            final PreparedStatement preparedStatement = getByIdStatement.build(ImmutableMap.of("id", id));
            final List<RoleDO> roles = queryRunner.execute(preparedStatement, RoleDO.class);

            return roles.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<RoleDO> getAll() {
        return null;
    }

    @Override
    public Optional<RoleDO> getByName(final String name) {
        try {
            final PreparedStatement preparedStatement = getByNameStatement.build(ImmutableMap.of("name", name));
            final List<RoleDO> roles = queryRunner.execute(preparedStatement, RoleDO.class);

            return roles.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RoleDO> update(final RoleDO roleDO) {
        return Optional.empty();
    }

    @Override
    public Optional<RoleDO> delete(final String s) {
        return Optional.empty();
    }
}

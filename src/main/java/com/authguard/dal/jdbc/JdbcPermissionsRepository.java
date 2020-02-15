package com.authguard.dal.jdbc;

import com.authguard.dal.PermissionsRepository;
import com.authguard.dal.jdbc.util.PreparedStatementParser;
import com.authguard.dal.jdbc.util.TemplateBuilder;
import com.authguard.dal.jdbc.util.TemplateStatement;
import com.authguard.dal.model.PermissionDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JdbcPermissionsRepository implements PermissionsRepository {
    private final static String GET_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("id = ${id}")
            .build();

    private final static String SEARCH_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("`group` = ${group} AND name = ${name}")
            .build();

    private final static String SEARCH_BY_GROUP_PERMISSION_STATEMENT = TemplateBuilder.select()
            .table("permissions")
            .whereStatement("`group` = ${group}")
            .build();

    private final static String INSERT_PERMISSION_STATEMENT = TemplateBuilder.insert()
            .table("permissions")
            .values(Arrays.asList("${id}", "${deleted}", "${group}", "${name}"))
            .build();

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement searchStatement;
    private final TemplateStatement searchByGroupStatement;
    private final TemplateStatement saveStatement;

    @Inject
    public JdbcPermissionsRepository(final ConnectionProvider connectionProvider) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());

        final PreparedStatementParser parser = new PreparedStatementParser();

        this.getByIdStatement = parser
                .parse(GET_PERMISSION_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.searchStatement = parser
                .parse(SEARCH_PERMISSION_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.searchByGroupStatement = parser
                .parse(SEARCH_BY_GROUP_PERMISSION_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser
                .parse(INSERT_PERMISSION_STATEMENT)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public PermissionDO save(final PermissionDO permissionDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(permissionDO);

            queryRunner.execute(preparedStatement);

            return permissionDO;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PermissionDO> getById(final String id) {
        try {
            final PreparedStatement preparedStatement = getByIdStatement.build(ImmutableMap.of("id", id));
            final List<PermissionDO> permissions = queryRunner.execute(preparedStatement, PermissionDO.class);

            return permissions.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PermissionDO> search(final String group, final String name) {
        try {
            final PreparedStatement preparedStatement = searchStatement
                    .build(ImmutableMap.of("group", group, "name", name));
            final List<PermissionDO> permissions = queryRunner.execute(preparedStatement, PermissionDO.class);

            return permissions.stream().findFirst();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PermissionDO> delete(final String s) {
        throw new UnsupportedOperationException("Delete is not supported");
    }

    @Override
    public Collection<PermissionDO> getAll() {
        throw new UnsupportedOperationException("Get all permissions is not supported");
    }

    @Override
    public Collection<PermissionDO> getAllForGroup(final String group) {
        try {
            final PreparedStatement preparedStatement = searchByGroupStatement
                    .build(ImmutableMap.of("group", group));

            return queryRunner.execute(preparedStatement, PermissionDO.class);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

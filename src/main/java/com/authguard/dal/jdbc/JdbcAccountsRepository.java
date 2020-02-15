package com.authguard.dal.jdbc;

import com.authguard.dal.AccountsRepository;
import com.authguard.dal.PermissionsRepository;
import com.authguard.dal.RolesRepository;
import com.authguard.dal.jdbc.model.AccountJoinedRow;
import com.authguard.dal.jdbc.util.PreparedStatementParser;
import com.authguard.dal.jdbc.util.TemplateBuilder;
import com.authguard.dal.jdbc.util.TemplateStatement;
import com.authguard.dal.model.AccountDO;
import com.authguard.dal.model.PermissionDO;
import com.authguard.dal.model.RoleDO;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcAccountsRepository implements AccountsRepository {
    private final static String GET_ACCOUNT_STATEMENT = TemplateBuilder.select()
            .table("accounts")
            .whereStatement("id = ${id}")
            .joinedStatement("account_permissions ON account_permissions.accountId = accounts.id LEFT JOIN account_roles ON account_roles.accountId = accounts.id")
            .build();

    private final static String INSERT_ACCOUNT_STATEMENT = TemplateBuilder.insert()
            .table("accounts")
            .values(Arrays.asList("${id}", "${deleted}", "${active}", "${scopes}"))
            .build();

    private static final String INSERT_ACCOUNT_PERMISSION_STATEMENT = TemplateBuilder.insert()
            .table("account_permissions")
            .values(Arrays.asList("${accountId}", "${permissionId}"))
            .build();

    private final JdbcQueryRunner queryRunner;

    private final TemplateStatement getByIdStatement;
    private final TemplateStatement saveStatement;
    private final TemplateStatement savePermissionsStatement;

    private final PermissionsRepository permissionsRepository;
    private final RolesRepository rolesRepository;

    @Inject
    public JdbcAccountsRepository(final ConnectionProvider connectionProvider,
                                  final PermissionsRepository permissionsRepository, final RolesRepository rolesRepository) throws SQLException {
        this.queryRunner = new JdbcQueryRunner(connectionProvider.getConnection());
        this.permissionsRepository = permissionsRepository;
        this.rolesRepository = rolesRepository;

        final PreparedStatementParser parser = new PreparedStatementParser();

        this.getByIdStatement = parser.parse(GET_ACCOUNT_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.saveStatement = parser.parse(INSERT_ACCOUNT_STATEMENT)
                .prepare(connectionProvider.getConnection());

        this.savePermissionsStatement = parser.parse(INSERT_ACCOUNT_PERMISSION_STATEMENT)
                .prepare(connectionProvider.getConnection());
    }

    @Override
    public AccountDO save(final AccountDO accountDO) {
        try {
            final PreparedStatement preparedStatement = saveStatement.build(accountDO);

            queryRunner.execute(preparedStatement);

            for (final PermissionDO permission : accountDO.getPermissions()) {
                queryRunner.execute(savePermissionsStatement
                        .build(ImmutableMap.of("accountId", accountDO.getId(), "permissionId", permission.getId())));
            }

            return accountDO;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AccountDO> getById(final String id) {
        try {
            final PreparedStatement preparedStatement = getByIdStatement.build(ImmutableMap.of("id", id));
            final List<AccountJoinedRow> rows = queryRunner.execute(preparedStatement, AccountJoinedRow.class);

            final Optional<AccountJoinedRow> chosenRow = rows.stream()
                    .filter(row -> row.getId().equals(id))
                    .findFirst();

            if (chosenRow.isEmpty()) {
                return Optional.empty();
            }

            final AccountDO.Builder accountBuilder = AccountDO.builder()
                    .id(chosenRow.get().getId())
                    .deleted(chosenRow.get().isDeleted())
                    .active(chosenRow.get().isActive())
                    .scopes(Collections.singletonList(chosenRow.get().getScopes()));

            accountBuilder.permissions(getPermissionsForAccount(rows));
            accountBuilder.roles(getRolesForAccount(rows));

            return Optional.of(accountBuilder.build());
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AccountDO> update(final AccountDO accountDO) {
        throw new UnsupportedOperationException("Update is currently not support");
    }

    @Override
    public List<AccountDO> getAdmins() {
        throw new UnsupportedOperationException("Get all admins operation is currently not support");
    }

    private List<PermissionDO> getPermissionsForAccount(final List<AccountJoinedRow> rows) {
        return rows.stream()
                .map(AccountJoinedRow::getPermissionId)
                .distinct()
                .map(permissionsRepository::getById)
                .map(opt -> opt.orElseThrow(() -> new RuntimeException("Permission not found")))
                .collect(Collectors.toList());
    }

    private List<String> getRolesForAccount(final List<AccountJoinedRow> rows) {
        return rows.stream()
                .map(AccountJoinedRow::getRoleId)
                .distinct()
                .map(rolesRepository::getById)
                .map(opt -> opt.orElseThrow(() -> new RuntimeException("Role not found")))
                .map(RoleDO::getName)
                .collect(Collectors.toList());
    }
}

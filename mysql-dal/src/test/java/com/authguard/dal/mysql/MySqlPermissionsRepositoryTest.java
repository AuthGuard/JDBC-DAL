package com.authguard.dal.mysql;

import com.authguard.dal.jdbc.ConnectionProvider;
import com.authguard.dal.jdbc.JdbcPermissionsRepository;
import com.authguard.dal.jdbc.TablesBootstrap;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;
import com.authguard.dal.model.PermissionDO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MySqlPermissionsRepositoryTest {

    private ConnectionProvider connectionProvider;
    private JdbcPermissionsRepository permissionsRepository;

    @BeforeAll
    void setup() throws SQLException {
        connectionProvider = new ConnectionProvider(
                ImmutableJdbcConfig.builder()
                        .connectionString("jdbc:mysql://127.0.0.1:3306/test")
                        .username("root")
                        .password("my-secret-pw")
                        .build());

        new TablesBootstrap(connectionProvider).bootstrapTable("permissions");

        permissionsRepository = new JdbcPermissionsRepository(connectionProvider);
    }

    @Test
    void getById() {
        final PermissionDO permission = PermissionDO.builder()
                .id(UUID.randomUUID().toString())
                .group("get-by-id-permission-group")
                .name("permission:create:get-by-id")
                .build();

        final PermissionDO saved = permissionsRepository.save(permission);

        assertThat(saved).isEqualTo(permission);

        final Optional<PermissionDO> retrieved = permissionsRepository.getById(permission.getId());

        assertThat(retrieved).contains(permission);
    }

    @Test
    void search() {
        final PermissionDO permission = PermissionDO.builder()
                .id(UUID.randomUUID().toString())
                .group("search-permission-group")
                .name("permission:create:search")
                .build();

        final PermissionDO saved = permissionsRepository.save(permission);

        assertThat(saved).isEqualTo(permission);

        final Optional<PermissionDO> retrieved = permissionsRepository.search(permission.getGroup(), permission.getName());

        assertThat(retrieved).contains(permission);
    }

    @Test
    void getAllForGroup() {
        final PermissionDO permission = PermissionDO.builder()
                .id(UUID.randomUUID().toString())
                .group("get-for-group-permission-group")
                .name("permission:create:search")
                .build();

        final PermissionDO saved = permissionsRepository.save(permission);

        assertThat(saved).isEqualTo(permission);

        final Collection<PermissionDO> retrieved = permissionsRepository.getAllForGroup(permission.getGroup());

        assertThat(retrieved).contains(permission);

        assertThat(retrieved.stream()
                .map(PermissionDO::getGroup)
                .filter(group -> !group.equals(permission.getGroup()))
                .findFirst()).isEmpty();
    }
}
package com.authguard.dal.mysql;

import com.authguard.dal.jdbc.ConnectionProvider;
import com.authguard.dal.jdbc.JdbcCredentialsRepository;
import com.authguard.dal.jdbc.TablesBootstrap;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;
import com.authguard.dal.model.CredentialsDO;
import com.authguard.dal.model.HashedPasswordDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MySqlCredentialsRepositoryTest {

    private JdbcCredentialsRepository credentialsRepository;

    @BeforeAll
    void setup() throws SQLException {
        final ConnectionProvider connectionProvider = new ConnectionProvider(
                ImmutableJdbcConfig.builder()
                        .connectionString("jdbc:mysql://127.0.0.1:3306/test")
                        .username("root")
                        .password("my-secret-pw")
                        .build());

        new TablesBootstrap(connectionProvider).bootstrapTable("credentials");

        credentialsRepository = new JdbcCredentialsRepository(connectionProvider);
    }

    @Test
    void getById() {
        final CredentialsDO credentials = CredentialsDO.builder()
                .id(UUID.randomUUID().toString())
                .username("get-by-id-username")
                .hashedPassword(HashedPasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .accountId("get-by-id-account-id")
                .build();

        final CredentialsDO saved = credentialsRepository.save(credentials);

        assertThat(saved).isEqualTo(credentials);

        final Optional<CredentialsDO> retrieved = credentialsRepository.getById(credentials.getId());

        assertThat(retrieved).contains(credentials);
    }

    @Test
    void findByUsername() {
        final CredentialsDO credentials = CredentialsDO.builder()
                .id(UUID.randomUUID().toString())
                .username("get-by-username-username")
                .hashedPassword(HashedPasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .accountId("get-username-account-id")
                .build();

        final CredentialsDO saved = credentialsRepository.save(credentials);

        assertThat(saved).isEqualTo(credentials);

        final Optional<CredentialsDO> retrieved = credentialsRepository.findByUsername(credentials.getUsername());

        assertThat(retrieved).contains(credentials);
    }
}
package com.authguard.dal.jdbc;

import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;
import com.authguard.dal.model.CredentialsAudit;
import com.authguard.dal.model.CredentialsAuditDO;
import com.authguard.dal.model.HashedPasswordDO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcCredentialsAudiRepositoryTest {

    private JdbcCredentialsAudiRepository credentialsAudiRepository;

    @BeforeAll
    void setup() throws SQLException {
        final ConnectionProvider connectionProvider = new ConnectionProvider(
                ImmutableJdbcConfig.builder()
                        .connectionString("jdbc:mysql://127.0.0.1:3306/test")
                        .username("root")
                        .password("my-secret-pw")
                        .build());

        new TablesBootstrap(connectionProvider).bootstrapTable("credentials_audit");

        credentialsAudiRepository = new JdbcCredentialsAudiRepository(connectionProvider);
    }

    @Test
    void getById() {
        final CredentialsAuditDO credentialsAudit = CredentialsAuditDO.builder()
                .id(UUID.randomUUID().toString())
                .action(CredentialsAudit.Action.UPDATED)
                .credentialId("get-by-id-credential-id")
                .username("get-by-id-username")
                .password(HashedPasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsAuditDO saved = credentialsAudiRepository.save(credentialsAudit);

        assertThat(saved).isEqualTo(credentialsAudit);

        final Optional<CredentialsAuditDO> retrieved = credentialsAudiRepository.getById(credentialsAudit.getId());

        assertThat(retrieved).contains(credentialsAudit);
    }

    @Test
    void findByCredentialsId() {
        final CredentialsAuditDO credentialsAudit = CredentialsAuditDO.builder()
                .id(UUID.randomUUID().toString())
                .action(CredentialsAudit.Action.UPDATED)
                .credentialId("get-by-credentials-id-credential-id")
                .username("get-by-id-username")
                .password(HashedPasswordDO.builder()
                        .password("password")
                        .salt("salt")
                        .build())
                .build();

        final CredentialsAuditDO saved = credentialsAudiRepository.save(credentialsAudit);

        assertThat(saved).isEqualTo(credentialsAudit);

        final Collection<CredentialsAuditDO> retrieved = credentialsAudiRepository.findByCredentialsId(credentialsAudit.getCredentialId());

        assertThat(retrieved).contains(credentialsAudit);
        assertThat(retrieved.stream()
                .map(CredentialsAuditDO::getCredentialId)
                .filter(credentialId -> !credentialId.equals(credentialsAudit.getCredentialId()))
        ).isEmpty();
    }
}
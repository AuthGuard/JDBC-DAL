package com.authguard.dal.jdbc.config;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        jdkOnly = true,
        validationMethod = Value.Style.ValidationMethod.NONE
)
public interface JdbcConfig {
    String getConnectionString();
    String getUsername();
    String getPassword();
}

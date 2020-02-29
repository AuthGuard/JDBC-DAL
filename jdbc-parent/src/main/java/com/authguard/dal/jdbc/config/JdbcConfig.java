package com.authguard.dal.jdbc.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        jdkOnly = true,
        validationMethod = Value.Style.ValidationMethod.NONE
)
@JsonSerialize(as = ImmutableJdbcConfig.class)
@JsonDeserialize(as = ImmutableJdbcConfig.class)
public interface JdbcConfig {
    String getConnectionString();
    String getUsername();
    String getPassword();
}

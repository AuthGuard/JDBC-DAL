package com.authguard.dal.jdbc;

import com.authguard.dal.ApiKeysRepository;
import com.authguard.dal.model.ApiKeyDO;

import java.util.Collection;
import java.util.Optional;

public class JdbcApiKeysRepository implements ApiKeysRepository {
    @Override
    public ApiKeyDO save(final ApiKeyDO apiKeyDO) {
        return null;
    }

    @Override
    public Optional<ApiKeyDO> getById(final String s) {
        return Optional.empty();
    }

    @Override
    public Collection<ApiKeyDO> getByAppId(final String s) {
        return null;
    }

    @Override
    public Optional<ApiKeyDO> update(final ApiKeyDO apiKeyDO) {
        return Optional.empty();
    }

    @Override
    public Optional<ApiKeyDO> delete(final String s) {
        return Optional.empty();
    }
}

package com.authguard.dal.jdbc;

import com.authguard.dal.ApplicationsRepository;
import com.authguard.dal.model.AppDO;

import java.util.List;
import java.util.Optional;

public class JdbcApplicationRepository implements ApplicationsRepository {
    @Override
    public AppDO save(final AppDO appDO) {
        return null;
    }

    @Override
    public Optional<AppDO> getById(final String s) {
        return Optional.empty();
    }

    @Override
    public Optional<AppDO> update(final AppDO appDO) {
        return Optional.empty();
    }

    @Override
    public Optional<AppDO> delete(final String s) {
        return Optional.empty();
    }

    @Override
    public List<AppDO> getAllForAccount(final String s) {
        return null;
    }
}

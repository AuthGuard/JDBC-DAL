package com.authguard.dal.jdbc;

import com.authguard.dal.OtpRepository;
import com.authguard.dal.model.OneTimePasswordDO;

import java.util.Optional;

public class JdbcOtpRepository implements OtpRepository {
    @Override
    public OneTimePasswordDO save(final OneTimePasswordDO oneTimePasswordDO) {
        return null;
    }

    @Override
    public Optional<OneTimePasswordDO> getById(final String s) {
        return Optional.empty();
    }
}

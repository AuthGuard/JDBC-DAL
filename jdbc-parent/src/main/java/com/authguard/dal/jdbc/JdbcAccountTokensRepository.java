package com.authguard.dal.jdbc;

import com.authguard.dal.AccountTokensRepository;
import com.authguard.dal.model.AccountTokenDO;

import java.util.Optional;

public class JdbcAccountTokensRepository implements AccountTokensRepository {
    @Override
    public AccountTokenDO save(final AccountTokenDO accountTokenDO) {
        return null;
    }

    @Override
    public Optional<AccountTokenDO> getByToken(final String s) {
        return Optional.empty();
    }
}

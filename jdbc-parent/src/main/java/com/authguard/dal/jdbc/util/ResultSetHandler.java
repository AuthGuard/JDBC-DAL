package com.authguard.dal.jdbc.util;

import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ResultSetHandler extends MapListHandler {
    public List<Map<String, Object>> handle(final ResultSet rs) throws SQLException {
        return super.handle(rs);
    }
}

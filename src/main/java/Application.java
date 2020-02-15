import com.authguard.dal.jdbc.*;
import com.authguard.dal.jdbc.config.ImmutableJdbcConfig;

import java.sql.SQLException;

public class Application {

    public static void main(String[] args) throws SQLException {
        // create the connection
        final ConnectionProvider connectionProvider = new ConnectionProvider(
                ImmutableJdbcConfig.builder()
                        .connectionString("jdbc:mysql://127.0.0.1:3306/test")
                        .username("root")
                        .password("my-secret-pw")
                        .build()
        );

        // create the tables
        final TablesBootstrap tablesBootstrap = new TablesBootstrap(connectionProvider);

        tablesBootstrap.bootstrap();
    }
}

package com.kap.mechanics_api.migration;

import java.util.Map;
import org.flywaydb.core.Flyway;

/** Executa somente o Flyway, sem iniciar servidor HTTP, JPA ou contexto Spring. */
public final class DatabaseMigrations {
    private DatabaseMigrations() {
    }

    public static void migrate(Map<String, String> environment) {
        Flyway.configure()
                .dataSource(required(environment, "SPRING_DATASOURCE_URL"),
                        required(environment, "SPRING_DATASOURCE_USERNAME"),
                        required(environment, "SPRING_DATASOURCE_PASSWORD"))
                .locations("classpath:db/migration")
                .validateMigrationNaming(true)
                .cleanDisabled(true)
                .load()
                .migrate();
    }

    private static String required(Map<String, String> environment, String key) {
        String value = environment.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variavel obrigatoria para migrations: " + key);
        }
        return value;
    }
}

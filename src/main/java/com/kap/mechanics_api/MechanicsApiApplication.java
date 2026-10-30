package com.kap.mechanics_api;

import com.kap.mechanics_api.migration.DatabaseMigrations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MechanicsApiApplication {

	public static void main(String[] args) {
		if (args.length == 1 && "--migrate-only".equals(args[0])) {
			DatabaseMigrations.migrate(System.getenv());
			return;
		}
		SpringApplication.run(MechanicsApiApplication.class, args);
	}

}

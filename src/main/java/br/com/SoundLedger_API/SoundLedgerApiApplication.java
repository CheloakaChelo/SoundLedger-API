package br.com.SoundLedger_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableScheduling
public class SoundLedgerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoundLedgerApiApplication.class, args);
	}

}

package it.polito.ai;

import com.fasterxml.jackson.databind.Module;
import it.polito.ai.models.Account;
import it.polito.ai.services.AccountService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.security.auth.login.AccountException;
import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}


//	@Bean @Qualifier("mainDataSource")
//	public DataSource dataSource(){
//		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//		EmbeddedDatabase db = builder
//				.setType(EmbeddedDatabaseType.H2)
//				.build();
//		return db;
//	}

//	@Bean
//	CommandLineRunner init(AccountService accountService) {
//		return args -> {
//			String[] usernames = {"user", "admin","john", "robert", "ana"};
//			for (String username: usernames) {
//				Account acct = new Account();
//				acct.setUsername(username);
//				
//				if (username.equals("user")) {
//					acct.setPassword("password");
//				} else {
//					acct.setPassword(passwordEncoder().encode("password"));
//				}
//				
//				acct.grantAuthority("ROLE_USER");
//				if (username.equals("admin")) {
//					acct.grantAuthority("ROLE_ADMIN");
//				}
//
//				try {
//					accountService.register(acct);
//				} catch (AccountException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//	}
}

package stegochat.stegochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import stegochat.stegochat.config.DotenvConfig;

@SpringBootApplication
public class StegochatApplication {

	public static void main(String[] args) {
		
		DotenvConfig.loadSystemProperties();
		
		SpringApplication.run(StegochatApplication.class, args);
	}

}

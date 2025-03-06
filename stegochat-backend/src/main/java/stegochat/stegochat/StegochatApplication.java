package stegochat.stegochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import stegochat.stegochat.security.DotenvConfig;


@SpringBootApplication
@EnableMongoAuditing
public class StegochatApplication {

	public static void main(String[] args) {
		
		String profile = System.getenv("PROFILES_ACTIVE");
        
        if (profile == null || profile.equals("dev")) {
            DotenvConfig.loadSystemProperties();
        }
		
		SpringApplication.run(StegochatApplication.class, args);
	}

}

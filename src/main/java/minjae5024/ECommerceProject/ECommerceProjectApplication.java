package minjae5024.ECommerceProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ECommerceProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceProjectApplication.class, args);
	}

}

package eccomerce.backend_eccomerce;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendEccomerceApplication {

	public static void main(String[] args) {
		// 1. Load the .env file
		// ignoreIfMissing() prevents the app from failing if the file is not found (e.g., in production)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		
		// 2. Map .env variables to Java System Properties
		// This allows Spring to use ${JWT_SECRET} as if it were a real environment variable
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		// 3. Start the application
		SpringApplication.run(BackendEccomerceApplication.class, args);
	}

}
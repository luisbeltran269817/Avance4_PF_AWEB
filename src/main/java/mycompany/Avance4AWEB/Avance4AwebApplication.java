package mycompany.Avance4AWEB;

import mycompany.Avance4AWEB.security.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Avance4AwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(Avance4AwebApplication.class, args);
	}

	//el filtro se aplica a todas las rutas que empiezan con /api/
	@Bean
	public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
		FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(jwtFilter);
		registration.addUrlPatterns("/api/*");
		return registration;
	}

}
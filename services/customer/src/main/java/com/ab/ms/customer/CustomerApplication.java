package com.ab.ms.customer;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ab.ms.customer.dto.CustomerMsContactInfoDto;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableConfigurationProperties(value = {CustomerMsContactInfoDto.class})
@OpenAPIDefinition(
	info = @Info(
		title = "Customer Microservice REST API Documentation",
		description = "REST APIs for ABMS Customer Microservice",
		version = "v1",
		contact = @Contact(
			name = "Adrian Blake",
			email = "adrian@gmail.com",
			url = "adrianBlog@blogspot.com"
		),
		license = @License(
			name = "Apache 2.0",
			url = "https://www.apache.org/licenses/LICENSE-2.0"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "REST APIs for ABMS Customer Microservice",
		url = "docs.com"
	)
)
public class CustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

}

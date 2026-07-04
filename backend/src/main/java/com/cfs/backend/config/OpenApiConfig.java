package com.cfs.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		return new OpenAPI()

				.info(

						new Info()

								.title("Collaborative Sync Engine API")
								.version("1.0")
								.description("Distributed real-time collaborative editing platform")
				);
	}
}

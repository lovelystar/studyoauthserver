package com.hard.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OAuthServerApplication extends SpringBootServletInitializer {
	
	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(OAuthServerApplication.class);
		
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		return application.sources(OAuthServerApplication.class);
		
	}
	
}

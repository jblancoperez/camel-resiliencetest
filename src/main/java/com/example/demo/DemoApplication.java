package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@Configuration
public class DemoApplication {

	

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> customizer() {
		return (custom) -> custom.config().commonTags("transaction", "test");
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

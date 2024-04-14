package io.agilehandy.demoreadmsword;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class DemoReadMswordApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoReadMswordApplication.class, args);
	}

	@Bean
	public CommandLineRunner cmdLineRunner(ApplicationContext ctx, MSWordReader reader) {
		return args -> reader.read();
	}

}

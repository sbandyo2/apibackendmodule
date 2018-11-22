package com.ibm.backendservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = { "com.ibm.csaservice","com.ibm.controller"} )
public class BackendServiceApplication {

	Logger logger = LoggerFactory.getLogger(BackendServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
		writePID();
	}
	
	/**
	 * Writes the process ID for this microservice
	 */
	private static void writePID() {
		SpringApplicationBuilder app = new SpringApplicationBuilder(BackendServiceApplication.class).web(WebApplicationType.NONE);
		app.build().addListeners(new ApplicationPidFileWriter("backenedshutdown.pid")); //
		app.run();
	}

}

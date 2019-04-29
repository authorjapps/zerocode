package com.zerocode.openapi.stepgen;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.Bootstrap;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zerocode.cli","com.zerocode"})
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class StepgenApplication {

	public static void main(String[] args) throws IOException {
		Bootstrap.main(args);
	}

}

package com.zerocode.oas.stepgen;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class StepgenApplication {

	public static void main(String[] args) {
		SpringApplication.run(StepgenApplication.class, args);
	}
	
    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("zerocode:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}

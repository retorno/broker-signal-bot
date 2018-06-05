package br.com.broker.signal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiBrokerSignalBot {

	public static void main(String[] args) {
		SpringApplication.run(ApiBrokerSignalBot.class, args);
	}
	
}

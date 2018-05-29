package br.com.broker.signal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import br.com.broker.signal.utils.Global;

@SpringBootApplication
@EnableScheduling
public class ApiBrokerSignalBot {

	//java -jar broker-signal-bot-0.0.1-SNAPSHOT.jar 1 Buy 1 40 40
	
	public static void main(String[] args) {
		
		Global.SIMULATION = args[0].equals("0") ? false : true;
		Global.ORDER_TYPE = args[1];
		Global.INICIAL_CONTRACTS = Integer.parseInt(args[2]);
		Global.LOSE_VARIANCE = Integer.parseInt(args[3]);
		Global.WIN_VARIANCE = Integer.parseInt(args[4]);
		
		SpringApplication.run(ApiBrokerSignalBot.class, args);
	}
	
}

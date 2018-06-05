package br.com.broker.signal.logic;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.broker.signal.model.Result;
import br.com.broker.signal.repository.ResultRepository;
import br.com.broker.signal.repository.ShoppingRepository;
import br.com.broker.signal.rest.client.BrokerSignalScrapAPIClient;

@Component
public class LogicBot {
	
	private static Boolean CLEAN_DATABASE = true;
	
	@Autowired
	private ShoppingRepository shoppingRepository;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private BrokerSignalScrapAPIClient client;
	
	@Autowired
	private LogicOne logicOne;
	@Autowired
	private LogicTwo logicTwo;
	
	@Scheduled(cron="*/1 * * * * *")
	public void doTheLogic() throws Exception{
		if(CLEAN_DATABASE) cleanDataBase();
		logicTwo.execute();
	}
	
	@Scheduled(cron="*/120 * * * * *")
	public void printLucroPrejuizo(){
		
		Result result = (resultRepository.findAll() != null && resultRepository.findAll().size() > 0)
				? resultRepository.findAll().get(0) : new Result(0L, 0L);
		
		System.out.println(new Date()+" ************************************************ ");
		System.out.println(new Date()+" ********* LUCRO/PREJUIZO:  "+ client.getRecipe());
		System.out.println(new Date()+" ********* QT LOSE       :  "+ result.getQtStops());
		System.out.println(new Date()+" ********* QT WIN        :  "+ result.getQtWin());
		System.out.println(new Date()+" ************************************************ ");
		
	}
	
	public void cleanDataBase(){
		shoppingRepository.deleteAll();
		resultRepository.deleteAll();
		CLEAN_DATABASE = false;
	}

	

}

package br.com.broker.signal.logic;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.broker.signal.model.Result;
import br.com.broker.signal.model.Shopping;
import br.com.broker.signal.repository.ResultRepository;
import br.com.broker.signal.repository.ShoppingRepository;
import br.com.broker.signal.rest.client.BrokerSignalScrapAPIClient;
import br.com.broker.signal.utils.Global;

@Component
public class LogicBot {
	
	private static Boolean CLEAN_DATABASE = true;
	private String orderType = Global.ORDER_TYPE;
	private Boolean isQtStopToW8 = false;
	
	@Autowired
	private ShoppingRepository shoppingRepository;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private BrokerSignalScrapAPIClient client;
	
	@Scheduled(cron="*/2 * * * * *")
	public void doTheLogic() throws Exception{
		if(CLEAN_DATABASE) cleanDataBase();
		
		Shopping shoppingPosition = (shoppingRepository.findAll() != null && shoppingRepository.findAll().size() > 0) 
				? shoppingRepository.findAll().get(0) : null;
		Result result = (resultRepository.findAll() != null && resultRepository.findAll().size() > 0)
				? resultRepository.findAll().get(0) : new Result(0L, 0L);
		
		if(!isQtStopToW8 && result.getQtStops() == Global.QT_STOP_TOW8) {
			System.out.println(new Date()+" ===>> Aguardando  : "+Global.MILLISEC_TOW8+" Millisegundos para retornar! ("+Global.QT_STOP_TOW8+" Stops)");
			Thread.sleep(Global.MILLISEC_TOW8);
			isQtStopToW8 = true;
		}
		if(result.getQtStops() == Global.QT_STOP_TOINVERT) {
			if(orderType.equals("Buy")) setOrderType("Sell");
			else setOrderType("Buy");
			System.out.println(new Date()+" ===>> Invertendo p/ : "+orderType+" ("+Global.QT_STOP_TOINVERT+" Stops)");
			result.setQtStops(0L);
		}
		
		Long lastPrice = Long.parseLong(client.getLastPrice());
		Long position = shoppingPosition == null ? Long.parseLong(client.getPosition()) : shoppingPosition.getPosition();
		
		System.out.println(new Date()+" ===>> Position    : "+position+" ===>> Last Price: "+lastPrice);
		
		if(position == 0) {
			
			Long stopLoss = (orderType.equals("Buy") ? lastPrice-Global.STOP_VARIANCE : lastPrice+Global.STOP_VARIANCE);
			client.changeStop(position+Global.INICIAL_CONTRACTS, orderType, stopLoss);
			
			System.out.println(new Date()+" ===>> Order("+orderType+") : "+ (position+Global.INICIAL_CONTRACTS) +" Contratos --- Price: "+lastPrice);
			System.out.println(new Date()+" ===>> Stop        : "+ (position+Global.INICIAL_CONTRACTS) +" Contratos --- Price: "+stopLoss);
			
			//Salva compra
			shoppingRepository.save(new Shopping(position+Global.INICIAL_CONTRACTS, lastPrice, 
					orderType.equals("Buy") ? lastPrice-Global.STOP_VARIANCE : lastPrice+Global.STOP_VARIANCE, 
					orderType.equals("Buy") ? lastPrice+Global.DOUBLE_VARIANCE : lastPrice-Global.DOUBLE_VARIANCE));
		}else {
			
			if((orderType.equals("Buy") && lastPrice <= shoppingPosition.getSellPrice()) ||
			   (orderType.equals("Sell") && lastPrice >= shoppingPosition.getSellPrice())) {
				
				System.out.println(new Date()+" ===>> Stop        : "+position+" ===>> Last Price: "+lastPrice);
				shoppingRepository.deleteAll();
				
				result.setPointsStop(orderType.equals("Buy") ? 
						result.getPointsStop() + ((lastPrice - shoppingPosition.getBuyPrice()) / position) :
						result.getPointsStop() - ((lastPrice - shoppingPosition.getBuyPrice()) / position));
				
				if(position == 1) {
					result.setQtStops(result.getQtStops() + 1);
				}
			
			}else if((orderType.equals("Buy") && lastPrice >= shoppingPosition.getDoublePositionPrice()) ||
					 (orderType.equals("Sell") && lastPrice <= shoppingPosition.getDoublePositionPrice())) {

				Long stopLoss = (orderType.equals("Buy") ? lastPrice-Global.STOP_VARIANCE : lastPrice+Global.STOP_VARIANCE);
				
				System.out.println(new Date()+" ===>> Double      : "+position*2+" Contratos ===>> Last Price: "+lastPrice);
				System.out.println(new Date()+" ===>> Stop        : "+position*2+" Contratos ===>> Price: "+stopLoss);
				
				shoppingPosition.setPosition(position*2);
				shoppingPosition.setSellPrice(orderType.equals("Buy") ? lastPrice-Global.STOP_VARIANCE : lastPrice+Global.STOP_VARIANCE);
				shoppingPosition.setDoublePositionPrice(orderType.equals("Buy") ? lastPrice+Global.DOUBLE_VARIANCE : lastPrice-Global.DOUBLE_VARIANCE);
				
				shoppingRepository.save(shoppingPosition);
				result.setQtStops(0L);
				
				client.changeStop(position*2, orderType, stopLoss);
			}
			
		}
		resultRepository.save(result);
	}
	
	@Scheduled(cron="*/300 * * * * *")
	public void printLucroPrejuizo(){
		
		Result result = (resultRepository.findAll() != null && resultRepository.findAll().size() > 0)
				? resultRepository.findAll().get(0) : new Result(0L, 0L);
		
		System.out.println(new Date()+" ************************************************ ");
		System.out.println(new Date()+" ********* LUCRO/PREJUIZO:  "+client.getRecipe());
		System.out.println(new Date()+" ********* RESULT POINT  :  "+ result.getPointsStop());
		System.out.println(new Date()+" ************************************************ ");
		
	}
	
	public void cleanDataBase(){
		shoppingRepository.deleteAll();
		resultRepository.deleteAll();
		CLEAN_DATABASE = false;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}

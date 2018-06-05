package br.com.broker.signal.logic;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.broker.signal.model.Result;
import br.com.broker.signal.model.Shopping;
import br.com.broker.signal.repository.ResultRepository;
import br.com.broker.signal.repository.ShoppingRepository;
import br.com.broker.signal.rest.client.BrokerSignalScrapAPIClient;
import br.com.broker.signal.utils.Global;

@Component
public class LogicTwo {
	
	private String orderType = Global.ORDER_TYPE;

	@Autowired
	private ShoppingRepository shoppingRepository;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private BrokerSignalScrapAPIClient client;
	
	private String operation;
	
	public void execute() throws Exception {
		Shopping shoppingPosition = (shoppingRepository.findAll() != null && shoppingRepository.findAll().size() > 0) 
				? shoppingRepository.findAll().get(0) : null;
		
		Result result = null;
		if(resultRepository.findAll() != null && resultRepository.findAll().size() > 0) {
			result =  resultRepository.findAll().get(0);
		}else {
			result = new Result(0L, 0L, 0L, 0L);
		}
		
		if(result.getQtStopsAux() == 2) {
			if(orderType.equals("Buy")) orderType = "Sell";
			else orderType = "Buy";
			result.setQtStopsAux(0L);
			resultRepository.save(result);
			shoppingRepository.deleteAll();
			shoppingPosition = null;
			Thread.sleep(30000);
		}
		
		Long lastPrice = Long.parseLong(client.getLastPrice());
		Integer position = Integer.parseInt(client.getPosition());
		
		System.out.println(new Date()+" ===>> Position    : "+position+" ===>> Last Price: "+lastPrice);
		
		if(position != 0 && shoppingPosition != null) {
			
			if((position > 0 && (lastPrice <= shoppingPosition.getBuyPrice()-(Global.LOSE_VARIANCE*2))) ||
			   (position < 0 && (lastPrice >= shoppingPosition.getBuyPrice()+(Global.LOSE_VARIANCE*2)))){
			
				client.zerarAll();
				result.setQtWinAux(0L);
				resultRepository.save(result);
				Thread.sleep(2000);
			
			}else {
				
				if(position > 0) {
					operation = "Buy";
				}else if(position < 0)  {
					operation = "Sell";
				}

				if((position > 0 && (lastPrice >= shoppingPosition.getBuyPrice()+Global.WIN_VARIANCE)) ||
				   (position < 0 && (lastPrice <= shoppingPosition.getBuyPrice()-Global.WIN_VARIANCE))){
					
					System.out.println(new Date()+" ===>> Win ");
					
					client.changeStop(position, operation, Global.LOSE_VARIANCE, Global.WIN_VARIANCE, 0L);
					
					shoppingPosition.setBuyPrice(lastPrice);
					shoppingRepository.save(shoppingPosition);
					
					result.setQtWin(result.getQtWin()+1);
					result.setQtWinAux(result.getQtWinAux()+1);
					resultRepository.save(result);
					
					Thread.sleep(2000);
					
					if(result.getQtWinAux() == 5) {
						client.zerarAll();
						result.setQtWinAux(0L);
						resultRepository.save(result);
						Thread.sleep(2000);
					}
				}
			}
			
		}else if(position != 0 && shoppingPosition == null) {
	
			shoppingRepository.deleteAll();
			shoppingRepository.save(new Shopping(position, lastPrice, Global.LOSE_VARIANCE, Global.WIN_VARIANCE));
			shoppingPosition = shoppingRepository.findAll().get(0);

		}else if(position == 0 && shoppingPosition != null) {
		
			System.out.println(new Date()+" ===>> Lose ");
			shoppingRepository.deleteAll();
			result.setQtStops(result.getQtStops()+1);
			result.setQtStopsAux(result.getQtStopsAux()+1);
			result.setQtWinAux(0L);
			resultRepository.save(result);
		
		}else if(position == 0 && shoppingPosition == null) {
		
			client.changeStop(Global.INICIAL_CONTRACTS, orderType, Global.LOSE_VARIANCE, Global.WIN_VARIANCE, 1L);
			Thread.sleep(2000);
			shoppingRepository.deleteAll();
			shoppingRepository.save(new Shopping(Global.INICIAL_CONTRACTS, lastPrice, Global.LOSE_VARIANCE, Global.WIN_VARIANCE));
			shoppingPosition = shoppingRepository.findAll().get(0);
		
		}
	}
	
}

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
public class LogicOne {

	private String orderType = Global.ORDER_TYPE;
	
	@Autowired
	private ShoppingRepository shoppingRepository;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private BrokerSignalScrapAPIClient client;
	
	public void execute() throws Exception {
		Shopping shoppingPosition = (shoppingRepository.findAll() != null && shoppingRepository.findAll().size() > 0) 
				? shoppingRepository.findAll().get(0) : null;
		Result result = (resultRepository.findAll() != null && resultRepository.findAll().size() > 0)
				? resultRepository.findAll().get(0) : new Result(0L, 0L, 0L);
		
		if(result.getQtStopsAux() == 2) {
			System.out.println("2 STOPs --- PLS RETRY BOT");
			return;
		}
				
		Long lastPrice = Long.parseLong(client.getLastPrice());
		Long position = shoppingPosition == null ? Long.parseLong(client.getPosition()) : shoppingPosition.getPosition();
		
		System.out.println(new Date()+" ===>> Position    : "+position+" ===>> Last Price: "+lastPrice);
		
		if(position == 0) {
			
			client.changeStop(position+Global.INICIAL_CONTRACTS, orderType, Global.LOSE_VARIANCE, Global.WIN_VARIANCE);
			
			System.out.println(new Date()+" ===>> Order("+orderType+") : "+ (position+Global.INICIAL_CONTRACTS) +" Contratos --- Price: "+lastPrice);
			System.out.println(new Date()+" ===>> Stop        : "+ (position+Global.INICIAL_CONTRACTS) +" Contratos --- Price: "+(orderType.equals("Buy") ? lastPrice-Global.LOSE_VARIANCE : lastPrice+Global.LOSE_VARIANCE));
			
			//Salva compra
			shoppingRepository.save(new Shopping(position+Global.INICIAL_CONTRACTS, lastPrice, 
				orderType.equals("Buy") ? lastPrice-Global.LOSE_VARIANCE : lastPrice+Global.LOSE_VARIANCE, 
				orderType.equals("Buy") ? lastPrice+Global.WIN_VARIANCE : lastPrice-Global.WIN_VARIANCE));
			
			shoppingPosition = shoppingRepository.findAll().get(0);
			
		}else {
			
			if((orderType.equals("Buy") && lastPrice <= shoppingPosition.getSellPrice()) ||
			   (orderType.equals("Sell") && lastPrice >= shoppingPosition.getSellPrice())) {
				
				System.out.println(new Date()+" ===>> Stop        : "+position+" ===>> Last Price: "+lastPrice);
				
				result.setQtStops(result.getQtStops()+1);
				result.setQtStopsAux(result.getQtStopsAux()+1);
				
				if(result.getQtStopsAux() == 2) {
					shoppingRepository.deleteAll();
				}else {
					shoppingPosition.setSellPrice(
							orderType.equals("Buy") ? lastPrice-Global.LOSE_VARIANCE : lastPrice+Global.LOSE_VARIANCE);
				}
				
			}else if((orderType.equals("Buy") && lastPrice >= shoppingPosition.getDoublePositionPrice()) ||
					 (orderType.equals("Sell") && lastPrice <= shoppingPosition.getDoublePositionPrice())) {

				System.out.println(new Date()+" ===>> Stop Win    : "+position+" Contratos ===>> Last Price: "+lastPrice);
				
				shoppingPosition.setDoublePositionPrice(
						orderType.equals("Buy") ? lastPrice+Global.WIN_VARIANCE : lastPrice-Global.WIN_VARIANCE);
				result.setQtStopsAux(0L);
				result.setQtWin(result.getQtWin()+1);
				
				client.changeStop(position*2, orderType, Global.LOSE_VARIANCE, Global.WIN_VARIANCE);
				System.out.println(new Date()+" ===>> Stop        : "+ (position*2) +" Contratos --- Price: "+(orderType.equals("Buy") ? lastPrice-Global.LOSE_VARIANCE : lastPrice+Global.LOSE_VARIANCE));
			}
			
		}
		resultRepository.save(result);
	}
	
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
}

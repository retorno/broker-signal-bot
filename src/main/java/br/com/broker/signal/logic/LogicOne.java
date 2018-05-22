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

/*
 * Logica baseada na compra (BUY/SELL) com stops pré montados e visa duplicar a possição em x na alavancagem
 */
@Component
public class LogicOne {

	private String orderType = Global.L1_ORDER_TYPE;
	private Boolean isQtStopToW8 = false;
	
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
				? resultRepository.findAll().get(0) : new Result(0L, 0L);
		
		if(!isQtStopToW8 && result.getQtStops() == Global.L1_QT_STOP_TOW8) {
			System.out.println(new Date()+" ===>> Aguardando  : "+Global.L1_MILLISEC_TOW8+" Millisegundos para retornar! ("+Global.L1_QT_STOP_TOW8+" Stops)");
			Thread.sleep(Global.L1_MILLISEC_TOW8);
			isQtStopToW8 = true;
		}
		if(result.getQtStops() == Global.L1_QT_STOP_TOINVERT) {
			if(orderType.equals("Buy")) setOrderType("Sell");
			else setOrderType("Buy");
			System.out.println(new Date()+" ===>> Invertendo p/ : "+orderType+" ("+Global.L1_QT_STOP_TOINVERT+" Stops)");
			result.setQtStops(0L);
		}
		
		Long lastPrice = Long.parseLong(client.getLastPrice());
		Long position = shoppingPosition == null ? Long.parseLong(client.getPosition()) : shoppingPosition.getPosition();
		
		System.out.println(new Date()+" ===>> Position    : "+position+" ===>> Last Price: "+lastPrice);
		
		if(position == 0) {
			
			Long stopLoss = (orderType.equals("Buy") ? lastPrice-Global.L1_STOP_VARIANCE : lastPrice+Global.L1_STOP_VARIANCE);
			client.changeStop(position+Global.L1_INICIAL_CONTRACTS, orderType, stopLoss);
			
			System.out.println(new Date()+" ===>> Order("+orderType+") : "+ (position+Global.L1_INICIAL_CONTRACTS) +" Contratos --- Price: "+lastPrice);
			System.out.println(new Date()+" ===>> Stop        : "+ (position+Global.L1_INICIAL_CONTRACTS) +" Contratos --- Price: "+stopLoss);
			
			//Salva compra
			shoppingRepository.save(new Shopping(position+Global.L1_INICIAL_CONTRACTS, lastPrice, 
					orderType.equals("Buy") ? lastPrice-Global.L1_STOP_VARIANCE : lastPrice+Global.L1_STOP_VARIANCE, 
					orderType.equals("Buy") ? lastPrice+Global.L1_DOUBLE_VARIANCE : lastPrice-Global.L1_DOUBLE_VARIANCE));
		}else {
			
			if((orderType.equals("Buy") && lastPrice <= shoppingPosition.getSellPrice()) ||
			   (orderType.equals("Sell") && lastPrice >= shoppingPosition.getSellPrice())) {
				
				System.out.println(new Date()+" ===>> Stop        : "+position+" ===>> Last Price: "+lastPrice);
				shoppingRepository.deleteAll();
				
				result.setQtStops(result.getQtStops()+1);
				
				if(position == 1) {
					result.setQtStops(result.getQtStops() + 1);
				}
			
			}else if((orderType.equals("Buy") && lastPrice >= shoppingPosition.getDoublePositionPrice()) ||
					 (orderType.equals("Sell") && lastPrice <= shoppingPosition.getDoublePositionPrice())) {

				Long stopLoss = (orderType.equals("Buy") ? lastPrice-Global.L1_STOP_VARIANCE : lastPrice+Global.L1_STOP_VARIANCE);
				
				System.out.println(new Date()+" ===>> Double      : "+position*2+" Contratos ===>> Last Price: "+lastPrice);
				System.out.println(new Date()+" ===>> Stop        : "+position*2+" Contratos ===>> Price: "+stopLoss);
				
				shoppingPosition.setPosition(position*2);
				shoppingPosition.setSellPrice(orderType.equals("Buy") ? lastPrice-Global.L1_STOP_VARIANCE : lastPrice+Global.L1_STOP_VARIANCE);
				shoppingPosition.setDoublePositionPrice(orderType.equals("Buy") ? lastPrice+Global.L1_DOUBLE_VARIANCE : lastPrice-Global.L1_DOUBLE_VARIANCE);
				
				shoppingRepository.save(shoppingPosition);
				result.setQtStops(0L);
				
				client.changeStop(position, orderType, stopLoss);
				result.setQtWin(result.getQtWin()+1);
			}
			
		}
		resultRepository.save(result);
	}
	
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
}

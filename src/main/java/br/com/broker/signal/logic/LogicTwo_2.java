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
 * Compra ou vende e para no lucro ou prejuizo em cima da variavel estimada - Tentativa de execução de menos ordens
 */
@Component
public class LogicTwo_2 {

	private String orderType = Global.L2_ORDER_TYPE;
	
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
			if(orderType.equals("Buy")) setOrderType("Sell");
			else setOrderType("Buy");
			result.setQtStopsAux(0L);
		}
				
		Long lastPrice = Long.parseLong(client.getLastPrice());
		Long position = shoppingPosition == null ? Long.parseLong(client.getPosition()) : shoppingPosition.getPosition();
		
		System.out.println(new Date()+" ===>> Position    : "+position+" ===>> Last Price: "+lastPrice);
		
		if(position == 0) {
			
			client.cancelOrder();
			client.zerarAll();
			Thread.sleep(2000);
			
			Long stopLoss = (orderType.equals("Buy") ? lastPrice-Global.L2_LOSE_VARIANCE : lastPrice+Global.L2_LOSE_VARIANCE);
			client.setOrder(position+Global.L2_INICIAL_CONTRACTS, orderType);
			
			System.out.println(new Date()+" ===>> Order("+orderType+") : "+ (position+Global.L2_INICIAL_CONTRACTS) +" Contratos --- Price: "+lastPrice);
			System.out.println(new Date()+" ===>> Stop        : "+ (position+Global.L2_INICIAL_CONTRACTS) +" Contratos --- Price: "+stopLoss);
			
			//Salva compra
			shoppingRepository.save(new Shopping(position+Global.L2_INICIAL_CONTRACTS, lastPrice, 
				orderType.equals("Buy") ? lastPrice-Global.L2_LOSE_VARIANCE : lastPrice+Global.L2_LOSE_VARIANCE, 
				orderType.equals("Buy") ? lastPrice+Global.L2_WIN_VARIANCE : lastPrice-Global.L2_WIN_VARIANCE));
			
			shoppingPosition = shoppingRepository.findAll().get(0);
			
		}else {
			
			if((orderType.equals("Buy") && lastPrice <= shoppingPosition.getSellPrice()) ||
			   (orderType.equals("Sell") && lastPrice >= shoppingPosition.getSellPrice())) {
				
				System.out.println(new Date()+" ===>> Stop        : "+position+" ===>> Last Price: "+lastPrice);
				
				result.setQtStops(result.getQtStops()+1);
				result.setQtStopsAux(result.getQtStopsAux()+1);
				
				if(result.getQtStopsAux() == 2) {
					shoppingRepository.deleteAll();
					client.zerarAll();
					Thread.sleep(2000);
				}else {
					shoppingPosition.setSellPrice(
							orderType.equals("Buy") ? lastPrice-Global.L2_LOSE_VARIANCE : lastPrice+Global.L2_LOSE_VARIANCE);
				}
				
			}else if((orderType.equals("Buy") && lastPrice >= shoppingPosition.getDoublePositionPrice()) ||
					 (orderType.equals("Sell") && lastPrice <= shoppingPosition.getDoublePositionPrice())) {

				System.out.println(new Date()+" ===>> Stop Win    : "+position+" Contratos ===>> Last Price: "+lastPrice);
				
				shoppingPosition.setDoublePositionPrice(
						orderType.equals("Buy") ? lastPrice+Global.L2_WIN_VARIANCE : lastPrice-Global.L2_WIN_VARIANCE);
				result.setQtStopsAux(0L);
				
				result.setQtWin(result.getQtWin()+1);
			}
			
		}
		resultRepository.save(result);
	}
	
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
}

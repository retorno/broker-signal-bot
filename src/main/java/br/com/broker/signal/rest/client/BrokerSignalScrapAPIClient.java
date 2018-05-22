package br.com.broker.signal.rest.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.broker.signal.utils.Global;

@Component
public class BrokerSignalScrapAPIClient {
	
	public String getLastPrice(){
		String URL = Global.URL_SCRAP_API+"/last-price";
		return new RestTemplate().getForObject(URL, String.class);
	}
	
	public String getPosition(){
		String URL = Global.URL_SCRAP_API+"/position";
		return new RestTemplate().getForObject(URL, String.class);
	}
	
	public String getRecipe(){
		String URL = Global.URL_SCRAP_API+"/recipe";
		return new RestTemplate().getForObject(URL, String.class);
	}
	
	public String zerarAll(){
		String URL = Global.URL_SCRAP_API+"/zerar-all";
		return new RestTemplate().getForObject(URL, String.class);
	}
	
	public void changeStop(Long quantityPosition, String operation, Long stopLoss) {
		if(Global.SIMULATION) return;
		
		String URL = Global.URL_SCRAP_API+"/change-stop";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("active","WINFUT");
		headers.set("quantity", ""+quantityPosition);
		headers.set("operation", operation);
		headers.set("stop_loss", ""+stopLoss);
		headers.set("production","1");
		headers.set("change_position", "1");

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		new RestTemplate().postForEntity(URL, request , String.class);
	}
	
	public void setOrder(Long quantity, String operation) {
		if(Global.SIMULATION) return;
		
		String URL = Global.URL_SCRAP_API+"/set-order";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("active","WINFUT");
		headers.set("quantity", ""+quantity);
		headers.set("operation", operation);
		headers.set("production","1");

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		new RestTemplate().postForEntity(URL, request , String.class);
	}
	
	public void setStop(Long quantity, String operation, Long stopLoss) {
		if(Global.SIMULATION) return;
		
		String URL = Global.URL_SCRAP_API+"/set-stop";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("active","WINFUT");
		headers.set("quantity", ""+quantity);
		headers.set("operation", operation);
		headers.set("stop_loss", ""+stopLoss);
		headers.set("production","1");

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		new RestTemplate().postForEntity(URL, request , String.class);
	}
	
	public void cancelOrder() {
		if(Global.SIMULATION) return;
		
		String URL = Global.URL_SCRAP_API+"/cancel-order";
		
		HttpHeaders headers = new HttpHeaders();
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		new RestTemplate().postForEntity(URL, request , String.class);
	}

}

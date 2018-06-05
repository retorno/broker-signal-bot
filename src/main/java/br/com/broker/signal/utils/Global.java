package br.com.broker.signal.utils;

public class Global {

	public static Boolean SIMULATION = false; //0 = False; 1 = True
	
	//Broker Signal Scrap API
	public static String URL_SCRAP_API = "http://127.0.0.1:5000/broker";
	
	public static int INICIAL_CONTRACTS = 1;
	public static String ORDER_TYPE = "Buy";
	public static Long WIN_VARIANCE = 40L;
	public static Long LOSE_VARIANCE = 40L;
	
}

package br.com.broker.signal.utils;

public class Global {

	//Broker Signal Scrap API
	public static String URL_SCRAP_API = "http://127.0.0.1:5000/broker";
	
	//Main Logic
	public static Boolean SIMULATION = true;
	public static int INICIAL_CONTRACTS = 1;
	public static String ORDER_TYPE = "Sell";
	public static int STOP_VARIANCE = 50;
	public static int DOUBLE_VARIANCE = 100;
	
	//Change Strategy
	public static int QT_STOP_TOW8 = 2;
	public static int MILLISEC_TOW8 = 180000; //3 minutos
	public static int QT_STOP_TOINVERT = 4;
	
}

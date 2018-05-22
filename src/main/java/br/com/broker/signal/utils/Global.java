package br.com.broker.signal.utils;

public class Global {

	public static Boolean SIMULATION = true;
	
	//Broker Signal Scrap API
	public static String URL_SCRAP_API = "http://127.0.0.1:5000/broker";
	
	//Logic One
	public static int L1_INICIAL_CONTRACTS = 1;
	public static String L1_ORDER_TYPE = "Sell";	// Sell / Buy
	public static int L1_STOP_VARIANCE = 50;
	public static int L1_DOUBLE_VARIANCE = 100;
	public static int L1_QT_STOP_TOW8 = 2;
	public static int L1_MILLISEC_TOW8 = 180000; //3 minutos
	public static int L1_QT_STOP_TOINVERT = 4;
	
	//Logic Two
	public static int L2_INICIAL_CONTRACTS = 2;
	public static String L2_ORDER_TYPE = "Sell";	// Sell / Buy
	public static int L2_WIN_VARIANCE = 60;
	public static int L2_LOSE_VARIANCE = 30;
	
}

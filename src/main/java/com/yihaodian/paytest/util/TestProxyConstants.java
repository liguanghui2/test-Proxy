package com.yihaodian.paytest.util;

public class TestProxyConstants {

	//是否是生产环境
	public static boolean IS_PRODUCTION = true;
	
	
	public static void setEvn(String evn){
		if(evn!=null && (evn.startsWith("test") || evn.startsWith("dev"))){
			IS_PRODUCTION = false;
		}
	}
}

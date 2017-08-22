package com.yihaodian.paytest.util;

import java.util.HashMap;
import java.util.Map;

import com.yihaodian.paytest.model.MethodInfo;

public class ContantUtil {
	
	private static Map<String, MethodInfo> methodMap = new HashMap<String, MethodInfo>();

	public static void addMethodInfo(MethodInfo method){
		methodMap.put(method.getId(), method);
	}
	
	public static MethodInfo getMethodInfo(String id){
		return methodMap.get(id);
	}
	
	public static void cleanCache(){
		methodMap.clear();
	}
}

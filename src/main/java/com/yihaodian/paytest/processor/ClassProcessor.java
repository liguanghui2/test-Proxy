package com.yihaodian.paytest.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yihaodian.paytest.model.MethodInfo;
import com.yihaodian.paytest.util.ClassUtil;
import com.yihaodian.paytest.util.ContantUtil;
import com.yihaodian.paytest.util.TestProxyConstants;

public class ClassProcessor {

	public static Object callMethod(String methodId, String inputJson) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

		MethodInfo method = ContantUtil.getMethodInfo(methodId);

/*		if(TestProxyConstants.IS_PRODUCTION && !ClassUtil.checkMethodCanCall(method.getLongName ())){
            return "错误：" + method.getLongName ()+"是写方法，限制访问！"; 
        }*/
		
		Method met = method.getMethod();

		Type[] ts = met.getGenericParameterTypes();

		if ( ts.length == 1 ){
			inputJson = inputJson.trim();
			String json = inputJson;
			if( inputJson.startsWith("[")){
				json = inputJson.substring(1); 
			}
			if( json.endsWith("]")){
				json = json.substring(0, json.length()-1);
			}
			
			Object obj = JSON.parseObject(json, ts[0]);			
			
			return met.invoke(method.getBean(), obj);
		}
		else if (ts.length > 1 ) {

			List<String> list = JSON.parseObject(inputJson,
					new TypeReference<List<String>>() {
					});
			Object[] param = new Object[ts.length];

			for (int i = 0; i < ts.length; ++i) {
				if (ts[i] == String.class) {
					param[i] = list.get(i);
				} 
				//日期另外处理
				else if( ts[i] == Date.class) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					param[i] = dateFormat.parse(list.get(i));
				}
				else {					
					param[i] = JSON.parseObject(list.get(i), ts[i]);
				}
			}
			return met.invoke(method.getBean(), param);
			
		} else {
			
			return met.invoke(method.getBean());
			
		}

	}

	
	public static void main(String[] args) {
	}
}

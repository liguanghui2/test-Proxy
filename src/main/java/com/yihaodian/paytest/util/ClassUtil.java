package com.yihaodian.paytest.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


import com.yihaodian.paytest.model.MethodInfo;

public class ClassUtil {
	

	private static  Logger log = Logger.getLogger(ClassUtil.class);
	/**
	 * 找出包下所有的类
	 * @param packageName
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getPackageClasses(String packageName) throws IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		File directory = null;
		if (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			directory = new File(resource.getFile());
		}
		if(directory != null){
			return findClasses(directory, packageName);
		}
		return null;
		
	}
	
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName){
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".class")) {
				try{
					Class tempObj = Class.forName(packageName
							+ '.'+ file.getName().substring(0,
									file.getName().length() - 6));
					
					classes.add(tempObj);
				}
				catch(Exception e){
					log.error(e);
				}
			}
		}
		return classes;
	}
	
	/**
	 * 求一个类的所有的方法
	 * @param pack  包名，如 ：com.yihaodian.front.busystock
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getMethodsFromClass( Class clazz ) {
		
		Method[] ms = clazz.getMethods();
		List<String> list = new ArrayList<String>();
		for(Method m:ms){
			StringBuffer fullName = new StringBuffer(200);
			fullName.append(m.getReturnType().getSimpleName()).append(" ").append(m.getName());
			Class[] ts = m.getParameterTypes();
			fullName.append("(");
			String temp = "";
			for(Type t:ts){
				fullName.append(temp).append(t.toString());
				temp = ",";
			}
			fullName.append(")");
			list.add(fullName.toString());
		}
		return list;
	}
	
	/**
	 * 求一个类的所有的接口方法
	 * @param pack  包名，如 ：com.yihaodian.front.busystock
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public static List<MethodInfo> getMethodInfoFromBean( Object obj ) {

		Class clazz = obj.getClass().getInterfaces()[0];
		
		Method[] ms = clazz.getMethods();
		List<MethodInfo> list = new ArrayList<MethodInfo>();
		
		for(Method m:ms){
			
			list.add(getInfoFromMethod(clazz, obj, m));
			
		}
        Collections.sort(list, new Comparator<MethodInfo>() {

            @Override
            public int compare(MethodInfo arg0, MethodInfo arg1) {
                String str0 = arg0.getLongName();
                String str1 = arg1.getLongName();
                return str0.compareTo(str1);
            }
        });
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private static MethodInfo getInfoFromMethod(Class clazz, Object obj, Method method){
		MethodInfo met = new MethodInfo();
		met.setMethod(method);
		met.setBean(obj);
		//解析出fullName, longName, Id
		StringBuffer fullName = new StringBuffer(200);
		StringBuffer id = new StringBuffer(200);
		StringBuffer longName = new StringBuffer(200);
		
		fullName.append(method.getReturnType().getSimpleName()).append(" ").append(method.getName()).append("(");
		longName.append( method.getName() ).append("(");
		id.append( clazz.getSimpleName() ).append( "_" ).append( method.getName() );
		
		Class[] ts = method.getParameterTypes();
		String temp = "";
		String tempStr = ".";
		for(Type t:ts){
			//fullName
			fullName.append(temp).append(t.toString());
			
			//longName
			String s = t.toString();
			int a = s.lastIndexOf(tempStr);
			if(a>-1){
				s = s.substring(a+1);
			}
			longName.append(temp).append(s);
			
			//id
			id.append("_").append(s);
			
			temp = ",";
		}
		
		fullName.append(")");
		longName.append(")");
		met.setId(id.toString());
		met.setFullName(fullName.toString());
		met.setLongName(longName.toString());
		return met;
	}
	
	/**
	 * 求出类中的方法
	 * @param packageName
	 * @return
	 * @throws IOException 
	 */
	public static String getMethodsFromClass(String beanName) {		
		
		Object obj = SpringBeanUtil.getBean(beanName);
		
		if(null == obj ){
			return null;
		}
				
		List<MethodInfo> methods = ClassUtil.getMethodInfoFromBean(obj);
		StringBuffer str = new StringBuffer(800);
		str.append("[");
		
		for(MethodInfo met:methods){
				str.append("{id:\"").append(met.getId())				
				.append("\",longName:\"").append(met.getLongName())
				.append("\"},");
				
				//缓存起来，等一下调用的时候用
				met.setBeanName(beanName);
				ContantUtil.addMethodInfo(met);
			
		}
		//去掉最后一个逗号
		if(methods.size()>0){
			str.deleteCharAt(str.length()-1);
		}
		str.append("]");
		
		return str.toString();
	}
	
	public static boolean checkMethodCanCall(String methodName){
		//Set<String> limitMethods = configDao.getLimitMethods();
		//PSS 写死哪些方法不能调用
		Set<String> limitMethods = new HashSet<String>();
		limitMethods.add("write");
		limitMethods.add("recovery");
		limitMethods.add("shelve");
		limitMethods.add("change");
		limitMethods.add("delete");
		limitMethods.add("update");
		limitMethods.add("save");
		limitMethods.add("set");
		limitMethods.add("insert");
		limitMethods.add ("create");
		for (String mn:limitMethods) {
			if(methodName.toLowerCase ().startsWith(mn)){
				return false;
			}
		}
		
		return true;
	}
	public static void main(String[] args){
		
	}
}

package com.yihaodian.paytest.model;

import java.lang.reflect.Method;

public class MethodInfo {
	
	private String id;
	
	private String name;
	
	private String longName;
	
	private String fullName;

	private Method method;
	
	private Object bean;
	
	private String beanName;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Object getBean() {
		return bean;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getBeanName() {
		return beanName;
	}

	
}

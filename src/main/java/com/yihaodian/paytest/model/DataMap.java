package com.yihaodian.paytest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataMap(){}
	
	private List<HessianConfigInfoDTO> beans;
	private Set<String> limitMethod;
	
	public List<HessianConfigInfoDTO> getBeans() {
		if( beans == null ){
			beans = new ArrayList<HessianConfigInfoDTO>();
		}
		return beans;
	}
	public void setBeans(List<HessianConfigInfoDTO> beans) {
		
		this.beans = beans;
	}
	public Set<String> getLimitMethod() {
		if( limitMethod == null){
			limitMethod = new HashSet<String>();
		}
		return limitMethod;
	}
	public void setLimitMethod(Set<String> limitMethod) {
		this.limitMethod = limitMethod;
	}		
}
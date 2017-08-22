package com.yihaodian.paytest.model;

import java.io.Serializable;

public class HessianConfigInfoDTO implements Serializable,Comparable<HessianConfigInfoDTO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3059241991719325390L;

	private String serviceId;
	private String pool;
	private String beanName;
	private Long id;
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public void setPool(String pool) {
		this.pool = pool;
	}
	public String getPool() {
		return pool;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	
    @Override
    public int compareTo(HessianConfigInfoDTO o) {
        return serviceId.compareTo(o.getServiceId());
    }
	@Override
	public String toString() {
		return "HessianConfigInfoDTO [serviceId=" + serviceId + ", pool=" + pool + ", beanName=" + beanName + ", id="
				+ id + "]";
	}
	
}

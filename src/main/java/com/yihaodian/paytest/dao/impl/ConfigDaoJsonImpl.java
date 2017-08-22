package com.yihaodian.paytest.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import com.alibaba.fastjson.JSON;
import com.yihaodian.paytest.dao.ConfigDao;
import com.yihaodian.paytest.model.DataMap;
import com.yihaodian.paytest.model.HessianConfigInfoDTO;

/**
 * 用json要保存数据，不再依赖数据库
 * @author wangxiaowu
 *
 */
public class ConfigDaoJsonImpl implements ConfigDao{

	private Logger logger = Logger.getLogger(this.getClass());
	
	private final String jsonFilePath = System.getProperty("global.config.path")+"/test-proxy-beans.json";
	private DataMap dataMap;
	
	private Long maxId = null;
	
	@Override
	public List<HessianConfigInfoDTO> getAllHessianConfig() {
		if( dataMap == null ){
			synchronized (jsonFilePath) {				
					dataMap = getAllDtoFromJsonFile();								
			}
		}
		return dataMap.getBeans();
	}

	@Override
	public List<HessianConfigInfoDTO> findHessianConfig(HessianConfigInfoDTO dto) {
		List<HessianConfigInfoDTO> result = new ArrayList<HessianConfigInfoDTO>();
		if( dto == null){
			return getAllHessianConfig();
		}
		
		for(HessianConfigInfoDTO obj:getAllHessianConfig()){
			if(obj == null ){
				continue;
			}
			
			if(!StringUtils.isEmpty(dto.getPool()) && !obj.getPool().equalsIgnoreCase(dto.getPool())){
				continue;
			}
			
			if(!StringUtils.isEmpty(dto.getServiceId()) && !obj.getServiceId().startsWith(dto.getServiceId())){
				continue;
			}
			
			if(!StringUtils.isEmpty(dto.getBeanName()) && !obj.getBeanName().startsWith(dto.getBeanName())){
				continue;
			}
			
			result.add(obj);
		}
		return result;
	}

	@Override
	public HessianConfigInfoDTO insertHessianConfigInfoDTO(
			HessianConfigInfoDTO dto) {
		dto.setId(getId());
		getAllHessianConfig().add(dto);
		saveJsonToFile();
		return dto;
	}

	@Override
	public int deleteHessianConfigInfoDTO(Long id) {
		List<HessianConfigInfoDTO> allBeans = getAllHessianConfig();
		for( HessianConfigInfoDTO dto:allBeans){
			if( dto.getId().longValue() == id.longValue() ){
				allBeans.remove(dto);
				saveJsonToFile();
				return 1;
			}
		}
		return 0;
	}
	
	@Override
	public Set<String> getLimitMethods()
	{		
		return getAllDtoFromJsonFile().getLimitMethod();
	}
	
	private DataMap getAllDtoFromJsonFile(){
		try {
			File file = new File(jsonFilePath);
			if( file!=null && !file.exists() ){
				file.createNewFile();
				FileUtils.writeStringToFile(file, "{}");	
				return new DataMap();
			}
			
			String json = FileUtils.readFileToString(file);
			
			return JSON.parseObject(json, DataMap.class);
			
		} catch (Exception e) {
			logger.error(e);
		}
		return new DataMap();
	}

	private void saveJsonToFile(){
		try {
			FileUtils.writeStringToFile(new File(jsonFilePath), JSON.toJSONString(dataMap, true));
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	private Long getId(){
		if(maxId == null ){
			synchronized (jsonFilePath) {
					maxId = 0L;
					for(HessianConfigInfoDTO dto:getAllHessianConfig()){
						if( maxId.longValue()<dto.getId().longValue()){
							maxId=dto.getId();
						}
					}				
			}
		}
		return ++maxId;
	}
		
}

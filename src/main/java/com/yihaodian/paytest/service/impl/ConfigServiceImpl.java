package com.yihaodian.paytest.service.impl;

import java.util.List;

import com.yihaodian.paytest.dao.ConfigDao;
import com.yihaodian.paytest.model.HessianConfigInfoDTO;
import com.yihaodian.paytest.service.ConfigService;

public class ConfigServiceImpl implements ConfigService{

	private ConfigDao configDao;

	public void setConfigDao(ConfigDao configDao) {
		this.configDao = configDao;
	}
	
	@Override
	public List<HessianConfigInfoDTO> getAllHessianConfig(){
		return configDao.getAllHessianConfig();
	}

	@Override
	public List<HessianConfigInfoDTO> findHessianConfig(HessianConfigInfoDTO dto) {
		return configDao.findHessianConfig(dto);
	}

	@Override
	public HessianConfigInfoDTO insertHessianConfigInfoDTO(
			HessianConfigInfoDTO dto) {
		
		HessianConfigInfoDTO dto1 = new HessianConfigInfoDTO();
		dto1.setBeanName(dto.getBeanName());
		dto1.setPool(dto.getPool());
		dto1.setServiceId(dto.getServiceId());
		return configDao.insertHessianConfigInfoDTO(dto1);
	}

	@Override
	public int deleteHessianConfigInfoDTO(Long id) {
		return configDao.deleteHessianConfigInfoDTO(id);
	}
	
	
}

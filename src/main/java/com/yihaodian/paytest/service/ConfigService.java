package com.yihaodian.paytest.service;

import java.util.List;

import com.yihaodian.paytest.model.HessianConfigInfoDTO;

public interface ConfigService {

	 /**获得所有hessian配置
     * @return
     */
    public List<HessianConfigInfoDTO> getAllHessianConfig();
    
    /**
     * 根据pool求Bean名
     * @param pool
     * @return
     */
    public List<HessianConfigInfoDTO> findHessianConfig(HessianConfigInfoDTO dto);
    
    /**
     * 新增一个Bean
     * @param dto
     * @return
     */
    public HessianConfigInfoDTO insertHessianConfigInfoDTO(HessianConfigInfoDTO dto);
    
    /**
     * 删除一个Bean
     * @param id
     * @return
     */
    public int deleteHessianConfigInfoDTO(Long id);

	
}

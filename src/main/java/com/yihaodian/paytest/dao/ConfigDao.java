package com.yihaodian.paytest.dao;

import java.util.List;
import java.util.Set;

import com.yihaodian.paytest.model.HessianConfigInfoDTO;

public interface ConfigDao {

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

    /**
     * 返回所有限制访问的方法名
     * @return
     */
	public Set<String> getLimitMethods();
}

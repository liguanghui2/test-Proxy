package com.yihaodian.paytest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yihaodian.architecture.hedwig.client.HedwigClientFactoryBean;
import com.yihaodian.paytest.model.HessianConfigInfoDTO;

/**
 * 客户端配置插件
 *
 * @author Hikin Yao
 * @version 1.0
 */
public class SpringBeanUtil {
	
	private static Logger logger = Logger.getLogger(SpringBeanUtil.class);
   
    private static ApplicationContext clientContext = null;
    
    private static Object lock = new Object();

    public static Object getBean(String beanName){
    	
    	if( null == clientContext ) {
    		
    		synchronized (lock) {
    			
    			if( null == clientContext ) {
	    			//初始化clientContext
	        		clientContext = new ClassPathXmlApplicationContext(getConfigLocations());
    			}
			}    		
    		
    	}
    	
    	if( clientContext!= null ){
    		
    		try
    		{
				return clientContext.getBean(beanName);
    		
    		}
    		catch(Exception e){ 
    			logger.error(e);
    		}
    		
    	}
    	
    	return null;
    }
    
    public static void reloadBean(){
    	clientContext = new ClassPathXmlApplicationContext(getConfigLocations());
    }
    
    static {
        String globalPath = System.getProperty("global.config.path");
        System.setProperty("gss_invoke_timeout", "5000");
        if (StringUtils.isBlank(globalPath)) {
            globalPath = System.getenv("global.config.path");
            if (StringUtils.isBlank(globalPath)) {
                System.setProperty("global.config.path", "/var/www/webapps/config/");
            }
        }
    }

    private static String[] getConfigLocations() {
        return new String[]{"classpath*:com/**/*-deploy.xml","classpath*:spring/*-deploy.xml"};
    }

	public static List<HessianConfigInfoDTO> getAllHessianConfigAuto() {
		List<HessianConfigInfoDTO> result = new ArrayList<HessianConfigInfoDTO>();
    	if( null == clientContext ) {
    		synchronized (lock) {
    			if( null == clientContext ) {
	    			//初始化clientContext
	        		clientContext = new ClassPathXmlApplicationContext(getConfigLocations());
    			}
			}    		
    	}
    	
    	//Key:beanName,value:beanValue
		Map nameBeanMap = clientContext.getBeansOfType(HedwigClientFactoryBean.class);
		//System.out.println("result size is "+nameBeanMap.size());
		long id=1;
		for (Object key : nameBeanMap.keySet()) {
			HessianConfigInfoDTO dto = new HessianConfigInfoDTO();
			String sKey = (String)key;
			result.add(dto);
			dto.setId(id);
			dto.setBeanName(sKey.substring(1));
			dto.setPool("PSS");
			HedwigClientFactoryBean bean = (HedwigClientFactoryBean) nameBeanMap.get(key);
			String interfaceName = bean.getServiceInterface().getSimpleName();
			dto.setServiceId(interfaceName);
			id++;
		}
		return result;
	}    
    
    public static void main(String[] args){
    	String s = "com.yhd.pss.spi.baseinfo.service.ProductBaseInfoService";
    	String c = s.substring(s.lastIndexOf(".")+1);
    	System.out.println(c);
    	System.out.println(c.substring(1));
    }
 
}

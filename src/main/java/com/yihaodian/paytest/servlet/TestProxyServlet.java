package com.yihaodian.paytest.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer;
import com.yihaodian.paytest.model.HessianConfigInfoDTO;
import com.yihaodian.paytest.model.MethodInfo;
import com.yihaodian.paytest.processor.ClassProcessor;
import com.yihaodian.paytest.service.ConfigService;
import com.yihaodian.paytest.util.ClassUtil;
import com.yihaodian.paytest.util.ContantUtil;
import com.yihaodian.paytest.util.SpringBeanUtil;
import com.yihaodian.paytest.util.TestProxyConstants;

/**
 * Servlet implementation class MainServlet
 */
public class TestProxyServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private ApplicationContext context;
    private ConfigService configService;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestProxyServlet ()
    {
        super ();
    }

    public void init (ServletConfig config) throws ServletException
    {
        context = WebApplicationContextUtils.getWebApplicationContext (config.getServletContext ());
        configService = (ConfigService) context.getBean ("configService");
    }

    private void deleteDto (String strId)
    {
        Long id = Long.parseLong (strId);
        configService.deleteHessianConfigInfoDTO (id);
    }

    private void insertDto (HessianConfigInfoDTO dto)
    {
        if (dto.getBeanName () != null)
        {
            dto.setBeanName (dto.getBeanName ().trim ());
        }
        if (dto.getPool () != null)
        {
            dto.setPool (dto.getPool ().trim ());
        }
        if (dto.getServiceId () != null)
        {
            dto.setServiceId (dto.getServiceId ().trim ());
        }
        configService.insertHessianConfigInfoDTO (dto);
        dto.setBeanName (null);
        dto.setServiceId (null);
    }

    public List <HessianConfigInfoDTO> getDtoByPool (HessianConfigInfoDTO dto)
    {
        return configService.findHessianConfig (dto);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                   IOException
    {
        String act = request.getParameter ("act");
        String methodId = request.getParameter ("methodId");
        String beanName = request.getParameter ("beanName");
        PrintWriter pw = null;

        try
        {
            // 调用方法  返回text/html
            if ("callMethod".equals (act))
            {
                response.setContentType ("text/html;charset=utf-8");
                pw = response.getWriter ();
                String method = request.getParameter ("method");
                String templateContext = request.getParameter ("templateContext");
                TestProxyConstants.setEvn (YccGlobalPropertyConfigurer.getEnv ());
                try
                {
                    Object result = ClassProcessor.callMethod (method, handleInputString (templateContext));
                    pw.print (toResultJsonString (result));
                }
                catch (Exception e)
                {
                    e.printStackTrace (pw);
                }  
                return;
            }

            // text/javascript
            response.reset ();
            response.setContentType ("text/javascript;charset=utf-8");
            pw = response.getWriter ();
            // 求出接口的方法
            if ("getMethod".equals (act))
            {
                if (beanName == null || "".equals (beanName.trim ()))
                {
                    return;
                }
                
                pw.print (ClassUtil.getMethodsFromClass (beanName));
                return;
            }

            // 方法参数模板生成
            else if ("getParameter".equals (act))
            {
                String json = getMethodsParameterJson (methodId);
                pw.write (json);
                return;
            }

            String strId = request.getParameter ("id");
            HessianConfigInfoDTO dto = new HessianConfigInfoDTO ();
            dto.setBeanName (request.getParameter ("dto.beanName"));
            dto.setPool (request.getParameter ("dto.pool"));
            dto.setServiceId (request.getParameter ("dto.serviceId"));
            dto.setId (strId != null && !"".equals (strId.trim ()) ? Long.parseLong (strId) : null);
            
            if ("list".equalsIgnoreCase (act))
            {
                request.setAttribute ("hessianConfigInfoList", getDtoByPool (dto));
                request.getRequestDispatcher ("pages/managerBeans.jsp").forward (request, response);
                return;
            }
            else if ("delete".equalsIgnoreCase (act))
            {
                deleteDto (strId);
            }
            else if ("insert".equalsIgnoreCase (act))
            {
                insertDto (dto);
            }
            else if ("environment".equalsIgnoreCase (act))
            {
                TestProxyConstants.setEvn (YccGlobalPropertyConfigurer.getEnv ());
                String str = YccGlobalPropertyConfigurer.getEnv () + ":" + TestProxyConstants.IS_PRODUCTION;
                pw.print (str);
                return;
            }
            // 初始页面
            else if ("init".equals (act))
            {
                List <HessianConfigInfoDTO> hessianConfigInfoList = configService.getAllHessianConfig ();
                Collections.sort(hessianConfigInfoList);
                // 预先加载
                for (HessianConfigInfoDTO dd : hessianConfigInfoList)
                {
                    SpringBeanUtil.getBean (dd.getBeanName ());
                    break;
                }
                request.setAttribute ("hessianConfigInfoList", hessianConfigInfoList);
                request.getRequestDispatcher ("pages/hessianInvoke.jsp").forward (request, response);
                return;
            }else if ("auto".equals(act)){
                List <HessianConfigInfoDTO> hessianConfigInfoList = SpringBeanUtil.getAllHessianConfigAuto ();
                // 预先加载
                for (HessianConfigInfoDTO dd : hessianConfigInfoList)
                {
                    SpringBeanUtil.getBean (dd.getBeanName ());
                    break;
                }
                Collections.sort(hessianConfigInfoList);
                request.setAttribute ("hessianConfigInfoList", hessianConfigInfoList);
                request.getRequestDispatcher ("pages/hessianInvoke.jsp").forward (request, response);
                return;
            }
        }
        catch (Exception e)  {
            System.out.println(e);
        }
        finally
        {
            if (pw != null)
            {
                pw.close ();
            }
        }
        pw.print ("OK");
        return;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                    IOException
    {
        doGet (request, response);
    }

    /**
     * 
     * @param packageName
     * @return
     * @throws IOException
     */
    private String getMethodsParameterJson (String methodId)
    {
        if (StringUtils.isBlank (methodId))
        {
            return "";
        }
        MethodInfo methodInfo = ContantUtil.getMethodInfo (methodId);
        if (methodInfo == null)
        {
            return "";
        }
        Method method = methodInfo.getMethod ();
        Type[] ts = method.getGenericParameterTypes ();
        List <Object> list = new ArrayList <Object> ();
        for (Type t : ts)
        {
            list.add (createInstance (t));
        }

        return toJsonString (list);

    }

    /**
     * 根据类型创建实例
     * 
     * @param type
     * @return
     */
    @SuppressWarnings (
    { "unchecked", "rawtypes" })
    private Object createInstance (Type type)
    {
        Object obj = createSimpleObject (type);
        if (type instanceof ParameterizedType)
        {

            Type[] ts = ((ParameterizedType) type).getActualTypeArguments ();
            if (ts.length == 1)
            {
                Type trueType = ts[0];
                ((List) obj).add (new TestProxyServlet ().createInstance (trueType));
            }
            else if (ts.length == 2)
            {
                Type keyType = ts[0];
                Type valType = ts[1];
                Object key = new TestProxyServlet ().createInstance (keyType);
                Object val = new TestProxyServlet ().createInstance (valType);
                ((Map) obj).put (key, val);
            }
        }

        return obj;
    }

    private Object createSimpleObject (Type clazz)
    {
        Object o = null;
        // 这里对性能要求不高，只要能生成一个对象即可
        try
        {
            o = JSON.parseObject ("0", clazz);
            if (o instanceof String)
            {
                return "";
            }
        }
        catch (Exception e)
        {
            try
            {
                o = JSON.parseObject ("[]", clazz);
            }
            catch (Exception ee)
            {

                try
                {
                    o = JSON.parseObject ("{}", clazz);
                }
                catch (Exception e1)
                {
                }
            }
        }
        return o;
    }

    private String toJsonString (Object obj)
    {
        PropertyFilter filter = new PropertyFilter ()
        {

            @Override
            public boolean apply (Object source, String name, Object value)
            {
                if ("clientVersion".equals (name))
                {
                    return false;
                }
                if ("reqTime".equals (name))
                {
                    return false;
                }
                if ("uuid".equals (name))
                {
                    return false;
                }
                if ("endRow".equals (name))
                {
                    return false;
                }
                if ("startRow".equals (name))
                {
                    return false;
                }
                return true;
            }

        };
        SerializeWriter out = new SerializeWriter ();
        JSONSerializer serializer = new JSONSerializer (out);
        serializer.config (SerializerFeature.QuoteFieldNames, false);
        serializer.setDateFormat ("yyyy-MM-dd HH:mm:ss");
        serializer.config (SerializerFeature.WriteDateUseDateFormat, true);
        serializer.config (SerializerFeature.PrettyFormat, true);
        serializer.config (SerializerFeature.WriteMapNullValue, true);
        serializer.config (SerializerFeature.WriteNullNumberAsZero, false);
        serializer.config (SerializerFeature.WriteNullStringAsEmpty, false);
        serializer.config (SerializerFeature.WriteNullListAsEmpty, false);
        serializer.getPropertyFilters ().add (filter);
        serializer.write (obj);

        String jsonString = out.toString ();
        out.close ();
        return jsonString;
    }

    private String toResultJsonString (Object obj)
    {
        if (obj instanceof String)
        {
            return (String) obj;
        }
        SerializeWriter out = new SerializeWriter ();
        JSONSerializer serializer = new JSONSerializer (out);
        serializer.config (SerializerFeature.QuoteFieldNames, false);
        serializer.setDateFormat ("yyyy-MM-dd HH:mm:ss");
        serializer.config (SerializerFeature.WriteDateUseDateFormat, true);
        serializer.config (SerializerFeature.PrettyFormat, true);
        serializer.config (SerializerFeature.WriteMapNullValue, true);
        serializer.write (obj);

        String jsonString = out.toString ();
        out.close ();
        return jsonString;
    }

    private String handleInputString (String method)
    {
        if (method == null || "".endsWith (method.trim ()))
        {
            return "[]";
        }
        method = method.trim ();
        if (method.startsWith ("("))
        {
            method = "[" + method.substring (1);
        }
        if (method.endsWith (")"))
        {
            method = method.substring (0, method.length () - 1) + "]";
        }

        if (!method.startsWith ("["))
        {
            method = "[" + method;
        }
        if (!method.endsWith ("]"))
        {
            method = method + "]";
        }
        return method;
    }

}

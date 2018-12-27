package cn.core.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.util.StringHelper;


public class StaticHtmlInterceptor implements HandlerInterceptor {

	private String viewName;
	private final static double TIME_OUT=10*60*1000;
	private final static String HTML_FILE="/html";
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		StaticHtml staticHtml=getStaticHtml(request,response,handler);
		if(staticHtml==null){
			return true;
		}
		
		String userDir=System.getProperty("user.dir");
		String requestPath = request.getServletPath();
		if(requestPath.equals("/")){
			return true;
		}
		String htmlFileName=getHtmlFileName(request,staticHtml);
		String htmlPath=userDir+HTML_FILE+htmlFileName;
		  
		File file=new File(htmlPath);
		if(file.exists()){
			
			if(checkTimeOut(file)){
				return true;
			}
			
			//response.sendRedirect(HTML_FILE+htmlFileName);
			
			PrintWriter writer=response.getWriter();
			writer.flush();
			
			BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
			try {
				String line=null;
				while ((line=bufferedReader.readLine())!=null) {
					writer.print(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				bufferedReader.close();
			}
			writer.close();
			return false;
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(modelAndView!=null){
			viewName=modelAndView.getViewName();
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		if(ex!=null){
			return;
		}
		if(StringHelper.IsNullOrEmpty(viewName)){
			return;
		}
		StaticHtml staticHtml=getStaticHtml(request,response,handler);
		if(staticHtml==null){
			return;
		}
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");

		ServletContext servletContext=request.getServletContext();
		
		String userDir=System.getProperty("user.dir");
		String requestPath = request.getServletPath();
		if(requestPath.equals("/")){
			return;
		}
		String htmlFileName=getHtmlFileName(request,staticHtml);
		String htmlPath=userDir+HTML_FILE+htmlFileName;
		
		File file=new File(htmlPath);
		if(file.exists()){
			if(!checkTimeOut(file)){
				return;
			}
		}
		File dirFile=new File(FilenameUtils.getFullPath(htmlPath));
		if(!dirFile.isDirectory()){
			dirFile.mkdirs();
		}
		file.createNewFile();
		
		String jspPath="/WEB-INF/jsp"+viewName+".jsp";
		
		RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher(jspPath);
		
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();//用于从ServletOutputStream中接收资源     
        final ServletOutputStream servletOuputStream = new ServletOutputStream(){//用于从HttpServletResponse中接收资源     
            public void write(byte[] b, int off,int len){  
                byteArrayOutputStream.write(b, off, len);    
            }    
            public void write(int b){  
                byteArrayOutputStream.write(b);  
            }
			@Override
			public boolean isReady() {
				return false;
			}
			@Override
			public void setWriteListener(WriteListener listener) {
			}    
        };    
        final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream));//把转换字节流转换成字符流     
        HttpServletResponse httpServletResponse = new HttpServletResponseWrapper(response){     
            public ServletOutputStream getOutputStream(){  
                return servletOuputStream;    
            }  
            public PrintWriter getWriter(){  
                return printWriter;    
            }  
        };    
        requestDispatcher.include(request, httpServletResponse);  
        printWriter.flush();
        FileOutputStream fileOutputStream = new FileOutputStream(htmlPath);    
        byteArrayOutputStream.writeTo(fileOutputStream);
        fileOutputStream.close();
	}
	
	private StaticHtml getStaticHtml(HttpServletRequest request,HttpServletResponse response,Object handler){
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			HandlerMethod hm=(HandlerMethod)handler;
			Class<?> clazz=hm.getBeanType();
	        Method m=hm.getMethod();
            if (clazz!=null && m != null ) {
                boolean isClzAnnotation= clazz.isAnnotationPresent(StaticHtml.class);
                boolean isMethondAnnotation=m.isAnnotationPresent(StaticHtml.class);
                StaticHtml staticHtml=null;
                if(isMethondAnnotation){
                	staticHtml=m.getAnnotation(StaticHtml.class);
                }else if(isClzAnnotation){
                	staticHtml=clazz.getAnnotation(StaticHtml.class);
                }
                return staticHtml;
            }
		}
		return null;
	}
	
	private String getHtmlFileName(HttpServletRequest request,StaticHtml staticHtml){
		String requestPath = request.getServletPath();
		String[] keys=staticHtml.keys();
		List<String> values=new ArrayList<String>();
		values.add(requestPath);
		if(keys!=null&&keys.length>0){
			for (String key : keys) {
				String value=request.getParameter(key);
				if(StringHelper.IsNullOrEmpty(value)){
					value="0";
				}
				values.add(value);
			}
		}
		if(!values.isEmpty()){
			requestPath=StringHelper.Join("-", values.toArray());
		}
		return requestPath+".html";
	}
	
	private boolean checkTimeOut(File file)
	{
		if(new Date().getTime()-file.lastModified()>TIME_OUT||file.length()==0){
			file.delete();
			return true;
		}
		return false;
	}
}

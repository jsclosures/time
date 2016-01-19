package com.jsclosures;

import com.jsclosures.services.RestImplService;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RestServiceServlet 
	extends HttpServlet implements RestService
{
	public static final String CONTENT_TYPE = "text/json";

	private Configuration configuration;
        public static RestImplService loadServiceClass(String className)
                    throws Exception {
            return (RestImplService)Class.forName(className).newInstance();
        }
        
	public void init(ServletConfig config)
		throws ServletException
	{
		super.init(config);

		configuration = new Configuration();
		
		String dataSourceName = config.getInitParameter("datasourcename");
		
		
		if( dataSourceName != null && dataSourceName.length() > 0 )
			configuration.setValue("datasourcename",dataSourceName);
		
		
		String solr = config.getInitParameter("solr");
		
		
		if( solr != null && solr.length() > 0 )
			configuration.setValue("solr",solr);
		
		
		String host = config.getInitParameter("host");
		
		
		if( host != null && host.length() > 0 )
			configuration.setValue("host",host);
		else
			configuration.setValue("host","http://localhost");
		
		
		String messageserver = config.getInitParameter("messageserver");
		
		
		if( messageserver != null && messageserver.length() > 0 )
			configuration.setValue("messageserver",messageserver);
                
                //includemessages
                String includemessages = config.getInitParameter("includemessages");
                
                if( includemessages != null && includemessages.length() > 0 )
                    configuration.setValue("includemessages",includemessages);
                else
                    configuration.setValue("includemessages",true);
                
                String webdriver = config.getInitParameter("webdriver");
                
                if( webdriver == null || webdriver.length() == 0 )
                    webdriver = "http://localhost:4444/wd/hub";
                
                configuration.setValue("webdriver",webdriver);
                
                
	    String grammarfile = config.getInitParameter("grammarfile");
	    
	    if( grammarfile == null || grammarfile.length() == 0 )
	        grammarfile = getServletContext().getRealPath("/WEB-INF/models/en-parser-chunking.bin");
	    
	    configuration.setValue("grammarfile",grammarfile);
            
	    String sentencefile = config.getInitParameter("sentencefile");
	    
	    if( sentencefile == null || sentencefile.length() == 0 )
	        sentencefile = getServletContext().getRealPath("/WEB-INF/models/en-sent.bin");
	    
	    configuration.setValue("sentencefile",sentencefile);
            
	    String mappingfile = config.getInitParameter("mappingfile");
	    
	    if( mappingfile == null || mappingfile.length() == 0 )
	        mappingfile = getServletContext().getRealPath("/WEB-INF/mappings.properties");
	    
	    configuration.setValue("mappingfile",mappingfile);
            
	    Configuration mapping = new Configuration(mappingfile);
            String r = mapping.open();
            writeLog(1,"mapping: " + r + " " + mapping.toString());
            configuration.setStructure("mapping",mapping);
	    
	    
	}
	
	public void setConfiguration(Configuration conf) {
	    configuration = conf;
	}
	
	public Configuration getConfiguration() {
		return( configuration );
	}
	
	public String getConfiguration(String which) {
		return( configuration.getString(which) );
	}
	
	private ArrayList<DataBean> dataList;

	public ArrayList<DataBean> getDataList()
	{
		return (dataList);
	}

	public DataBean getDataList(String mode,HttpServletRequest req,HttpServletResponse resp)
	{

		if (dataList == null)
		{
			dataList = new ArrayList<DataBean>();

			DataBean tmp;

			for (int i = 0, size = 10; i < size; i++)
			{
				tmp = new DataBean();
				tmp.setValue("id", i);
				tmp.setValue("uid", i);
				tmp.setValue("title", "link " + i);
				dataList.add(tmp);
			}
		}

		ArrayList columnList = new ArrayList();
		columnList.add("id");
		columnList.add("uid");
		columnList.add("title");


		DataBean result = new DataBean();

		result.setValue("status", "0");
		result.setValue("message", "AOK");

		result.setCollection("beanlist", dataList);
		result.setCollection("columnlist", columnList);

		return (result);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();

		DataBean dataSet = getDataList("GET",request,response);
		ArrayList beanList = dataSet.getCollection("beanlist");
		ArrayList columnList = dataSet.getCollection("columnlist");
                dataSet.removeCollection("columnlist");
                dataSet.removeCollection("beanlist");
                dataSet.setCollection("items",beanList);
                //dataSet.setValue("totalCount",beanList.size());
                //dataSet.setValue("resultCount",beanList.size());
	    
		String data = Helper.toJson(dataSet);//serializeData(beanList, columnList, dataSet);
                
                if( dataSet.getString("fmt").equalsIgnoreCase("CSV") )
                    response.setHeader("Content-Disposition","attachment; filename=zen.csv;");
                
		out.print(data);

		writeLog(1, "do get: " + data);

		writeRequestState(request, "get");


		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		
	    response.setContentType(CONTENT_TYPE);
	    PrintWriter out = response.getWriter();

	    DataBean dataSet = getDataList("POST",request,response);
	    ArrayList beanList = dataSet.getCollection("beanlist");
	    ArrayList columnList = dataSet.getCollection("columnlist");

	    String data = serializeData(beanList, columnList, dataSet);
	    out.print(data);

	    writeLog(1, "do post: " + data);

	    writeRequestState(request, "post");


	    out.close();
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();

		DataBean dataSet = getDataList("DELETE",request,response);
		ArrayList beanList = dataSet.getCollection("beanlist");
		ArrayList columnList = dataSet.getCollection("columnlist");

		String data = serializeData(beanList, columnList, dataSet);
		out.print(data);

		writeLog(1, "do delete: " + data);

		writeRequestState(request, "delete");


		out.close();
	}


	public void doPut(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
	    response.setContentType(CONTENT_TYPE);
	    PrintWriter out = response.getWriter();

	    DataBean dataSet = getDataList("PUT",request,response);
	    ArrayList beanList = dataSet.getCollection("beanlist");
	    ArrayList columnList = dataSet.getCollection("columnlist");

	    String data = serializeData(beanList, columnList, dataSet);
	    out.print(data);

	    writeLog(1, "do put: " + data);

	    writeRequestState(request, "put");


	    out.close();
	}

	

	
	public int indexOf(ArrayList<DataBean> dList, String key, String value)
	{
		int result = -1;

		if (value != null)
		{
			for (int i = 0, size = dList.size(); i < size; i++)
			{
				if (dList.get(i).getString(key).equalsIgnoreCase(value))
				{
					result = i;
					break;
				}
			}
		}

		return (result);
	}

	public void writeRequestState(HttpServletRequest request, String prefix)
	{
		ArrayList pList = Helper.getParameterList(request);
		DataBean tmp;
		for (int i = 0, size = pList.size(); i < size; i++)
		{
			tmp = (DataBean) pList.get(i);

			writeLog(1, prefix + " parameter: " + tmp.getString("name") + " value: " + tmp.getString("value"));
		}

		Enumeration headerNames = request.getHeaderNames();

		String tToken;

		while (headerNames.hasMoreElements())
		{
			tToken = headerNames.nextElement().toString();

			writeLog(1, prefix + " header: " + tToken + " value: " + request.getHeader(tToken));
		}
	}

	public void writeLog(int level, String message)
	{
		Helper.writeLog(level,message);
	}


	public String serializeData(ArrayList beanList, ArrayList columnNameList, DataBean attributes)
	{
		return (Helper.serializeData(this,beanList,columnNameList,attributes) );
	}

	


	public static DataBean getDefaultArguments(HttpServletRequest context)
	{
		DataBean args = new DataBean();


		return (args);
	}
    
}

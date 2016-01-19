package com.jsclosures;

import javax.servlet.http.HttpServletRequest;


public class RestServiceStub
	extends DataBean implements RestService
{
    public RestServiceStub(){
        super();
    }
    private Configuration configuration;
	
	public void initialize(DataBean config)
	{

		configuration = new Configuration();
		
		String dataSourceName = config.getString("datasourcename","jdbc/webuiDB");
		
		
		if( dataSourceName != null && dataSourceName.length() > 0 )
			configuration.setValue("datasourcename",dataSourceName);
		
		
		String solr = config.getString("solr","localhost:2181:zen");
		
		
		if( solr != null && solr.length() > 0 )
			configuration.setValue("solr",solr);
		
		
		String host = config.getString("host","http://localhost");
		
		
		if( host != null && host.length() > 0 )
			configuration.setValue("host",host);
		else
			configuration.setValue("host","http://localhost");
		
		
		String messageserver = config.getString("messageserver","http://localhost/queryui/cometd/");
		
		
		if( messageserver != null && messageserver.length() > 0 )
			configuration.setValue("messageserver",messageserver);
                
                
	    String logFile = config.getString("host","/tmp/rest.txt");
	    configuration.setValue("logfile",logFile);
            
            
	    String includeMessages = config.getString("includemessages","");
	    configuration.setValue("includemessages",includeMessages);
            
            
	    String webdriver = config.getString("webdriver","http://localhost:4444/wd/hub");
	    configuration.setValue("webdriver",webdriver);
	    
	  
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

	public void writeLog(int level, String message)
	{
		Helper.writeLog(level,message);
	}

	public static DataBean getDefaultArguments(HttpServletRequest context)
	{
		DataBean args = new DataBean();


		return (args);
	}
    
}

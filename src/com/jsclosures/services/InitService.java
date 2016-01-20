package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.Helper;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import org.apache.solr.client.solrj.impl.CloudSolrClient;

/**
 *
 *
 * @author admin
 *
 */
public class InitService implements RestImplService {

    public static String CONTENTTYPE = "USER";
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"username","STRING"},
        {"userkey","STRING"},
        {"contenttype","STRING"},
        {"contentowner","STRING"},
        {"last_modified","DATE"}};
    
    public DataBean getData(RestService context,DataBean args) {
    	
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        columNameList.add("value");

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("US"));  
        solrTmp.setValue("contenttype",CONTENTTYPE);
        
        solrTmp.setValue("username","zen");  
        solrTmp.setValue("userkey",Helper.hashUserKey("1234"));  
        solrTmp.setValue("contentowner","zen");  
        
        solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 

        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
        //SolrHelper.commitToSolr(solrServer);
        solrTmp.setValue("value",solrAttributeResult.getString("error"));
        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    public DataBean putData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();


        return (result);
    }
    
    
    public DataBean postData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();


        return (result);
    }
    
    
    public DataBean deleteData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        return (result);
    }

    public static void main(String[] args) {
    	InitService gs = new InitService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

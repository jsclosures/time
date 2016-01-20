package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.Helper;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;

/**
 *
 *
 * @author admin
 *
 */
public class UserService implements RestImplService {

    public static String CONTENTTYPE = "USER";
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"username","STRING"},
        {"userkey","STRING"},
        {"useralias","STRING"},
        {"useremail","STRING"},
        {"contenttype","STRING"},
        {"contentowner","STRING"},
        {"last_modified","DATE"}};
    
    public DataBean getData(RestService context, DataBean args) {
    	
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
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype",CONTENTTYPE);
        HttpServletRequest req = (HttpServletRequest)args.getObject("request");
        SolrHelper.readSortArguments(context, req,solrArgs);
        SolrHelper.readPagingArguments(context,req, solrArgs);
        SolrHelper.readQueryArguments(context, args,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, FIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
        result.setValue("resultcount", tCache.getString("numFound"));

        ArrayList entryList = tCache.getCollection("entrylist");
        if (entryList != null)
        {
            resultList = entryList;
        }
        
        result.setValue("totalCount", tCache.getValue("numFound"));
        
        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    public DataBean postData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("US"));  
        solrTmp.setValue("contenttype",CONTENTTYPE);
        
        solrTmp.setValue("username",args.getString("username"));  
        solrTmp.setValue("userkey",Helper.hashUserKey(args.getString("userkey")));
        solrTmp.setValue("useralias",args.getString("useralias"));
        solrTmp.setValue("useralias",args.getString("useralias"));
        solrTmp.setValue("useremail",args.getString("useremail"));
        solrTmp.setValue("contentowner",args.getString("contentowner","zen"));
        solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 

        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
        //SolrHelper.commitToSolr(solrServer);
        //SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr insert attribute result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr insert attribute result: " + solrAttributeResult.getString("error"));
        result.setValue("totalCount",1);
        
        

        return (result);
    }
    
    public DataBean putData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        context.writeLog(1,"solr update document id: " + args.getValue("id"));
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",args.getValue("id"));  
        solrTmp.setValue("contenttype",CONTENTTYPE);
        
        solrTmp.setValue("username",args.getString("username"));  
        solrTmp.setValue("userkey",Helper.hashUserKey(args.getString("userkey")));    
        solrTmp.setValue("useralias",args.getString("useralias"));
        solrTmp.setValue("useralias",args.getString("useralias"));
        solrTmp.setValue("useremail",args.getString("useremail"));
        solrTmp.setValue("contentowner",args.getString("contentowner","zen"));
        
        solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 

        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);
        
        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);

        solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
        //SolrHelper.commitToSolr(solrServer);
        //SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr update result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr update attribute result: " + solrAttributeResult.getString("error"));
        result.setValue("totalCount",1);
        result.setValue("status",0);
        
        

        return (result);
    }
    
    
    public DataBean deleteData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        result.setCollection("beanlist", resultList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
    

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",args.getValue("id"));  

        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);
        
        SolrHelper.removeDocumentsFromSolrByQuery(solrServer,"parentid:" + args.getString("id"));
        //SolrHelper.commitToSolr(solrServer);
        
       // SolrHelper.optimizeToSolr(solrServer);
        
        context.writeLog(1,"solr delete attribute result: " + solrAttributeResult.getString("error"));
        
        result.setValue("message","solr delete attribute result: " + solrAttributeResult.getString("error"));
       
        result.setValue("totalCount",1);
        result.setValue("status",0);
        
        

        return (result);
    }

    public static void main(String[] args) {
    	UserService gs = new UserService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.Helper;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;

/**
 *
 *
 * @author admin
 *
 */
public class SessionManager  {

    /**
     *Create a entry in the "sessiontablename" table.
     * @param context
     * @param user
     * @param password
     * @param cookieName
     * @return
     */
    public static DataBean createSession(RestService context,DataBean args, HttpServletRequest req,HttpServletResponse res,String cookieName) {
        deleteSession(context,req,res,args,cookieName);
        
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        DataBean solrTmp = new DataBean();
        solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("AU"));  
        solrTmp.setValue("contenttype",AuthService.CONTENTTYPE);
        
        solrTmp.setValue("authname",args.getString("username").toLowerCase());  
        solrTmp.setValue("authkey",Helper.MessageDigestToHex(args.getString("username")+SolrHelper.getTimestamp()));  
        solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 

        ArrayList<DataBean> resultList = new ArrayList<DataBean>();
        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,AuthService.FIELDLIST,resultList);
        //SolrHelper.commitToSolr(solrServer);
        
        DataBean cookieArgs = new DataBean();
        cookieArgs.setValue("path",req.getContextPath());
        
        Helper.setCookie(cookieArgs,res,cookieName,solrTmp.getString("authkey"));
        
        return (solrTmp);
    }

    /**
     *Delete a session in the "sessiontablename"
     * @param context
     * @param sessionKey
     * @return
     */
    public static DataBean deleteSession(RestService context, HttpServletRequest req,HttpServletResponse res,DataBean args,String cookieName) {
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
       
        ArrayList<DataBean> resultList = getSessionsForUser(context,args);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);

        //SolrHelper.commitToSolr(solrServer);
        
        DataBean cookieArgs = new DataBean();
        cookieArgs.setValue("path",req.getContextPath());
        cookieArgs.setValue("expires",0);
        Helper.setCookie(cookieArgs,res,cookieName,"");
        

        return (solrAttributeResult);
    }
    
    public static ArrayList<DataBean> getSessionsForUser(RestService context,DataBean args) {
        DataBean result = new DataBean();

            String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
            
            DataBean solrArgs = SolrHelper.getDefaultArguments(context);
            DataBean queryArgs = new DataBean();
            
            queryArgs.setValue("contenttype",AuthService.CONTENTTYPE);
            queryArgs.setValue("authname",args.getString("username").toLowerCase());
        
        
            SolrHelper.readQueryArguments(context, queryArgs,solrArgs);

            String resourceURL = dataSourceURL;
            
            int timeOut = 1000;

            if( args.isValid("timeout") )
                timeOut = args.getInt("timeout");
            
            ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
            CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

            context.writeLog(1,"datapath: " + resourceURL);
            //check memcached for data if not there then create data with following and add to memcached
            DataBean tCache = SolrHelper.querySolr(server, params, AuthService.FIELDLIST);
            context.writeLog(1,"Sessions Query: " + tCache.toString() + " error: " + tCache.getString("error") + " found: " + tCache.getString("numFound"));
            
            ArrayList<DataBean> entryList = (ArrayList<DataBean>)tCache.getCollection("entrylist");
            

        return (entryList);
    }


    public static DataBean checkUser(RestService context,DataBean args) {
        DataBean result = new DataBean();

            String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
            
            DataBean solrArgs = SolrHelper.getDefaultArguments(context);
            
            DataBean queryArgs = new DataBean();
        
            queryArgs.setValue("contenttype",UserService.CONTENTTYPE);
            queryArgs.setValue("username",args.getString("username").toLowerCase());
            queryArgs.setValue("userkey",Helper.hashUserKey(args.getString("userkey")));
        
            SolrHelper.readQueryArguments(context, queryArgs,solrArgs);

            String resourceURL = dataSourceURL;
            
            int timeOut = 1000;

            if( args.isValid("timeout") )
                timeOut = args.getInt("timeout");
            
            ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
            CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

            context.writeLog(1,"datapath: " + resourceURL);
            //check memcached for data if not there then create data with following and add to memcached
            DataBean tCache = SolrHelper.querySolr(server, params, UserService.FIELDLIST);
            context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
            result.setValue("resultcount", tCache.getString("numFound"));

            ArrayList entryList = tCache.getCollection("entrylist");
            if (entryList != null && entryList.size() > 0 )
            {
                result = (DataBean)entryList.get(0);
            }

        return (result);
    }
    
    /**
     *Check that a key exist in the table
     * @param context
     * @param sessionKey
     * @return
     */
    public static DataBean checkSession(RestService context,DataBean args) {
        DataBean result = new DataBean();

            String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
            
            DataBean solrArgs = SolrHelper.getDefaultArguments(context);
            
            DataBean queryArgs = new DataBean();
        
            queryArgs.setValue("contenttype",AuthService.CONTENTTYPE);
            queryArgs.setValue("authkey",args.getString("authkey"));
        
            SolrHelper.readQueryArguments(context, queryArgs,solrArgs);

            String resourceURL = dataSourceURL;
            
            int timeOut = 1000;

            if( args.isValid("timeout") )
                timeOut = args.getInt("timeout");
            
            ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
            CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

            context.writeLog(1,"datapath: " + resourceURL);
            //check memcached for data if not there then create data with following and add to memcached
            DataBean tCache = SolrHelper.querySolr(server, params, AuthService.FIELDLIST);
            context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
            result.setValue("resultcount", tCache.getString("numFound"));

            ArrayList entryList = tCache.getCollection("entrylist");
            if (entryList != null && entryList.size() > 0 )
            {
                result = (DataBean)entryList.get(0);
            }

        return (result);
    }

    public static DataBean checkUserHasSession(RestService context,DataBean args) {
        DataBean result = new DataBean();

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        DataBean queryArgs = new DataBean();
        
        queryArgs.setValue("contenttype",AuthService.CONTENTTYPE);
        queryArgs.setValue("authname",args.getString("username").toLowerCase());

        SolrHelper.readQueryArguments(context, queryArgs,solrArgs);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
        CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolr(server, params, AuthService.FIELDLIST);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
       
        ArrayList entryList = tCache.getCollection("entrylist");
        if (entryList != null)
        {
            result = (DataBean)entryList.get(0);
        }
        
        return (result);
    }

    public static DataBean deleteSessionByUser(RestService context, DataBean args) {
        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        
        DataBean solrTmp = checkUserHasSession(context,args); 

        ArrayList<DataBean> resultList = new ArrayList<DataBean>();
        resultList.add(solrTmp);
        
        CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

        DataBean solrAttributeResult = SolrHelper.removeDocumentsFromSolr(solrServer,resultList);

        //SolrHelper.commitToSolr(solrServer);

        return (solrTmp);
    }
    
    public static void main(String[] args) {
    	SessionManager gs = new SessionManager();
    }
}

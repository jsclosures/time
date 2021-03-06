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
public class SignUpService implements RestImplService {

    public static String CONTENTTYPE = "SIGNUP";
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"username","STRING"},
        {"userkey","STRING"},
        {"useremail","STRING"},
        {"useralias","STRING"},
        {"contenttype","STRING"},
        {"contentowner","STRING"},
        {"groupname","STRING"},
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
    

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        DataBean solrTmp = new DataBean();
        resultList.add(solrTmp);
        
        if( args.isValid("username") && args.isValid("userkey") && args.isValid("useralias") && args.isValid("useremail") ){
            context.writeLog(1,"signup: " + args.toString());
            UserService service = new UserService();
            DataBean queryArgs = new DataBean();
            queryArgs.setValue("username",args.getValue("username"));
            queryArgs.setObject("request",args.getObject("request"));
            queryArgs.setObject("response",args.getObject("response"));
            context.writeLog(1,"queryArgs: " + queryArgs.toString());
            
            DataBean checkResult = service.getData(context,queryArgs);
            context.writeLog(1,"checkResult: " + checkResult);
            
            ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
            context.writeLog(1,"userList: " + userList);
            
            if( userList == null || userList.size() == 0 ){
                
                solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("SU"));  
                solrTmp.setValue("contenttype",CONTENTTYPE);
                
                solrTmp.setValue("username",args.getString("username"));  
                solrTmp.setValue("useralias",args.getString("useralias"));  
                solrTmp.setValue("useremail",args.getString("useremail"));  
                solrTmp.setValue("userkey",Helper.hashUserKey(args.getString("userkey"))); 
                
                String contentOwner;
                
                if( args.isValid("groupkey") ){
                    contentOwner = args.getString("groupkey");
                    DataBean uqueryArgs = new DataBean();
                    uqueryArgs.setValue("contenttype",SignUpService.CONTENTTYPE);
                    uqueryArgs.setValue("contentowner",contentOwner);
                    uqueryArgs.setObject("request",args.getObject("request"));
                    uqueryArgs.setObject("response",args.getObject("response"));
                    context.writeLog(1,"queryArgs: " + uqueryArgs.toString());
                    
                    DataBean groupResult = service.getData(context,uqueryArgs);
                    context.writeLog(1,"groupResult: " + groupResult);
                    
                    ArrayList<DataBean> groupList = groupResult.getCollection("beanlist");
                    context.writeLog(1,"groupList: " + groupList);
                    
                    if( groupList != null || groupList.size() > 0 ){
                        DataBean group = groupList.get(0);
                        solrTmp.setValue("groupname",group.getValue("groupname"));
                    }
                }
                else if( args.isValid("groupname") ) {
                    solrTmp.setValue("groupname",args.getString("groupname"));
                    
                    contentOwner = Helper.hashUserKey(args.getString("groupname"));
                    solrTmp.setValue("groupkey",contentOwner);
                    columNameList.add("groupkey");
                    
                    context.writeLog(1,"by groupname: " + contentOwner + " from " + args.getString("groupname"));
                }
                else {
                    contentOwner = "zen";
                    solrTmp.setValue("groupname",contentOwner);
                    solrTmp.setValue("groupkey",contentOwner);
                    
                    context.writeLog(1,"by default: " + contentOwner);
                }
                solrTmp.setValue("contentowner",contentOwner);  
                
                solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 
        
                args.setValue("contentowner",contentOwner);
                //save the user
                service.postData(context,args);
                
                CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);
        
                DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
                SolrHelper.releaseServer(solrServer);
                solrTmp.setValue("value",solrAttributeResult.getString("error"));
            }
            else {
                solrTmp.setValue("value","user exists: " + args.getString("username"));
            }
        }
        else {
            StringBuffer missing = new StringBuffer();
            
            if( !args.isValid("username") ){
                missing.append("username ");
            }
            if( !args.isValid("userkey") ){
                missing.append("userkey ");
            }
            if( !args.isValid("useralias") ){
                missing.append("useralias ");
            }
            if( !args.isValid("useremail") ){
                missing.append("useremail ");
            }
            columNameList.add("value");
            
            solrTmp.setValue("value","missing: " + missing.toString());
        }
        
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
    	SignUpService gs = new SignUpService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

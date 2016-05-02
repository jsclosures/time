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
public class ManagerService implements RestImplService {

    public static String CONTENTTYPE = "MANAGER";
    public static String FIELDLIST[][] = {{"action","STRING"},
        {"username","STRING"},
        {"contentowner","STRING"},
        {"groupname","STRING"},
        {"last_modified","DATE"}};
    
    public DataBean getData(RestService context,DataBean args) {
    	
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
       
    

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);

        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        DataBean solrTmp = new DataBean();
        resultList.add(solrTmp);
        StringBuffer missing = new StringBuffer();
        
        String action = args.getString("action");
        if( action.equalsIgnoreCase("ACTIVATEUSER") ){
            if( !args.isValid("authname") ){
                missing.append("authname ");
            }
            else {
                UserService service = new UserService();
                DataBean queryArgs = new DataBean();
                queryArgs.setValue("username",args.getValue("authname"));
                queryArgs.setObject("request",args.getObject("request"));
                queryArgs.setObject("response",args.getObject("response"));
                context.writeLog(1,"queryArgs: " + queryArgs.toString());
                
                DataBean checkResult = service.getData(context,queryArgs);
                context.writeLog(1,"checkResult: " + checkResult);
                
                ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
                context.writeLog(1,"userList: " + userList);
                
                if( userList != null || userList.size() > 0 ){
                    DataBean user = userList.get(0);
                    user.setObject("request",args.getObject("request"));
                    user.setObject("response",args.getObject("response"));
                    context.writeLog(1,"user: " + user.toString());
                    
                    user.setValue("userstatus","1");
                    
                    service.postData(context,user);
                    
                    solrTmp.setValue("username",user.getValue("username"));
                    solrTmp.setValue("userstatus",user.getValue("userstatus"));
                    
                    columNameList.add("username");
                    columNameList.add("userstatus");
                }
            }
        }
        else if( action.equalsIgnoreCase("CHECK") ){
            StringBuffer checking = new StringBuffer();
            
            if( args.isValid("authname") ){
                UserService service = new UserService();
                DataBean queryArgs = new DataBean();
                queryArgs.setValue("username",args.getValue("authname"));
                queryArgs.setObject("request",args.getObject("request"));
                queryArgs.setObject("response",args.getObject("response"));
                context.writeLog(1,"queryArgs: " + queryArgs.toString());
                
                DataBean checkResult = service.getData(context,queryArgs);
                context.writeLog(1,"checkResult: " + checkResult);
                
                ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
                context.writeLog(1,"userList: " + userList);
                
                if( userList != null || userList.size() > 0 ){
                    checking.append("authname exists ");
                }
            }
                        
            if( args.isValid("groupname") ){
                UserService service = new UserService();
                DataBean queryArgs = new DataBean();
                queryArgs.setValue("contentowner",Helper.hashUserKey(args.getString("groupname")));
                queryArgs.setObject("request",args.getObject("request"));
                queryArgs.setObject("response",args.getObject("response"));
                context.writeLog(1,"queryArgs: " + queryArgs.toString());
                
                DataBean checkResult = service.getData(context,queryArgs);
                context.writeLog(1,"checkResult: " + checkResult);
                
                ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
                context.writeLog(1,"userList: " + userList);
                
                if( userList != null || userList.size() > 0 ){
                    checking.append("groupname exists ");
                }
            }
            
            if( args.isValid("phone") ){
                UserService service = new UserService();
                DataBean queryArgs = new DataBean();
                queryArgs.setValue("phone",args.getString("phone"));
                queryArgs.setObject("request",args.getObject("request"));
                queryArgs.setObject("response",args.getObject("response"));
                context.writeLog(1,"queryArgs: " + queryArgs.toString());
                
                DataBean checkResult = service.getData(context,queryArgs);
                context.writeLog(1,"checkResult: " + checkResult);
                
                ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
                context.writeLog(1,"userList: " + userList);
                
                if( userList != null || userList.size() > 0 ){
                    checking.append("phone exists ");
                }
            }
            
            if( checking.length() > 0 ){
                columNameList.add("value");
                solrTmp.setValue("value","checking: " + checking.toString());
            }
        }
        else {
            missing.append("action");
        }
        
        if( missing.length() > 0 ){
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
    	InitService gs = new InitService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

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
public class MessageService extends SolrService {

    public static String CONTENTTYPE = "MESSAGE";
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"phone","STRING"},
        {"comments","STRING"},
        {"location","STRING"},
        {"imagedata","STRING"},
        {"starttime","STRING"},
        {"endtime","STRING"},
        {"contenttype","STRING"},
        {"contentowner","STRING"},
        {"last_modified","DATE"}};
    
    
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
            DataBean user = userList.get(0);
        
            DataBean solrTmp = new DataBean();
            solrTmp.setValue("id",SolrHelper.hashIDWithPrefix("MS"));  
            solrTmp.setValue("contenttype",CONTENTTYPE);
            
            solrTmp.setValue("phone",args.getString("phone"));  
            solrTmp.setValue("location",args.getString("location"));
            solrTmp.setValue("starttime",args.getString("starttime"));
            solrTmp.setValue("endtime",args.getString("endtime"));
            solrTmp.setValue("imagedata",args.getString("imagedata"));
            solrTmp.setValue("comments",args.getString("comments"));
            solrTmp.setValue("contentowner",user.getString("contentowner","zen"));
            solrTmp.setValue("username",user.getString("username","zen"));
            solrTmp.setValue("last_modified",SolrHelper.getTimestamp()); 
    
            resultList.add(solrTmp);
        
            CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, timeOut);

            DataBean solrAttributeResult = SolrHelper.addDocumentsToSolr(solrServer,FIELDLIST,resultList);
            
            createTrackerData(context,args,solrTmp,solrServer);
            
            //SolrHelper.commitToSolr(solrServer);
            //SolrHelper.optimizeToSolr(solrServer);
            SolrHelper.releaseServer(solrServer);
        
            context.writeLog(1,"solr insert attribute result: " + solrAttributeResult.getString("error"));
            result.setValue("message","solr insert attribute result: " + solrAttributeResult.getString("error"));
            result.setValue("totalCount",1);
        }
        
        return (result);
    }
    
    
    private void createTrackerData(RestService context,DataBean args,DataBean message,CloudSolrClient solrServer){
        
        UserProfileService userProfileService = new UserProfileService();
        DataBean queryArgs = new DataBean();
        queryArgs.setValue("username",message.getString("username"));
        queryArgs.setObject("request",args.getObject("request"));
        queryArgs.setObject("response",args.getObject("response"));
        context.writeLog(1,"queryArgs: " + queryArgs.toString());
        
        DataBean checkResult = userProfileService.getData(context,queryArgs);
        context.writeLog(1,"checkResult: " + checkResult);
        
        ArrayList<DataBean> userProfileList = checkResult.getCollection("beanlist");
        context.writeLog(1,"userProfileList: " + userProfileList);
        DataBean userProfile;
        
        if( userProfileList != null || userProfileList.size() > 0 ){
            userProfile = userProfileList.get(0);    
        }
        else {
            userProfile = new DataBean();
        }
        
        if( !message.isValid("starttime") ){
            if( userProfile.isValid("starttime") ){
                message.setValue("starttime",userProfile.getValue("starttime"));
            }
            else {
                message.setValue("starttime",SolrHelper.getTimestamp());
            }
        }
        
        if( !message.isValid("endtime") ){
            if( userProfile.isValid("endtime") ){
                message.setValue("endtime",userProfile.getValue("endtime"));
            }
            else {
                message.setValue("endtime",SolrHelper.getTimestamp());
            }
        }
        
        ArrayList<DataBean> docList = new ArrayList<>();
        docList.add(message);
        DataBean docInsertResult = SolrHelper.addDocumentsToSolr(solrServer,TrackerService.FIELDLIST,docList);
        context.writeLog(1,"tracker insert result: " + docInsertResult.getString("error"));
    }

    public static void main(String[] args) {
    	MessageService gs = new MessageService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

package com.jsclosures;

import com.jsclosures.services.SessionManager;

import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationService extends RestServiceServlet {
    public AuthenticationService() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public DataBean getDataList(String mode, HttpServletRequest req, HttpServletResponse resp) {
        ArrayList columnList = new ArrayList();
        

        DataBean result = new DataBean();
        ArrayList<DataBean> dataList = new ArrayList<DataBean>();

        result.setValue("status", "0");
        result.setValue("message", "UNK");

        result.setCollection("beanlist", dataList);
        result.setCollection("columnlist", columnList);
        writeLog(2, "auth doing get data for mode " + mode);

        if (mode.equalsIgnoreCase("GET")) {
            String contentType = req.getParameter("contenttype");
            if (contentType == null || contentType.length() == 0)
                contentType = "CONTENT";
            
            DataBean queryArgs = Helper.readAllParameters(this, req);
            Helper.readSortArguments(this, req, queryArgs);
            Helper.readPagingArguments(this, req, queryArgs);
            
            if (contentType.equalsIgnoreCase("AUTH")) {
                String cookieName = getConfiguration().getString("cookiename","AUTH");
                
                DataBean authArgs = new DataBean();
                authArgs.setValue("authkey",Helper.getCookie(req,cookieName));
                
                if( authArgs.isValid("authkey") ){
                    DataBean user = SessionManager.checkSession(this,authArgs);
                    
                    if( user.isValid("id") ){
                        result.setValue("status", "1");
                        result.setValue("message", "AOK");
                        user.setValue("username",user.getValue("authname"));
                        dataList.add(user);
                        columnList.add("id");
                        columnList.add("username");
                    }
                    else {
                        result.setValue("status", "0");
                        result.setValue("message", "AUTHFAILED");
                    }
                }
                else {
                    result.setValue("status", "0");
                    result.setValue("message", "AUTHFAILED");
                }
            }
        } else if (mode.equalsIgnoreCase("POST")) {
            DataBean queryArgs = Helper.readAllJSONParameters(this, req);
            String contentType = queryArgs.getString("contenttype");
            
            writeLog(2, "auth doing post " + contentType);

            if (contentType.equalsIgnoreCase("AUTH")) {
                String cookieName = getConfiguration().getString("cookiename","AUTH");
                
                DataBean user = SessionManager.checkUser(this,queryArgs);
                if( user.isValid("id") ){
                    result.setValue("status", "1");
                    result.setValue("message", "AOK");
                    dataList.add(user);
                    columnList.add("id");
                    columnList.add("username");
                    
                    DataBean authResult = SessionManager.createSession(this,user,req,resp,cookieName);
                    
                    if( queryArgs.isValid("getkey") ){
                        writeLog(1,"setting authkey in result: " + authResult.getString("authkey"));
                        columnList.add("authkey");
                        
                        user.setValue("authkey",authResult.getString("authkey"));
                    }
                }
                else {
                    result.setValue("status", "-1");
                    result.setValue("message", "AUTHFAILED");
                }
            }
        } else if (mode.equalsIgnoreCase("PUT")) {
            DataBean queryArgs = Helper.readAllJSONParameters(this, req);
            String contentType = queryArgs.getString("contenttype");
            

        } else if (mode.equalsIgnoreCase("DELETE")) {
            DataBean queryArgs = Helper.readAllParameters(this, req);
            String contentType = queryArgs.getString("contenttype");
            writeLog(2, "auth doing delete " + contentType);

            if (contentType.equalsIgnoreCase("AUTH")) {
                String cookieName = getConfiguration().getString("cookiename","AUTH");
                
                DataBean authArgs = new DataBean();
                authArgs.setValue("authkey",Helper.getCookie(req,cookieName));
                
                DataBean user = SessionManager.checkSession(this,authArgs);
                if( user.isValid("id") ){
                    result.setValue("status", "1");
                    result.setValue("message", "AOK");
                    SessionManager.deleteSession(this,req,resp,user,cookieName);
                }
                else {
                    result.setValue("status", "-1");
                    result.setValue("message", "AUTHFAILED");
                }
            }   
        }

        return (result);
    }
}

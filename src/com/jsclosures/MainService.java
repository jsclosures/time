package com.jsclosures;

import com.jsclosures.services.RestImplService;
import com.jsclosures.services.SessionManager;

import com.jsclosures.services.UserService;

import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainService extends RestServiceServlet {
    public MainService() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public DataBean getDataList(String mode, HttpServletRequest req, HttpServletResponse resp) {
        ArrayList columnList = new ArrayList();
        
        DataBean mappingRec = getConfiguration().getStructure("mapping");
        
        DataBean result = new DataBean();
        ArrayList<DataBean> dataList = new ArrayList<DataBean>();

        result.setValue("status", "0");
        result.setValue("message", "AOK");

        result.setCollection("beanlist", dataList);
        result.setCollection("columnlist", columnList);
        writeLog(2, "main doing get data for mode " + mode);
        
        String contentType = req.getParameter("contenttype");
        if (contentType == null || contentType.length() == 0)
            contentType = "CONTENT";
        
        String cookieName = getConfiguration().getString("cookiename","AUTH");
        
        DataBean authArgs = new DataBean();
        authArgs.setValue("authkey",Helper.getCookie(req,cookieName));
        
        if( !authArgs.isValid("authkey") ){
            authArgs.setValue("authkey",Helper.getAuthenticationKey(req,cookieName));
        }

        if( authArgs.isValid("authkey") ){
            DataBean session = SessionManager.checkSession(this,authArgs);
             
            if( session.isValid("id") && session.isValid("authname") ){
                String currentUserName = session.getString("authname");
                
                UserService userService = new UserService();
                DataBean searchArgs = new DataBean();
                searchArgs.setValue("username",currentUserName);
                searchArgs.setObject("request",req);
                searchArgs.setObject("response",resp);
                writeLog(1,"userQueryArgs: " + searchArgs.toString());
                
                DataBean checkResult = userService.getData(this,searchArgs);
                writeLog(1,"check user info: " + checkResult);
                
                ArrayList<DataBean> userList = checkResult.getCollection("beanlist");
                writeLog(1,"userList: " + userList);
                DataBean user;
                
                if( userList != null || userList.size() > 0 ){
                    user = userList.get(0);
                    if( user.isValid("userrole") && user.getString("userrole").equalsIgnoreCase("system") && req.getParameter("username") != null ){
                        searchArgs = new DataBean();
                        searchArgs.setValue("username",req.getParameter("username"));
                        searchArgs.setObject("request",req);
                        searchArgs.setObject("response",resp);
                        writeLog(1,"asuser userQueryArgs: " + searchArgs.toString());
                        
                        checkResult = userService.getData(this,searchArgs);
                        writeLog(1,"asuser check user info: " + checkResult);
                        
                        userList = checkResult.getCollection("beanlist");
                        writeLog(1,"asuser userList: " + userList);
                 
                        if( userList != null || userList.size() > 0 ){
                            user = userList.get(0);
                        }
                    }
                    
                    currentUserName = user.getString("username");
                }
                else {
                    user = new DataBean();
                    user.setValue("username",currentUserName);
                }
                
                if (mode.equalsIgnoreCase("GET")) {
                    DataBean queryArgs = Helper.readAllParameters(this, req);
                    Helper.readSortArguments(this, req, queryArgs);
                    Helper.readPagingArguments(this, req, queryArgs);
                    queryArgs.setValue("user",currentUserName);
                    queryArgs.setValue("username",currentUserName);
                    queryArgs.setValue("contentowner",user.getString("contentowner","zen"));
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        
                        result = gs.getData(this, queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Get Handler: " + e.toString());
                    }
                    
                    if( queryArgs.isValid("fmt") )
                        result.setValue("fmt",queryArgs.getValue("fmt"));
                    
                } else if (mode.equalsIgnoreCase("POST")) {
                    DataBean queryArgs = Helper.readAnyParameters(this, req);
                    writeLog(2,queryArgs.toString());
                    if( !queryArgs.isValid("contenttype") )
                        queryArgs = Helper.readAllParameters(this, req);
                    
                    contentType = queryArgs.getString("contenttype");
                    queryArgs.setValue("user",currentUserName);
                    queryArgs.setValue("username",currentUserName);
                    queryArgs.setValue("contentowner",user.getString("contentowner","zen"));
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    
                    writeLog(2, "doing post " + contentType);

                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        
                        result = gs.postData(this,queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Post Handler: " + e.toString());
                    }

                } else if (mode.equalsIgnoreCase("PUT")) {
                    DataBean queryArgs = Helper.readAllJSONParameters(this, req);
                    contentType = queryArgs.getString("contenttype");
                    queryArgs.setValue("user",currentUserName);
                    queryArgs.setValue("username",currentUserName);
                    queryArgs.setValue("contentowner",user.getString("contentowner","zen"));
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        
                        result = gs.putData(this,queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Put Handler: " + e.toString());
                    }

                } else if (mode.equalsIgnoreCase("DELETE")) {
                    DataBean queryArgs = Helper.readAnyParameters(this, req);
                    
                    contentType = queryArgs.getString("contenttype");
                    queryArgs.setValue("user",currentUserName);
                    queryArgs.setValue("username",currentUserName);
                    queryArgs.setValue("contentowner",user.getString("contentowner","zen"));
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);    
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        
                        result = gs.deleteData(this,queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Delete Handler: " + e.toString());
                    }
                }
            }
            else {
                if( contentType.equalsIgnoreCase("INIT") ) {
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        DataBean queryArgs = Helper.readAllParameters(this, req);
                        queryArgs.setValue("user","zen");
                        queryArgs.setValue("username","zen");
                        queryArgs.setValue("contentowner","zen");
                        queryArgs.setObject("request",req);
                        queryArgs.setObject("response",resp);
                        result = gs.getData(this, queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Handler: " + e.toString());
                    }
                }
                else if( contentType.equalsIgnoreCase("SIGNUP") ) {
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        DataBean queryArgs = Helper.readAllParameters(this, req);
                        //queryArgs.setValue("user",currentUserName);
                        //queryArgs.setValue("username",currentUserName);
                        
                        queryArgs.setObject("request",req);
                        queryArgs.setObject("response",resp);
                        result = gs.getData(this, queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Handler: " + e.toString());
                    }
                }
                else {
                    result.setValue("status", "-1");
                    result.setValue("message", "NOHANDLER");
                }
            }
        }
        else {
            if( contentType.equalsIgnoreCase("INIT") ) {
                try{
                    RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                    DataBean queryArgs = Helper.readAllParameters(this, req);
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    result = gs.getData(this, queryArgs);
                }catch(Exception e){
                    writeLog(1,"Handler: " + e.toString());
                }
            }
            else if( contentType.equalsIgnoreCase("SIGNUP") ) {
                try{
                    writeLog(1,"create signup");
                    RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                    DataBean queryArgs = Helper.readAllParameters(this, req);
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    result = gs.getData(this, queryArgs);
                }catch(Exception e){
                    writeLog(1,"Handler: " + e.toString());
                }
            }
            else {
                result.setValue("status", "-1");
                result.setValue("message", "AUTHFAILED");
            }
        }

        return (result);
    }
}

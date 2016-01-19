package com.jsclosures;

import com.jsclosures.services.RestImplService;
import com.jsclosures.services.SessionManager;

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
        writeLog(2, "doing get data for mode " + mode);
        
        String contentType = req.getParameter("contenttype");
        if (contentType == null || contentType.length() == 0)
            contentType = "CONTENT";
        
        String cookieName = getConfiguration().getString("cookiename","AUTH");
        
        DataBean authArgs = new DataBean();
        authArgs.setValue("authkey",Helper.getCookie(req,cookieName));
        
        if( authArgs.isValid("authkey") ){
            DataBean user = SessionManager.checkSession(this,authArgs);
            String currentUserName = user.getString("username");
            
            if( user.isValid("id") ){
                if (mode.equalsIgnoreCase("GET")) {
                    
                    
                    DataBean queryArgs = Helper.readAllParameters(this, req);
                    Helper.readSortArguments(this, req, queryArgs);
                    Helper.readPagingArguments(this, req, queryArgs);
                    queryArgs.setValue("user",currentUserName);
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
                    DataBean queryArgs = Helper.readAllJSONParameters(this, req);
                    contentType = queryArgs.getString("contenttype");
                    queryArgs.setValue("user",currentUserName);
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
                    queryArgs.setObject("request",req);
                    queryArgs.setObject("response",resp);
                    try{
                        RestImplService gs = loadServiceClass(mappingRec.getString(contentType));
                        
                        result = gs.putData(this,queryArgs);
                    }catch(Exception e){
                        writeLog(1,"Put Handler: " + e.toString());
                    }

                } else if (mode.equalsIgnoreCase("DELETE")) {
                    DataBean queryArgs = Helper.readAllParameters(this, req);
                    if( !queryArgs.isValid("contenttype") )
                        queryArgs = Helper.readAllJSONParameters(this, req);
                    
                    contentType = queryArgs.getString("contenttype");
                    queryArgs.setValue("user",currentUserName);
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
            else {
                result.setValue("status", "-1");
                result.setValue("message", "AUTHFAILED");
            }
        }

        return (result);
    }
}

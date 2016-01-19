package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.RestServiceStub;

/**
 *
 * DataBean tmp = new DataBean();
        try{
	        Connection conn = ConnectionManager.getConnection(context,"default");
	
	        tmp.setValue("id", conn != null ? "ben-jamin" : "bad");
        }
        catch(Exception e){
        	tmp.setValue("id",e.getMessage());
        }

        resultList.add(tmp);
 * @author admin
 *
 */
public class GrammarDetailService extends SolrService {

   
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"sentencetype","STRING"},
        {"responses","ARRAY"},
        {"failedresponses","ARRAY"},
        {"fields","ARRAY"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
     public static String CONTENTTYPE = "GRAMMARDETAIL";
    public static String CONTENTTYPEPREFIX = "GD";
    
    public GrammarDetailService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
        setParentKeyName("sentencetype");
    }
    
    
    

    public static void main(String[] args) {
        
        RestServiceStub ctx = new RestServiceStub();
        DataBean initArgs = new DataBean();
        initArgs.setValue("solr","localhost:10983:zen");
        ctx.initialize(initArgs);
        
        DataBean queryArgs = new DataBean();
        queryArgs.setValue("sentencetype","QUESTION.HOW");
    	GrammarDetailService gs = new GrammarDetailService();
        DataBean foo = gs.getData(ctx,queryArgs);
        System.out.println(foo);
    }
}
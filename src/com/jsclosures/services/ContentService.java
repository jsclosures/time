package com.jsclosures.services;

import com.jsclosures.DataBean;

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
public class ContentService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"contenttitle","STRING"},
        {"contentbody","STRING"},
        {"carryforward","STRING"},
        {"contenttype","STRING"},
        {"contentowner","STRING"},
        {"last_modified","DATE"}};
     public static String CONTENTTYPE = "CONTENT";
    public static String CONTENTTYPEPREFIX = "CT";
    
    public ContentService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }

    public static void main(String[] args) {
    	ContentService gs = new ContentService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}
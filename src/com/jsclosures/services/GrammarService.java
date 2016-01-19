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
public class GrammarService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"sentencetype","STRING"},
        {"condition","STRING"},
        {"action","STRING"},
        {"grammars","STRING"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
     public static String CONTENTTYPE = "GRAMMAR";
    public static String CONTENTTYPEPREFIX = "GR";
    
    public GrammarService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }

    public static void main(String[] args) {
    	GrammarService gs = new GrammarService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}
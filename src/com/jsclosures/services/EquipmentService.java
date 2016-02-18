package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 *
 * @author admin
 *
 */
public class EquipmentService extends SolrService {
    public EquipmentService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"title","STRING"},
                                            {"comments","STRING"},
                                            {"background","STRING"},
                                            {"username","STRING"},
                                            {"contentowner","STRING"},
                                            {"contenttype","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "EQUIPMENT";
    public static String CONTENTTYPEPREFIX = "EQ";

    public static void main(String[] args) {
    	EquipmentService gs = new EquipmentService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

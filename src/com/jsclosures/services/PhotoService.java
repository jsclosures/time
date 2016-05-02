package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 *
 * @author admin
 *
 */
public class PhotoService extends SolrService {
    public PhotoService(){
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
     public static String CONTENTTYPE = "PHOTO";
    public static String CONTENTTYPEPREFIX = "PT";

    public static void main(String[] args) {
    	PhotoService gs = new PhotoService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

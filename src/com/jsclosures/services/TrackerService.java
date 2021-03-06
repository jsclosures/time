package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 *
 * @author admin
 *
 */
public class TrackerService extends SolrService {
    public TrackerService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
        setSort("last_modified desc");
    }
    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"name","STRING"},
                                            {"job","STRING"},
                                            {"equipment","STRING"},
                                            {"comments","STRING"},
                                            {"location","STRING"},
                                            {"starttime","STRING"},
                                            {"endtime","STRING"},
                                            {"username","STRING"},
                                            {"contenttype","STRING"},
                                            {"contentowner","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "TRACKER";
    public static String CONTENTTYPEPREFIX = "TR";

    public static void main(String[] args) {
    	TrackerService gs = new TrackerService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

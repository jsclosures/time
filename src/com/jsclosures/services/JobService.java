package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 *
 * @author admin
 *
 */
public class JobService extends SolrService {
    public JobService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"title","STRING"},
                                            {"comments","STRING"},
                                            {"username","STRING"},
                                            {"contenttype","STRING"},
                                            {"contentowner","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "JOB";
    public static String CONTENTTYPEPREFIX = "JB";

    public static void main(String[] args) {
    	JobService gs = new JobService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

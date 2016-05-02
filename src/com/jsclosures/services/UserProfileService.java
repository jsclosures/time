package com.jsclosures.services;

import com.jsclosures.DataBean;

/**
 *
 *
 * @author admin
 *
 */
public class UserProfileService extends SolrService {
    public UserProfileService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"username","STRING"},
                                            {"starttime","STRING"},
                                            {"endtime","STRING"},
                                            {"breaktime","STRING"},
                                            {"contenttype","STRING"},
                                            {"contentowner","STRING"},
                                            {"last_modified","DATE"}};
     public static String CONTENTTYPE = "USERPROFILE";
    public static String CONTENTTYPEPREFIX = "UP";

    public static void main(String[] args) {
    	UserProfileService gs = new UserProfileService();
        DataBean foo = gs.getData(null,new DataBean());
        System.out.println(foo);
    }
}

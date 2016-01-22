package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;

/**
 *
 *
 * @author admin
 *
 */
public class TrackerFacetService extends SolrService {
    public TrackerFacetService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
    public static String FIELDLIST[][] = {{"id","NUMBER"},
                                            {"name","STRING"}};
     public static String CONTENTTYPE = TrackerService.CONTENTTYPE;
    public static String CONTENTTYPEPREFIX = "TR";
    
    public DataBean getData(RestService context, DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList resultList = new ArrayList();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        columNameList.add("value");

        String dataSourceURL = SolrHelper.getDefaultDataSourceURL(context);
        
        DataBean solrArgs = SolrHelper.getDefaultArguments(context);
        
        args.setValue("contenttype",CONTENTTYPE);
        
    
        String resourceURL = dataSourceURL;
        
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        String facetField = args.getString("field","name");
        
        ModifiableSolrParams params = SolrHelper.getQueryParameters();
        params.set("rows","0");
        params.set("facet","true");
        params.set("facet.field",facetField);
        params.set("facet.mincount","1");
        params.set("q","contenttype:" + CONTENTTYPE);
        
        CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

        context.writeLog(1,"datapath: " + resourceURL);
        //check memcached for data if not there then create data with following and add to memcached
        DataBean tCache = SolrHelper.querySolrFacet(server, params);
        context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
        result.setValue("resultcount", tCache.getString("numFound"));
        SolrHelper.releaseServer(server);
        
        ArrayList<DataBean> entryList = (ArrayList<DataBean>)tCache.getCollection("entrylist");
        if (entryList != null)
        {

                    for(int i = 0,size = entryList.size();i < size;i++){
                        if( entryList.get(i).getInt("count") >= 1 ) {
                                DataBean tmp = new DataBean();
                                tmp.setValue("id",entryList.get(i).getString("name"));
                                tmp.setValue("name",entryList.get(i).getString("name"));
                            
                                resultList.add(tmp);
                        }
                    }
            
        }
        
        result.setValue("totalCount", tCache.getValue("numFound"));
        
        
        result.setCollection("beanlist", resultList);

        return (result);
    }

    public static void main(String[] args) {
    	TrackerFacetService gs = new TrackerFacetService();
        DataBean foo = gs.getData(null, new DataBean());
        System.out.println(foo);
    }
}

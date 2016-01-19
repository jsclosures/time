package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.Helper;
import com.jsclosures.RestService;
import com.jsclosures.RestServiceStub;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;

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
public class AgentService implements RestImplService {

    /** current date. */
    public static String DATE = "@DATE";

    /** Curreent Day of the week. */
    public static String DAY = "@DAY";

    /** which month is currently is */
    public static String MONTH = "@MONTH";

    /** morning greeting.  Good Morning. */
    public static String MORNING = "@MORNING";

    /** Current users alias. */
    public static String NICKNAME = "@NICKNAME";

    /** current time. */
    public static String TIME = "@TIME";

    /** current username. */
    public static String USER = "@USER";

    /** current year. */
    public static String YEAR = "@YEAR";

    /** current used words */
    public static String USEDWORDS = "@USEDWORDS";

    /** current key words */
    public static String KEYWORDS = "@KEYWORDS";

    /** current frame */
    public static String SENTENCETYPE = "@SENTENCETYPE";

    /** current topic. */
    public static String CURRENTTOPIC = "@CURRENTTOPIC";

    /** parent topic. */
    public static String PARENTTOPIC = "@PARENTTOPIC";
    
    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"sentencetype","STRING"},
        {"action","STRING"},
        {"condition","STRING"},
        {"score","STRING"},                                 
        {"grammars","STRING"},
        {"keywords","STRING"},
        {"contenttype","STRING"}};
    
     public static String CONTENTTYPE = "AGENT";
    
    public AgentService(){
    }
    
    public DataBean getData(RestService context,DataBean args) {
        
        //DataBean setup
        DataBean result = new DataBean();

        ArrayList<DataBean> resultList = new ArrayList<>();
        ArrayList columNameList = new ArrayList();

        result.setCollection("columnlist", columNameList);
        
        for(int i = 0;i < FIELDLIST.length;i++)
        {
          columNameList.add(FIELDLIST[i][0]);
        }
        
        String query = args.getString("query");
        
        ArrayList<String> qList = Helper.splitFields(query," ");
        if( qList.size() > 0 ){
            
            DataBean queryArgs = SolrHelper.getDefaultArguments(context);
            queryArgs.setValue("fq","contenttype:GRAMMAR");
            queryArgs.setValue("fl","*,score");
            queryArgs.setValue("sort","score desc");
            
            StringBuffer queryStr = new StringBuffer();
            int interval = 20/qList.size();
            int start = 0;
            StringBuffer runningQueryStr = new StringBuffer();
            
            for(int i = 0,size = qList.size();i < size;i++){
                String tQueryPrefix = "q".concat(String.valueOf(i+1));
                String tTerm = qList.get(i);
                if( i > 0 ){
                    queryStr.append(" OR ");
                    runningQueryStr.append(" AND ");
                }
                queryStr.append("_val_:{!scaled queryPrefix=\"" + tQueryPrefix + "\"}");
                runningQueryStr.append(tTerm);
                queryArgs.setValue(tQueryPrefix.concat("q"),runningQueryStr.toString());
                queryArgs.setValue(tQueryPrefix.concat("l"),start);
                queryArgs.setValue(tQueryPrefix.concat("u"),start+interval);
                queryArgs.setValue(tQueryPrefix.concat("df"),"grammars_zen");
                
                start += interval + 1;
            }
            queryArgs.setValue("q",queryStr.toString());
            String resourceURL = SolrHelper.getDefaultDataSourceURL(context);
            int timeOut = 5000;

            if( args.isValid("timeout") )
                timeOut = args.getInt("timeout");
            
            context.writeLog(1,"args: " + queryArgs.toString());
            
            ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(queryArgs);
            CloudSolrClient server = SolrHelper.getSolrServer(resourceURL, timeOut);

            context.writeLog(1,"datapath: " + resourceURL);
            //check memcached for data if not there then create data with following and add to memcached
            DataBean tCache = SolrHelper.querySolr(server, params, FIELDLIST);
            context.writeLog(1,"Query: " + tCache.toString() + " error: " + tCache.getString("error"));
            result.setValue("resultcount", tCache.getString("numFound"));

            ArrayList<DataBean> entryList = tCache.getCollection("entrylist");
            if (entryList != null)
            {
                resultList = entryList;
                
                if( resultList.size() > 0 ){
                    StringBuffer keywords = new StringBuffer();
                    DataBean target = resultList.get(0);
                    
                    String grammars = target.getString("grammars");
                    grammars = grammars.replaceAll("\\[","").replaceAll("\\]","");
                    
                    for(int i = 0,size = qList.size();i < size;i++){
                        String tTerm = qList.get(i);
                        
                        if( grammars.indexOf(tTerm) < 0 ){
                            if( keywords.length() > 0 ){
                                keywords.append(" ");    
                            }
                            
                            keywords.append(tTerm);
                        }
                    }
                    
                    resultList.get(0).setValue("keywords",keywords.toString());
                    
                    //load details
                    DataBean detailQArgs = new DataBean();
                    detailQArgs.setValue("sentencetype",resultList.get(0).getValue("sentencetype"));
                    GrammarDetailService gService = new GrammarDetailService();
                    
                   
                    DataBean tqCache = gService.getData(context,detailQArgs);
                    result.setValue("resultcount", tqCache.getString("totalCount"));

                    ArrayList<DataBean> detailList = tqCache.getCollection("beanlist");
                    if (detailList != null && detailList.size() > 0 )
                    {
                        transformDetailEntries(detailList);
                        DataBean details = detailList.get(0);
                        
                        if( details.getCollection("responses") != null ){
                            ArrayList<DataBean> itemList = details.getCollection("responses");
                            
                        }
                        
                        resultList.get(0).setStructure("detail",details);
                        context.writeLog(1,"details struct: " + details.toString());
                    }
                    
                    context.writeLog(1,"details query: " + result.toString());
                }
            }
            
            if( server != null ){
                try{
                    server.close();
                }
                catch(Exception e){
                    
                }
            }
            
            result.setValue("totalCount", tCache.getValue("numFound"));
        }

        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    public DataBean putData(RestService context,DataBean args) {
        return( new DataBean() );
    }
    
    public DataBean postData(RestService context,DataBean args) {
        return( new DataBean() );
    }
    
    public DataBean deleteData(RestService context,DataBean args) {
        return( new DataBean() );
    }
    
    public ArrayList<DataBean> transformDetailEntries(ArrayList<DataBean> entries){
        
        for(int i = 0,size = entries.size();i < size;i++){
            DataBean tEntry = entries.get(i);
            if( tEntry.getCollection("responses") != null ){
                ArrayList rList = tEntry.getCollection("responses");
                java.util.Random tRam = new java.util.Random();
                int idx = tRam.nextInt(rList.size());
                
                if( idx > -1 && idx < rList.size() ){
                    String response = replaceResponseVariables(tEntry,rList.get(idx).toString());
                    
                    //tEntry.setValue("responses",response);
                    tEntry.resetCollection("responses");
                    tEntry.addToCollection("responses",response);
                }
            }
            if( tEntry.getCollection("failedresponses") != null ){
                ArrayList rList = tEntry.getCollection("failedresponses");
                java.util.Random tRam = new java.util.Random();
                int idx = tRam.nextInt(rList.size());
                
                if( idx > -1 && idx < rList.size() ){
                    String response = replaceResponseVariables(tEntry,rList.get(idx).toString());
                    
                    //tEntry.setValue("failedresponses",response);
                    tEntry.resetCollection("failedresponses");
                    tEntry.addToCollection("failedresponses",response);
                }
            }
        }
        
        return( entries );
    }
    
    public String replaceResponseVariables(DataBean context,String response){
        String result = response;
        
        result = result.replaceAll(KEYWORDS,context.getString("keywords"));
        
        
        return( result );
    }
    
   

    public static void main(String[] args) {
    	AgentService gs = new AgentService();
        RestServiceStub ctx = new RestServiceStub();
        DataBean initArgs = new DataBean();
        initArgs.setValue("solr","localhost:10983:zen");
        ctx.initialize(initArgs);
        
        DataBean queryArgs = new DataBean();
        queryArgs.setValue("query","who is kevin");
        DataBean foo = gs.getData(ctx,queryArgs);
        System.out.println(foo);
    }
}

package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.Extractor;
import com.jsclosures.RestService;
import com.jsclosures.SolrHelper;

import java.util.ArrayList;

import opennlp.tools.parser.Parser;
import opennlp.tools.sentdetect.SentenceDetectorME;

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
public class ZENService extends SolrService {

    public static String FIELDLIST[][] = {{"id","NUMBER"},
        {"body","STRING"},
        {"terms","STRING"},
        {"contenttype","STRING"},
        {"last_modified","DATE"}};
    
     public static String CONTENTTYPE = "TERMS";
    public static String CONTENTTYPEPREFIX = "TM";
    
    public ZENService(){
        super();
        setContentType(CONTENTTYPE);
        setContentTypePrefix(CONTENTTYPEPREFIX);
        setFieldList(FIELDLIST);
    }
    
    public static String TERMSDOCUMENTFIELDLIST[][] = {
        { "id", "NUMBER","1.0" }, 
        { "contenttype", "STRING","1.0" }, 
        { "body", "STRING","1.0" }, 
        { "terms", "STRING","1.0" }, 
    { "terms_noun", "STRING","1.0" }, 
    { "terms_verb", "STRING","1.0" }, 
    { "terms_adjective", "STRING","1.0" }, 
        { "terms_nn", "STRING","1.0" },
        { "terms_nns", "STRING","1.0" },
        { "terms_nnp", "STRING","1.0" },
        { "terms_nnps", "STRING","1.0" },
        { "terms_np", "STRING","1.0" },
        { "terms_vbz", "STRING","4.0" }, 
        { "terms_vbp", "STRING","1.0" }, 
        { "terms_vb", "STRING","1.0" }, 
        { "terms_vp", "STRING","1.0" },
        { "terms_vbd", "STRING","1.0" }, 
        { "terms_vbg", "STRING","1.0" }, 
        { "terms_vbn", "STRING","1.0" }, 
        { "terms_jj", "STRING","1.0" },
        { "terms_jjr", "STRING","1.0" },
        { "terms_jjs", "STRING","1.0" }, 
        { "terms_rb", "STRING","1.0" },
        { "terms_rbr", "STRING","1.0" },
        { "terms_rbs", "STRING","1.0" },
        { "terms_tk", "STRING","1.0" }
    };
    
    private static Parser parser = null;
    private static SentenceDetectorME detector = null;
    
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
    

        String resourceURL = dataSourceURL;
        context.writeLog(1,"solr server: " + resourceURL);
        int timeOut = 1000;

        if( args.isValid("timeout") )
            timeOut = args.getInt("timeout");
        
        DataBean conf = context.getConfiguration();
        try {

            DataBean input = new DataBean();
            input.addToCollection("sentences",args.getString("question"));
            context.writeLog(1,"Zen question: " + args.getString("question"));
            
            String sentences[] = (String[])input.getCollection("sentences").toArray(new String[input.getCollection("sentences").size()]);
            
            context.writeLog(1,"Zen questions size: " + sentences.length);
            
            String grammarFileName = conf.getString("grammarfile","C:\\Apps\\apache-opennlp-1.5.3\\models\\en-parser-chunking.bin");
            String sentenceFileName = conf.getString("sentencefile","C:\\Apps\\apache-opennlp-1.5.3\\models\\en-sent.bin");
            CloudSolrClient solrServer = SolrHelper.getSolrServer(resourceURL, 1000);
            DataBean defaultArgs = new DataBean();
            Extractor extractor = new Extractor();
            extractor.setConfiguration(conf);
            
            parser = parser == null ? extractor.getParser(grammarFileName) : parser;
            detector = detector == null ? extractor.getSentenceDetector(sentenceFileName) : detector;
            context.writeLog(1,"ONLP: " + parser + " " + detector);
            
            for(int i = 0;i < sentences.length;i++){
                String tmp[] = extractor.parseSentences(detector,sentences[i]);
                context.writeLog(1,"Parser: " + parser);
                
                DataBean newRec = extractor.parse(parser,tmp);
                DataBean solrArgs = SolrHelper.getDefaultArguments(context);
                args.setValue("fq","contenttype:" + CONTENTTYPE);
                args.removeValue("contenttype");
                
                context.writeLog(1,"Do zen query: " + args.toString());
                context.writeLog(1,"query rec: " + newRec.toString());
                
                SolrHelper.readQueryArguments(context, newRec,solrArgs,0);
                ModifiableSolrParams params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
                params.set("fl","score,*");
                params.set("sort","score desc");
                context.writeLog(1,"Query: " + solrArgs.toString());
                
                DataBean queryResult = SolrHelper.querySolr(solrServer, params, FIELDLIST);
                ArrayList entryList = queryResult.getCollection("entrylist");
                if (entryList != null && entryList.size() > 0 ){
                    for(int j = 0,jSize = entryList.size();j < jSize;j++){
                        context.writeLog(1,"Result: " + entryList.get(j).toString());
                        resultList.add(entryList.get(j));
                    }
                }
                else {
                    context.writeLog(1,"sub: " + queryResult.getString("error"));
                    solrArgs = SolrHelper.getDefaultArguments(context);
                    SolrHelper.readQueryArguments(this, newRec,solrArgs,1);
                    params = SolrHelper.getQueryParametersFromURLArguments(solrArgs);
                    params.set("fl","score,*");
                    params.set("sort","score desc");
                    context.writeLog(1,"Sub Query: " + solrArgs.toString());
                    
                    queryResult = SolrHelper.querySolr(solrServer, params,FIELDLIST);
                    entryList = queryResult.getCollection("entrylist");
                    if (entryList != null && entryList.size() > 0 ){
                        for(int j = 0,jSize = entryList.size();j < jSize;j++){
                            context.writeLog(1,"Sub Result: " + entryList.get(j).toString());
                            resultList.add(entryList.get(j));
                        } 
                    }
                }
            }
        }
        catch(Exception e){
            context.writeLog(2,"action failed: " + e.toString());
        }
        
    
        
        result.setValue("totalCount",resultList.size());
        
        
        result.setCollection("beanlist", resultList);

        return (result);
    }
    
    
   

    public static void main(String[] args) {
    	ZENService gs = new ZENService();
        DataBean foo = gs.getData(null, new DataBean());
        System.out.println(foo);
    }
}

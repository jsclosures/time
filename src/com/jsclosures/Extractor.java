package com.jsclosures;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
/**
 * 
terms_CC Coordinating conjunction
terms_CD Cardinal number
terms_DT Determiner
terms_EX Existential there
terms_FW Foreign word
terms_IN Preposition or subordinating conjunction
terms_JJ Adjective
terms_JJR Adjective, comparative
terms_JJS Adjective, superlative
terms_LS List item marker
terms_MD Modal
terms_NN Noun, singular or mass
terms_NNS Noun, plural
terms_NNP Proper noun, singular
terms_NNPS Proper noun, plural
terms_PDT Predeterminer
terms_POS Possessive ending
terms_PRP Personal pronoun
terms_PRP$ Possessive pronoun
terms_RB Adverb
terms_RBR Adverb, comparative
terms_RBS Adverb, superlative
terms_RP Particle
terms_SYM Symbol
terms_TO to
terms_UH Interjection
terms_VB Verb, base form
terms_VBD Verb, past tense
terms_VBG Verb, gerund or present participle
terms_VBN Verb, past participle
terms_VBP Verb, non�3rd person singular present
terms_VBZ Verb, 3rd person singular present
terms_WDT Wh determiner
terms_WP Wh pronoun
terms_WP$ Possessive wh pronoun
terms_WRB Wh adverb
 */

public class Extractor {
    public Extractor() {
        super();
        
       
    }
    
    public static void ssscombine(int level,DataBean rec){
        String others[] = {"terms_nn","terms_nns","terms_nnp","terms_nnps"};
        others = new String[]{"terms_vb","terms_vbz","terms_vbp","terms_vbg","terms_vbd","terms_vbn"};
        others = new String[]{"terms_jj","terms_jjr","terms_jjs"};
        
    }
    
    public void readParse(Parse parse,DataBean context){
        if( parse.getType() != null && parse.getCoveredText() != null ){
            String cType = parse.getType().toLowerCase();
            String cVal = parse.getCoveredText();
            context.append("terms_" + cType,cVal,true);
            
            String global = "";
            
            if( cType.startsWith("n") ){
                context.append("terms_noun",cVal,true);
            }
            else if( cType.startsWith("v") ){
                context.append("terms_verb",cVal,true);
            }
            else if( cType.startsWith("j") ){
                context.append("terms_adjective",cVal,true);
            }
            else if( cType.startsWith("r") ){
                context.append("terms_adverb",cVal,true);
            }
            
            writeLog(1,"parse " + cType + " " + parse.getCoveredText());
        }
    
        if( parse.getChildren() != null && parse.getChildren().length > 0 ){
            for (Parse p : parse.getChildren()){
                readParse(p,context);
            }
        }
    }
    
    public Parser getParser(String grammar) throws InvalidFormatException, IOException {
        InputStream is = new FileInputStream(grammar);
        
        ParserModel model = new ParserModel(is);

        Parser result = ParserFactory.create(model);
    
        is.close();
        
        return( result );
    }
    
    public DataBean parse(String grammar,String sentence[]) throws InvalidFormatException, IOException {

            Parser parser = getParser(grammar);
            
            DataBean result = new DataBean();
        
            for( String s : sentence){
                Parse topParses[] = ParserTool.parseLine(s, parser, 1);
                
                for (Parse p : topParses){
                    readParse(p,result);
                }
            }

            return( result );
    }
    
    public DataBean parse(Parser parser,String sentence[]) throws InvalidFormatException, IOException {
            
            DataBean result = new DataBean();
        
            for( String s : sentence){
                Parse topParses[] = ParserTool.parseLine(s, parser, 1);
                
                for (Parse p : topParses){
                    readParse(p,result);
                }
            }

            return( result );
    }
    
    public SentenceDetectorME getSentenceDetector(String grammar) throws InvalidFormatException, IOException {
        InputStream is = new FileInputStream(grammar);
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME result = new SentenceDetectorME(model);
        is.close();
        
        return( result );
    }
    
    public String[] parseSentences(SentenceDetectorME sentenceDetector,String grammar,String sentence) throws InvalidFormatException, IOException {
                        
            String result[] = sentenceDetector.sentDetect(sentence);
            
            return( result );
    }
    
    public String[] parseSentences(String grammar,String sentence) throws InvalidFormatException, IOException {
            SentenceDetectorME sentenceDetector = getSentenceDetector(grammar);
                        
            String result[] = sentenceDetector.sentDetect(sentence);
            
            return( result );
    }
    
    public String[] parseSentences(SentenceDetectorME sentenceDetector,String sentence) throws InvalidFormatException, IOException {                        
            String result[] = sentenceDetector.sentDetect(sentence);
            
            return( result );
    }
    
    public String collapseSentences(String sentences[]){
        StringBuilder result = new StringBuilder();
        
        for( String s : sentences){
            if( result.length() > 0 )
                result.append(" ");
            result.append(s);
        }
        
        return( result.toString() );
    }
    
    private DataBean configuration = new DataBean();
    public DataBean getConfiguration(){
        return( configuration );
    }
    public void initializeConfiguration(){
        configuration.setValue("solrBinaryServerName","http://localhost:8080/solr/");
        configuration.setValue("solrRESTServerName","http://localhost:8080/solr/update/json");
        configuration.setValue("transportFormat","javabin");
        configuration.setValue("maximumLogLevel","2");
        configuration.setValue("maximumRowCount","10");
    }
    
    public void setConfiguration(String name,String value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(String name,int value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(DataBean conf){
        configuration = conf;
    }
    
    public void writeLog(int level, String message) {
        if (level <= getConfiguration().getInt("maximumloglevel") ) {
            System.out.println(message);
        }
    }
    
    public void processArgs(String[] args) {
        initializeConfiguration();
        
        for (int i = 0; i < args.length; i++) {
                int idx = args[i].indexOf("=");
            
            if( idx > -1 ){
                String name = args[i].substring(0,idx);
                String value = args[i].substring(idx+1);
                setConfiguration(name,value);
            }
        }

        writeLog(1,getConfiguration().toString());
    }
    
    public DataBean readFile(String fileName) {
        DataBean result = new DataBean();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                  new FileInputStream(fileName), "UTF8"));
            
                String line;
                result.setValue("filename",fileName);
                while ((line = br.readLine()) != null) {
                    writeLog(3,line);
                    String newLine = testSentence(line);
                    
                    if( newLine.length() > 0 ){
                        result.addToCollection("sentences",newLine);
                    }
                    else {
                        writeLog(1,"rejected: " + line);
                    }
                }
            br.close();
        } catch (Exception e) {
            writeLog(-1, e.toString());
        }
        
        return( result );
    }
    
    private String ignoreCharacters[] = {">","<",":","\\}","\\{","\\$"};
    private short    ignoreMinimums[] = {1,1,1,1,1,2};
    private String startsWithCharacters[] = {"{","var"};
    private String privatePatterns[][] = {};//{"^AN*","AccountNumber"}};
    
    public String testSentence(String sentence){
        String result = sentence;
        
        for(int i = 0;i < ignoreCharacters.length;i++){
            if( getCountInString(ignoreCharacters[i],sentence) > ignoreMinimums[i] ){
                result = "";
                break;
            }
        }
        
        if( result.length() > 0 ){
            for(int i = 0;i < startsWithCharacters.length;i++){
                if( sentence.startsWith(startsWithCharacters[i]) ){
                    result = "";
                    break;
                }
            } 
        }
        
        if( result.length() > 0 ){
            ArrayList<String> parts = Helper.splitFields(sentence," ");
            if( parts.size() < 3 ){
                result = "";
            }
            else {
                StringBuilder newString = new StringBuilder();
                for(int i = 0,size = parts.size();i < size;i++){
                    if( newString.length() > 0 )
                        newString.append(" ");
                    
                    newString.append(translatePrivate(parts.get(i)));
                }
                result = newString.toString();
            }
        }
        
        return( result );
    }
    
    private String translatePrivate(String token){
        String result = token;
        
        for(int i = 0;i < privatePatterns.length;i++){
            Pattern pattern = Pattern.compile(privatePatterns[i][0]);
            Matcher matcher = pattern.matcher(token);
            if( matcher.find() ) {
                result = privatePatterns[i][1];
                break;
            }
        } 
        
        return( result );
    }
    
    public int getCountInString(String c,String s){
        int result = 0;
        
        Pattern pattern = Pattern.compile("[^" + c + "]*" + c);
        Matcher matcher = pattern.matcher(s);
        while(matcher.find()) {
            result++;
        }
        
        return( result );
    }
}

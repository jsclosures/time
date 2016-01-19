package com.jsclosures;


import java.io.File;
import java.io.FileInputStream;

import java.net.URLEncoder;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import org.json.simple.JSONObject;


public class MessageClient extends DataBean {
    
    private org.cometd.client.BayeuxClient messageClient;
    private org.cometd.bayeux.client.ClientSessionChannel messageChannel;
    private org.cometd.bayeux.client.ClientSessionChannel specialMessageChannel;
    private RestService context;
    private DataBean queryArgs;
    private String key;
    private String user;
    private String channelName = "/esb/messages";
    private String specialChannelName = null;
    private boolean includeMessages = true;
    private ChatListener chatListener;
    
    public MessageClient(RestService context,DataBean queryArgs){
        super();
        this.context = context;
        this.queryArgs = queryArgs;
        this.key = queryArgs.getString("messagekey","72999");
        this.user = queryArgs.getString("user","goodway");
        if( queryArgs.getString("mode","client").equalsIgnoreCase("server") ) {
            this.channelName = queryArgs.getString("messagechannel",channelName);
            this.messageClient = initializeMessageService(context,queryArgs);
            this.messageChannel = messageServiceOpenChannel(context,queryArgs,messageClient,channelName);
            initializeListener();
        }
        else {
            this.includeMessages = context.getConfiguration().getString("includemessages").equalsIgnoreCase("true");
            if( queryArgs.isValid("messagekey") ){
                this.channelName = context.getConfiguration().getString("messagechannel",channelName) + "/" + key;
            }
            else {
                this.channelName = context.getConfiguration().getString("messagechannel",channelName);
            }
            if( queryArgs.isValid("specialmessagechannel") ){
                this.specialChannelName = queryArgs.getString("specialmessagechannel");
            }            
            if( queryArgs.isValid("includemessages") ){
                this.includeMessages = queryArgs.getString("includemessages").equalsIgnoreCase("true");
            }
            
            
            if( this.includeMessages ){
                this.messageClient = initializeMessageService(context,queryArgs);
                this.messageChannel = messageServiceOpenChannel(context,queryArgs,messageClient,channelName);
                if( this.specialChannelName != null ){
                    this.specialMessageChannel = messageServiceOpenChannel(context,queryArgs,messageClient,specialChannelName);
                }
            }
        }
    }
    
    public MessageClient(RestService context,DataBean queryArgs,String channelName){
        super();
        this.context = context;
        this.queryArgs = queryArgs;
        this.key = queryArgs.getString("messagekey","72999");
        this.user = queryArgs.getString("user","goodway");
        this.channelName = channelName;
        
        this.includeMessages = context.getConfiguration().getString("includemessages").equalsIgnoreCase("true");
        
        if( this.includeMessages ){
            this.messageClient = initializeMessageService(context,queryArgs);
            this.messageChannel = messageServiceOpenChannel(context,queryArgs,messageClient,channelName);
        }
    }
    
    public MessageClient(RestService context,DataBean queryArgs,org.cometd.client.BayeuxClient messageClient,org.cometd.bayeux.client.ClientSessionChannel messageChannel){
        super();
        
        this.context = context;
        this.queryArgs = queryArgs;
        this.messageClient = messageClient;
        this.messageChannel = messageChannel;
    }
    
    public void initializeListener(){
        this.chatListener = new ChatListener();
        
        messageChannel.subscribe(chatListener);
    }
    
    public void sendMessage(String action,String target,DataBean message){
        if( this.includeMessages ){
            messageServicePublishChannel(context,queryArgs,messageChannel,key,action,target,user,channelName,message);
        }
    }
    
    public void sendSpecialMessage(String action,String target,String user,String email,DataBean message){
        if( this.includeMessages ){
            messageServicePublishChannel(context,queryArgs,specialMessageChannel,key,action,target,user,email,specialChannelName,message);
        }
    }
    
    public void sendPrivateMessage(String action,String target,DataBean message,String text){
        if( this.includeMessages ){
            messageServicePublishPrivateChannel(context,queryArgs,messageChannel,key,action,target,user,channelName,message,text);
        }
    }
    
    public void sendUserMessage(String userChannelName,String action,String target,DataBean message,String text){
        if( this.includeMessages ){
            org.cometd.bayeux.client.ClientSessionChannel userChannel = messageServiceOpenChannel(context,queryArgs,messageClient,"/esb/" + userChannelName);
            messageServicePublishChannel(context,queryArgs,userChannel,key,action,target,user,userChannelName,message);
            userChannel.release();
        }
    }
    
    public void destroy(){
        if( this.messageChannel != null ){
            this.messageChannel.unsubscribe();
            this.messageChannel.release();
            this.messageChannel = null;
        }
        if( this.specialMessageChannel != null ){
            this.specialMessageChannel.unsubscribe();
            
            this.specialMessageChannel.release();
            this.specialMessageChannel = null;
        }
        if( this.includeMessages ){
            //finalizeMessageService(context,queryArgs,messageClient);
            Timer timer = new Timer();
            timer.schedule(new DestroyTask(context,queryArgs,messageClient), 5*1000);
        
            this.messageClient = null;
            this.messageChannel = null;
            this.specialMessageChannel = null;
        }
    }
    
    
    class DestroyTask extends TimerTask {
        private RestService context;
        private DataBean args;
        private org.cometd.client.BayeuxClient client;
        
        public DestroyTask(RestService context,DataBean args,org.cometd.client.BayeuxClient client){
            this.context = context;
            this.args = args;
            this.client = client;
        }
            public void run() {
                finalizeMessageService(context,args,client);
            }
        }
    
    public static DataBean loadAuthorizationKey(RestService context,String key,String secret){
        DataBean result = new DataBean();
        
        try {
            String authURL = context.getConfiguration().getString("authurl","https://esb.goodwaygroup.com/TradeDeskWeb/authservice");
            authURL += "?userkey=" + secret + "&username=" + key;
            
            //HttpGet httpget = new HttpGet(authURL);
            //HttpClient client = new CloseableHttpClient();
            //HttpResponse response = client.execute(httpget);//getHttpClient(context).execute(httpget);
            
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(authURL);
            HttpResponse response = httpclient.execute(httpGet);
            
            HttpEntity rspEntity = response.getEntity();
            String rspStr = EntityUtils.toString(rspEntity);
            context.writeLog(1,"auth json: " + rspStr);
            result = Helper.parseJSON(rspStr);
            
            if( result.getCollection("items") != null && result.getCollection("items").size() > 0 ){
                result = (DataBean)result.getCollection("items").get(0);
                result.setValue("messagekey",result.getValue("id"));
            }
            context.writeLog(1,"auth response: " + result.toString());
        }
        catch(Exception e){
            result.setValue("error",e.toString());
            context.writeLog(1,"auth error: " + e.toString());
        }
        
        return( result );
    }
    
    public static CloseableHttpClient getHttpClient(RestService context){
            
             CloseableHttpClient httpclient = null;
            boolean certIsLoaded = false;
            Certificate cert = null;
            DataBean data = new DataBean();
            data.setValue("cert","");
            data.setValue("alias","");
            
            try{
            if (true) {
                context.writeLog(1, "Using SSL ");
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                 String certPath = System.getenv("JAVA_HOME") + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
                context.writeLog(1, "Cert Path: "+certPath);
                File certFile = new File(certPath);
               // File certFile = new File("C:\\Java\\jdk1.7.0_25\\jre\\lib\\security\\cacerts");
                File pcert = new File(System.getenv("JAVA_HOME") + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + data.getString("cert"));
                
                if (certFile.exists()) {
                    FileInputStream instream = new FileInputStream(certFile);
                    FileInputStream pinstream = new FileInputStream(pcert);
                    
                    try {
                        trustStore.load(instream, "changeit".toCharArray());
                        certIsLoaded = true;
                        if(trustStore.getCertificate(data.getString("alias")) == null){
                            
                         CertificateFactory cf = CertificateFactory.getInstance("X.509");
                         cert = (X509Certificate)cf.generateCertificate(pinstream);
                         trustStore.setCertificateEntry(data.getString("alias"), cert);
                         context.writeLog(1,"Loaded Custom cert..."+data.getString("cert"));
                        }
                        context.writeLog(1,"Cert is loaded...");
                    }catch(Exception el){
                        el.printStackTrace();
                    } finally {
                        instream.close();
                        
                    }

                    // Trust own CA and all self-signed certs
                    SSLContext sslcontext = SSLContexts.custom()
                            .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                            .build();
                    // Allow TLSv1 protocol only
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                            sslcontext,
                            new String[]{"TLSv1"},
                            null,
                            SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .build();

                } else {
                    context.writeLog(1, "No CERT file found " + certFile.getAbsolutePath());
                }

            } else {
                context.writeLog(1, "Using standard HTTP");
                httpclient = HttpClients.createDefault();
            }
            } catch(Exception e){
                e.printStackTrace();
            }
            return httpclient;
        }
    
    public static org.cometd.client.BayeuxClient initializeMessageService(RestService context,DataBean args){
            // Handshake
            org.cometd.client.BayeuxClient client = null;
            
             try {
                     String url = context.getConfiguration() != null ? context.getConfiguration().getString("messageserver") : "http://localhost/TradeDeskWeb/cometd";
                     Helper.writeLog(1,"message server url: " + url);
                 
                     client = new org.cometd.client.BayeuxClient(url, org.cometd.client.transport.LongPollingTransport.create(null));
                     
                     client.getChannel(org.cometd.bayeux.Channel.META_HANDSHAKE).addListener
                (new org.cometd.bayeux.client.ClientSessionChannel.MessageListener() {

                public void onMessage(org.cometd.bayeux.client.ClientSessionChannel channel, org.cometd.bayeux.Message message) {

                    System.out.println("[CHANNEL:META_HANDSHAKE]: " + message);

                    boolean success = message.isSuccessful();
                    if (!success) {
                        String error = (String) message.get("error");
                        if (error != null) {
                            Helper.writeLog(1,"Error during HANDSHAKE: " + error);
                            //System.out.println("Exiting...");
                            //System.exit(1);
                        }

                        Exception exception = (Exception) message.get("exception");
                        if (exception != null) {
                            Helper.writeLog(1,"Exception during HANDSHAKE: ");
                            exception.printStackTrace();
                            //System.out.println("Exiting...");
                            //System.exit(1);

                        }
                    }
                    else {
                             //Helper.messageServicePublishChannel(context,args,client,"/zen/app","connected to channel");
                            System.out.println("Success during HANDSHAKE: ");
                    }
                }

            });

                     client.getChannel(org.cometd.bayeux.Channel.META_CONNECT).addListener
                (new org.cometd.bayeux.client.ClientSessionChannel.MessageListener() {

                public void onMessage(org.cometd.bayeux.client.ClientSessionChannel channel, org.cometd.bayeux.Message message) {

                   Helper.writeLog(1,"[CONNECT]: " + message);

                    boolean success = message.isSuccessful();
                    if (!success) {
                        String error = (String) message.get("error");
                        if (error != null) {
                            Helper.writeLog(1,"Error during CONNECT: " + error);
                            Helper.writeLog(1,"Exiting...");
                            //System.exit(1);
                        }

                        Exception exception = (Exception) message.get("exception");
                        if (exception != null) {
                            Helper.writeLog(1,"Exception during CONNECT: ");
                            exception.printStackTrace();
                            Helper.writeLog(1,"Exiting...");
                            //System.exit(1);

                        }
                    }
                    else {
                             //Helper.messageServicePublishChannel(context,args,client,"/zen/app","connected to channel");
                            Helper.writeLog(1,"Success during CONNECT: ");
                    }
                }

            });
                     
                     client.handshake();
                     client.waitFor(1000, org.cometd.client.BayeuxClient.State.CONNECTED);

             }
             catch(Exception e){
                     Helper.writeLog(1,"Cometd Exception: " + e.toString());
             }
             
             return( client );
    }
    
    public static org.cometd.client.BayeuxClient finalizeMessageService(RestService context,DataBean args,org.cometd.client.BayeuxClient client){

            if( client != null ){
                    try {
                            client.disconnect(10000);
                            client.waitFor(10000, org.cometd.client.BayeuxClient.State.DISCONNECTED);
                        
                        Helper.writeLog(1,"Cometd disconnect complete");
                    }
                     catch(Exception e){
                             Helper.writeLog(1,"Cometd disconnect Exception: " + e.toString());
                     }
            }
            else 
                    Helper.writeLog(1,"Cometd invalid client");
            
            return( client );
    }
    
    public static org.cometd.bayeux.client.ClientSessionChannel messageServiceOpenChannel(RestService context,DataBean args,org.cometd.client.BayeuxClient client,String channelNameNew){
            Helper.writeLog(1,"js open channel " + channelNameNew);
            
            org.cometd.bayeux.client.ClientSessionChannel channel = client != null ? client.getChannel(channelNameNew) : null;
            
            return( channel );
    }
    
    public static org.cometd.bayeux.client.ClientSessionChannel messageServicePublishPrivateChannel(RestService context,DataBean args,org.cometd.bayeux.client.ClientSessionChannel channel,String key,String action,String target,String user,String channelName,DataBean message,String text){
             // Publishing to channels
             if( channel != null ){
                 java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();
                 /*data.put("action",action);
                 data.put("target",target);
                 data.put("key",key);
                 
                 Map params = message.getValues();
                 if( params != null )
                    data.put("params",params);*/
                 data.put("user",user);
                 data.put("channel",channelName);
                 StringBuffer json = new StringBuffer("{\"action\": \"" + action + "\",\"target\":\"" + target + "\",\"key\":\"" + key + "\",\"notify\": true,\"email\": \"" + user + "\",\"status\": 1,\"message\": \"" + JSONObject.escape(text) + "\"");
                 
                 /*Map params = message.getValues();
                 if( params != null ){
                     params.remove("action");
                     params.remove("target");
                     params.remove("key");
                     
                    Iterator tParams = params.keySet().iterator();
                    StringBuffer pBuffer = new StringBuffer(",\"params\": [");
                    int counter = 0;
                    try {
                    while( tParams.hasNext() ){
                        String tKey = tParams.next().toString();
                        String tVal = params.get(tKey) != null ? params.get(tKey).toString().trim() : "";
                        if( counter > 0 )
                            pBuffer.append(",");
                        pBuffer.append("{\"name\": \"" + URLEncoder.encode(tKey,"UTF-8") + "\",\"value\": \"" + URLEncoder.encode(tVal,"UTF-8") + "\"}");
                        counter++;
                    }
                    }
                    catch(Exception e){
                        context.writeLog(1,e.toString());
                    }
                    
                     pBuffer.append("]");
                     json.append(pBuffer.toString());
                 }*/
                 
                 json.append("}");
                 
                 data.put("text",json);
                 
                 channel.publish(data);
                 
                 Helper.writeLog(1,"Message Data " + json);
                 Helper.writeLog(1,"Cometd sent channel data " + channel.getId());
             }
             else 
                Helper.writeLog(1,"Cometd invalid channel");
             
            return( channel );
    }
    public static org.cometd.bayeux.client.ClientSessionChannel messageServicePublishChannel(RestService context,DataBean args,org.cometd.bayeux.client.ClientSessionChannel channel,String key,String action,String target,String user,String channelName,DataBean message){
        return( messageServicePublishChannel(context,args,channel,key,action,target,user,null,channelName,message) );
    }
    
    public static org.cometd.bayeux.client.ClientSessionChannel messageServicePublishChannel(RestService context,DataBean args,org.cometd.bayeux.client.ClientSessionChannel channel,String key,String action,String target,String user,String email,String channelName,DataBean message){
             // Publishing to channels
             if( channel != null ){
                 java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();
                 /*data.put("action",action);
                 data.put("target",target);
                 data.put("key",key);*/
                 
                 Map params = message.getValues();
                 if( params != null )
                    data.put("params",params);
                 data.put("user",user);
                 data.put("channel",channelName);
                 String uid = UUID.randomUUID().toString();
                 data.put("sequence",uid);
                 if( email != null ){
                     data.put("email",email);
                 }
                 context.writeLog(1,"Sending Message: " + action + ": " + user);
                 StringBuffer json = new StringBuffer("{\"action\": \"" + action + "\",\"target\":\"" + target + "\",\"key\":\"" + key + "\",\"sequence\":\"" + uid + "\"");
                 
                 //Map params = message.getValues();
                 if( params != null ){
                     params.remove("action");
                     params.remove("target");
                     params.remove("key");
                     
                    Iterator tParams = params.keySet().iterator();
                    StringBuffer pBuffer = new StringBuffer(",\"params\": [");
                    int counter = 0;
                    try {
                    while( tParams.hasNext() ){
                        String tKey = tParams.next().toString();
                        String tVal = params.get(tKey) != null ? params.get(tKey).toString().trim() : "";
                        //data.put(tKey,tVal);
                        if( counter > 0 )
                            pBuffer.append(",");
                        pBuffer.append("{\"" +  URLEncoder.encode(tKey,"UTF-8") + "\": \"" + URLEncoder.encode(tVal,"UTF-8") + "\"}");
                        counter++;
                    }
                    }
                    catch(Exception e){
                        context.writeLog(1,e.toString());
                    }
                    
                     pBuffer.append("]");
                     json.append(pBuffer.toString());
                 }
                 
                 json.append("}");
                 
                 data.put("text",json);
                 
                 channel.publish(data);
                 context.writeLog(1,"Message Data " + action + ": " + json);
                 context.writeLog(1,"Cometd sent channel data " + channel.getId());
             }
             else 
                context.writeLog(1,"Cometd invalid channel");
             
            return( channel );
    }
    
    public interface MessageHandler {
        public void handleMessage(DataBean data);
    }
    
    private ArrayList<MessageHandler> handlers;
    
    public void addHandler(MessageHandler handler){
        if( handlers == null ){
            handlers = new ArrayList<>();
        }
        
        handlers.add(handler);
    }
    
    
    public class ChatListener implements ClientSessionChannel.MessageListener {

            public final static String ANON_USER = "anon";
            public final static String NO_MESSAGE = "no message!";
            public final static String NO_CHANNEL = "no channel!";
            private HashMap<String, String> registry = null;

            public void onMessage(ClientSessionChannel channel, Message message) {
                Map<String, Object> data = message.getDataAsMap();
                // String fromUser = (String) data.get("user");
                // String text = (String) data.get("chat");
                
                if (registry == null || registry.size() > 100000) {
                    registry = new HashMap<>();
                }
                
                boolean isUnique = true;

                if (data.get("uid") != null && registry.get((String) data.get("uid")) == null) {
                    registry.put((String) data.get("uid"), (String) data.get("user"));
                    
                } else if(registry.get((String) data.get("uid")) != null){
                    isUnique = false;
                }
                String fromUser = ANON_USER;
                String text = NO_MESSAGE;
                String channelName = NO_CHANNEL;
                String responseChannel = null;
                if ((String) data.get("user") != null) {
                    fromUser = (String) data.get("user");
                }

                if ((String) data.get("text") != null) {
                    text = (String) data.get("text");
                }

                if ((String) data.get("chat") != null) {
                    text = (String) data.get("chat");
                }

                if (data.get("channel") != null) {
                    channelName = (String) data.get("channel");
                }

                if (data.get("response_channel") != null) {
                    channelName = (String) data.get("channel");
                }
                //System.err.printf("%s: %s%n", fromUser, text);
                //ESBLog.log(MessagingClient.ChatListener.class, "Message on channel: " + channelName + " From [" + fromUser + "] " + text);
                // client.getChannel("/esb/messages").publish("I will "+text);
                //  sendSingleTo("I will "+text, "/esb/messages", "GESB Response");

                if (handlers != null
                        && !text.equals(NO_MESSAGE)
                        && !fromUser.equals(ANON_USER) 
                        && isUnique) {
                    DataBean tData = new DataBean();
                    Iterator keys = data.keySet().iterator();
                    while( keys.hasNext() ){
                        String tKey = keys.next().toString();
                        tData.setValue(tKey,data.get(tKey));
                    }
                    for (int i = 0; i < handlers.size(); i++) {
                        handlers.get(i).handleMessage(tData);
                    }
                } else {
                    if(!isUnique){
                         //ESBLog.log(getClass(), " ** NOT invoking handler chain on NON-UNIQUE MESSAGE: ..."+data.get("uid"));
                    } else {
                         //ESBLog.log(getClass(), " ** NOT invoking handler chain...");
                    }
                }
            }
        }
}
package com.jsclosures;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;


/**
A generalized utility class that provides easy to operations

 */

public class Helper extends DataBean {

    public Helper() {
        super();
    }

    public static  String postContent(String theURL, String type,DataBean map) {
            String result = "";
            HttpPost post = null;
            HttpClient client = null;
            try {
                client = new DefaultHttpClient();
                post = new HttpPost(theURL);
            
                    /*StringEntity reqEntity = new StringEntity(Helper.toJson(map));
                    reqEntity.setContentType("application/json");
                    post.setEntity(reqEntity);*/
                
                    //Enumeration innerColumNameList = map.getColumnNames();
                    //List<NameValuePair> nameValuePairs = new ArrayList<>();  
                //nameValuePairs.add(new BasicNameValuePair("type",type));
                
                    //if( innerColumNameList != null )
                    //{
                        //String tKey;
                        //while( innerColumNameList.hasMoreElements() )
                        //{
                                //tKey = innerColumNameList.nextElement().toString();
                                //nameValuePairs.add(new BasicNameValuePair(tKey, map.getString(tKey)));
                        //}
                    //}
                    String json = Helper.toJson(map);
                    writeLog(1,"json: " + json);
                
                    StringEntity requestEntity = new StringEntity(
                    json,
                    "application/json",
                    "UTF-8");
                post.setEntity(requestEntity);
                
                    //nameValuePairs.add(new BasicNameValuePair("json",json));
                     //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                   
                    HttpResponse response = client.execute(post);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result += line;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                 if(client != null){
                    client =  null;
                }
                if (post != null) {
                    post.releaseConnection();
                    post = null;
                }
            }
            return result;
        }
    /**
     *  field the query result by a certain field in the result set (searchName)
     * @param queryArgs   [sortfield:asc]   [start:0] [rows: 10]
     * @param result
     * @param searchName
     */
    public static void filterResponse(DataBean queryArgs, DataBean result, String searchName) {

        ArrayList<DataBean> dataList = result.getCollection("beanlist");

        result.setValue("totalCount", dataList.size());

        //sort it before reducing the size
        if (queryArgs.isValid("sortfield")) {
            if (queryArgs.getString("sortdirection").equalsIgnoreCase("asc"))
                QuickSort.sortAscending(dataList, queryArgs.getString("sortfield"));
            else
                QuickSort.sortDescending(dataList, queryArgs.getString("sortfield"));
        }


        //search them
        String name = queryArgs.getString(searchName).toUpperCase();
        if (name.length() > 0) {
            for (int i = dataList.size() - 1; i >= 0; i--) {
                if (!dataList.get(i).getString(searchName).toUpperCase().startsWith(name))
                    dataList.remove(i);
            }
        }

        if (queryArgs.isValid("start") && queryArgs.isValid("rows")) {
            ArrayList<DataBean> shortList = new ArrayList<DataBean>();
            for (int i = queryArgs.getInt("start"), size =
                 Math.min(queryArgs.getInt("start") + queryArgs.getInt("rows"), dataList.size()); i < size; i++) {
                shortList.add(dataList.get(i));
            }

            result.setCollection("beanlist", shortList);
        }
    }

    public static void concatLists(ArrayList<DataBean> dest, ArrayList<DataBean> src) {
        //for(int i = 0,size = src.size();i < size;i++)
        //     dest.add(src.get(i));

        dest.addAll(src);
    }


    public static boolean DEBUG = true;

    public static void writeLog(int level, String message) {
        if (DEBUG) {
            if (message == null) {
                message = "null message";
            }

            System.out.println(message);

            if (true) {
                try {
                    //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("C:\\temp\\rest.txt",true);
                    //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("/tmp/rest.txt",true);
                    java.io.FileOutputStream errorLog =
                        new java.io.FileOutputStream(File.pathSeparator.equalsIgnoreCase(";") ? "c:\\temp\\rest.txt" :
                                                     "/tmp/rest.txt", true);

                    java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(errorLog));

                    bw.write(message, 0, message.length());
                    bw.write("\n");
                    bw.close();

                    errorLog.close();
                } catch (java.io.IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
    }


    public static int parseInt(String v) {
        int result = -1;

        try {
            result = Integer.parseInt(v);
        } catch (Exception e) {

        }
        return (result);
    }

    public static ArrayList<String> splitFields(String sSrc, String sDelim) {
        ArrayList<String> retval = new ArrayList<String>();
        int nd = sDelim.length();
        int ns = sSrc.length();
        int i = 0;
        int j = 0;
        boolean bf = false;
        char q = '\"';

        if (sDelim.length() != 0 && ns != 0) {
            while (j + nd <= ns) {
                if (sSrc.charAt(j) == q) {
                    bf = !bf;

                }
                if (sSrc.substring(j, j + nd).equals(sDelim) && !bf) {
                    retval.add(sSrc.substring(i, j));
                    j += nd;
                    i = j;
                } else {
                    j++;
                }
            }
            retval.add(sSrc.substring(i));
        }

        return retval;
    }

    public static ArrayList<String> simpleSplitFields(String sSrc, String sDelim) {
        ArrayList<String> retval = new ArrayList<String>();
        int nd = sDelim.length();
        int ns = sSrc.length();
        int i = 0;
        int j = 0;
        boolean bf = false;
        char q = '\"';

        if (sDelim.length() != 0) {
            while (j + nd <= ns) {
                if (sSrc.substring(j, j + nd).equals(sDelim)) {
                    retval.add(sSrc.substring(i, j));
                    j += nd;
                    i = j;
                } else {
                    j++;
                }
            }
            retval.add(sSrc.substring(i));
        }

        return retval;
    }

    public static DataBean readFromRESTURL(String url) {
        String json = readFromURL(url);


        return (parseJSON(json));
    }

    public static String readFromURL(String url) {
        StringBuffer result = new StringBuffer();
        try {
            URL u = new URL(url);
            InputStream in = u.openStream();
            InputStreamReader uin = new InputStreamReader(in);
            BufferedReader fin = new BufferedReader(uin);

            String buffer;
            while ((buffer = fin.readLine()) != null) {
                result.append(buffer);

            }

            uin.close();
            in.close();
        } catch (IOException e) {
            result.append("Error: " + e.toString() + " " + e.getMessage());
        }
        return (result.toString());
    }


    public static ArrayList<DataBean> cloneList(ArrayList<DataBean> src) {
        ArrayList<DataBean> result = new ArrayList<DataBean>(src.size());

        for (int i = 0, size = src.size(); i < size; i++)
            result.add(src.get(i));

        return (result);
    }

    /**
     *use to include the default
     * version=1.0
     * @param context
     * @return
     */
    public static DataBean getDefaultArguments(RestService context, HttpServletRequest req) {
        DataBean args = new DataBean();
        args.setValue("version", "1.0");

        return (args);
    }

    public static DataBean readAllParameters(RestService context, HttpServletRequest req) {
        DataBean result = new DataBean();

        Enumeration pList = req.getParameterNames();
        String tKey;

        while (pList.hasMoreElements()) {
            tKey = pList.nextElement().toString();

            writeLog(2, "found key " + tKey + " v: " + req.getParameter(tKey));

            result.setValue(tKey, req.getParameter(tKey));
        }

        return (result);
    }

    public static String readPut(HttpServletRequest req) {
        StringBuffer result = new StringBuffer();

        try {
            InputStream in = req.getInputStream();
            DataInputStream din = new DataInputStream(in);
            int dSize = in.available();
            int size;
            int blockSize = 1024;

            byte buffer[] = new byte[blockSize];


            int i;
            for (;;) {
                size = din.read(buffer, 0, blockSize);

                for (i = 0; i < size; i++) {
                    result.append((char) buffer[i]);

                }
                if (size < 0) {
                    break;
                }
            }
        } catch (Exception e) {
            writeLog(1, "Read Error: " + e.toString());

        }

        return (result.toString());
    }

    public static DataBean parseJSON(String json) {
        DataBean result = new DataBean();

        try {
            JSONObject obj = new JSONObject(json);
            result = parseJSON(obj);
        } catch (Exception e) {
            result.setValue("error", "parse error: " + e.toString());
            //writeLog(1, result.getString("error"));
        }

        return (result);
    }


    public static DataBean parseJSON(JSONObject json) throws Exception {

        DataBean result = new DataBean();
        //writeLog(1,"internal parse:");

        String fNames[] = JSONObject.getNames(json);
        for (int i = 0; i < fNames.length; i++) {
            Object currentObject = ((JSONObject) json).get(fNames[i]);
            //writeLog(1,"fcolumn: " + fNames[i] + " instance of: " + currentObject.getClass().getName());

            if (currentObject instanceof String || currentObject instanceof JSONString ||
                currentObject instanceof Integer || currentObject instanceof Float || currentObject instanceof Double ||
                currentObject instanceof Boolean) {
                result.setValue(fNames[i], currentObject.toString());
                //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
            } else if (currentObject instanceof JSONArray) {

                JSONArray currentArray = (JSONArray) currentObject;
                for (int j = 0, jsize = currentArray.length(); j < jsize; j++) {
                    if (currentArray.get(j) instanceof String || currentArray.get(j) instanceof JSONString) {
                        result.addToCollection(fNames[i], currentArray.get(j).toString());
                        //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
                    } else
                        result.addToCollection(fNames[i], parseJSON((JSONObject) currentArray.get(j)));
                }
            } else if (currentObject instanceof JSONObject) {
                result.setStructure(fNames[i], parseJSON((JSONObject) currentObject));
            }

        }
        
        return (result);
    }

    public static ArrayList parseJsonArray(String json){
        DataBean tempStructure = parseJSON("{\"list\":" + json + "}");
        
        ArrayList result = tempStructure.getCollection("list");
        
        return( result );
    }
    
    public static DataBean readAllJSONParameters(RestService context, HttpServletRequest req) {
        String jsonData = Helper.readPut(req);
        writeLog(1, "read all json data: " + jsonData.getClass().getName() + " json: " + jsonData);
        if (jsonData.getClass().getName().endsWith("String"))
            return (parseJSON(jsonData));
        else
            return (parseJSON(jsonData));
    }

    public static DataBean readJSONFromString(String jsonData) {
        DataBean result = new DataBean();


        try {
            writeLog(1, "parseing json");
            JSONObject obj = new JSONObject(jsonData);
            String fNames[] = obj.getNames(obj);

            for (int i = 0; i < fNames.length; i++) {
                result.setValue(fNames[i], (String) obj.get(fNames[i]));
            }
        } catch (Exception e) {
            writeLog(2, "json parse failed: " + e.toString());
        }
        writeLog(1, "after parsing json: " + result.toString());
        return (result);
    }

    /**
     *partially match from the beginning of a parameter name
     * @param context
     * @param name
     * @return
     */
    public static String matchParameterName(RestService context, HttpServletRequest req, String name) {
        String result = null;

        Enumeration pList = req.getParameterNames();
        String tKey;

        while (pList.hasMoreElements()) {
            tKey = pList.nextElement().toString();
            if (tKey.toUpperCase().startsWith(name.toUpperCase())) {
                result = tKey;
                break;
            }
        }
        return (result);
    }

    /**
     *read the sort order set by the ui control for sorting of results
     * it looks for the pattern  sort(<columnname>[+-])
     * @param context
     * @param args
     */
    public static void readSortArguments(RestService context, HttpServletRequest req, DataBean args) {
        String sortParameterName = matchParameterName(context, req, "sort(");
        context.writeLog(1, "sortParameterName: " + sortParameterName);
        if (sortParameterName != null) {
            String sortStr = sortParameterName;
            String direction = sortStr.substring("sort(".length(), "sort(".length() + 1);
            String fieldName = sortStr.substring("sort(".length() + 1);
            fieldName = fieldName.substring(0, fieldName.indexOf(")"));


            String sortField = fieldName;
            String sortDirection = "ascending";
            if (direction.equals("-"))
                sortDirection = "descending";


            args.setValue("sortfield", sortField);
            args.setValue("sortdirection", sortDirection);
        } else if (args.isValid("sort")) {
            args.setValue("sortfield", args.getValue("sort"));
            args.setValue("sortdirection", args.getString("sortdirection", "asc"));
        }
    }

    /**
     *Read the paging (start,count) variables from the request
     * for dojo this in the http header as the "Range" header.
     * @param context
     * @param args
     */
    public static void readPagingArguments(RestService context, HttpServletRequest req, DataBean args) {
        String start = args.getString("start", "0");
        String rows = args.getString("rows", "25");
        String range = req.getHeader("Range");
        context.writeLog(1, "range: " + req.getHeader("Range"));

        if (range != null && range.length() > 0 && range.indexOf("items=") == 0) {
            //items=0-24
            String subRange = range.substring("items=".length());

            context.writeLog(1, "subrange: " + subRange);
            int idx = subRange.indexOf("-");

            if (idx > -1) {
                start = subRange.substring(0, idx);
                rows = subRange.substring(idx + 1);
            }
        }


        context.writeLog(1, "paging: " + start + " " + rows);
        args.setValue("start", start);
        args.setValue("rows", rows);
    }


    public static int indexOf(ArrayList<DataBean> list, String column, String value) {
        int result = -1;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getString(column).equalsIgnoreCase(value)) {
                result = i;

                break;
            }

        }

        return (result);
    }


    public static int indexOf(ArrayList<DataBean> list, String column1, String value1, String column2, String value2) {
        int result = -1;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getString(column1).equalsIgnoreCase(value1) &&
                list.get(i).getString(column2).equalsIgnoreCase(value2)) {
                result = i;

                break;
            }

        }

        return (result);
    }


    public static org.cometd.client.BayeuxClient initializeMessageService(RestService context, DataBean args) {
        // Handshake
        org.cometd.client.BayeuxClient client = null;

        try {
            String url =
                context.getConfiguration() != null ? context.getConfiguration().getString("messageserver") :
                "http://localhost/queryui/cometd";

            client =
                new org.cometd.client.BayeuxClient(url, org.cometd.client.transport.LongPollingTransport.create(null));

            client.getChannel(org.cometd.bayeux.Channel.META_HANDSHAKE).addListener(new org.cometd.bayeux.client.ClientSessionChannel.MessageListener() {

                public void onMessage(org.cometd.bayeux.client.ClientSessionChannel channel,
                                      org.cometd.bayeux.Message message) {

                    System.out.println("[CHANNEL:META_HANDSHAKE]: " + message);

                    boolean success = message.isSuccessful();
                    if (!success) {
                        String error = (String) message.get("error");
                        if (error != null) {
                            System.out.println("Error during HANDSHAKE: " + error);
                            //System.out.println("Exiting...");
                            //System.exit(1);
                        }

                        Exception exception = (Exception) message.get("exception");
                        if (exception != null) {
                            System.out.println("Exception during HANDSHAKE: ");
                            exception.printStackTrace();
                            //System.out.println("Exiting...");
                            //System.exit(1);

                        }
                    } else {
                        //Helper.messageServicePublishChannel(context,args,client,"/zen/app","connected to channel");
                        System.out.println("Success during HANDSHAKE: ");
                    }
                }

            });

            client.getChannel(org.cometd.bayeux.Channel.META_CONNECT).addListener(new org.cometd.bayeux.client.ClientSessionChannel.MessageListener() {

                public void onMessage(org.cometd.bayeux.client.ClientSessionChannel channel,
                                      org.cometd.bayeux.Message message) {

                    System.out.println("[CONNECT]: " + message);

                    boolean success = message.isSuccessful();
                    if (!success) {
                        String error = (String) message.get("error");
                        if (error != null) {
                            System.out.println("Error during CONNECT: " + error);
                            System.out.println("Exiting...");
                            //System.exit(1);
                        }

                        Exception exception = (Exception) message.get("exception");
                        if (exception != null) {
                            System.out.println("Exception during CONNECT: ");
                            exception.printStackTrace();
                            System.out.println("Exiting...");
                            //System.exit(1);

                        }
                    } else {
                        //Helper.messageServicePublishChannel(context,args,client,"/zen/app","connected to channel");
                        System.out.println("Success during CONNECT: ");
                    }
                }

            });

            client.handshake();
            client.waitFor(1000, org.cometd.client.BayeuxClient.State.CONNECTED);

        } catch (Exception e) {
            context.writeLog(1, "Cometd Exception: " + e.toString());
        }

        return (client);
    }

    public static org.cometd.client.BayeuxClient finalizeMessageService(RestService context, DataBean args,
                                                                        org.cometd.client.BayeuxClient client) {

        if (client != null) {
            try {
                client.disconnect();
                client.waitFor(1000, org.cometd.client.BayeuxClient.State.DISCONNECTED);

                context.writeLog(1, "Cometd disconnect complete");
            } catch (Exception e) {
                context.writeLog(1, "Cometd disconnect Exception: " + e.toString());
            }
        } else
            context.writeLog(1, "Cometd invalid client");

        return (client);
    }

    public static org.cometd.bayeux.client.ClientSessionChannel messageServiceOpenChannel(RestService context,
                                                                                          DataBean args,
                                                                                          org.cometd.client.BayeuxClient client,
                                                                                          String channelName) {
        org.cometd.bayeux.client.ClientSessionChannel channel = client != null ? client.getChannel(channelName) : null;

        return (channel);
    }

    public static org.cometd.bayeux.client.ClientSessionChannel messageServicePublishChannel(RestService context,
                                                                                             DataBean args,
                                                                                             org.cometd.bayeux.client.ClientSessionChannel channel,
                                                                                             String key, String type,
                                                                                             String tag,
                                                                                             String message) {
        // Publishing to channels
        if (channel != null) {
            java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();
            data.put("alias", args.getString("user"));
            data.put("type", type);
            data.put("key", key);
            data.put(tag, message);
            channel.publish(data);

            context.writeLog(1, "Cometd sent channel data " + channel.getId());
        } else
            context.writeLog(1, "Cometd invalid channel");

        return (channel);
    }

    public static org.cometd.client.BayeuxClient messageServicePublishChannel(RestService context, DataBean args,
                                                                              org.cometd.client.BayeuxClient client,
                                                                              String channelName, String key,
                                                                              String type, String tag, String message) {
        // Publishing to channels
        if (client != null) {
            java.util.Map<String, Object> data = new java.util.HashMap<String, Object>();

            data.put("alias", args.getString("user"));
            data.put("type", type);
            data.put("key", key);
            data.put(tag, message);
            client.getChannel(channelName).publish(data);

            context.writeLog(1, "Cometd sent channel data " + client.getId());
        } else
            context.writeLog(1, "Cometd invalid client");

        return (client);
    }


    public static String generateKeyWithPrefix(String prefix) {
        long hash = 0;
        String value = String.valueOf(Calendar.getInstance().getTime().getTime());

        for (int i = 0; i < value.length(); i++) {
            hash = 32 * hash + value.charAt(i);
        }

        return (prefix + String.valueOf(hash));
    }

    public static String serializeData(RestService context, ArrayList beanList, ArrayList columnNameList,
                                       DataBean attributes) {
        StringBuffer result = new StringBuffer();

        String formatField = attributes.getString("fmt", "JSON");

        if (formatField.equalsIgnoreCase("CSV")) {
            //simple export the collections only;
            //Log.log("Export to CSV");

            if (beanList != null && columnNameList != null) {
                String tKey;
                DataBean tEntry;

                boolean hasColumns = false;
                DataBean subEntry;
                DataBean subSubEntry;
                StringBuffer headerBuffer = new StringBuffer();
                StringBuffer entryBuffer = new StringBuffer();
                ArrayList allColumnNameList = new ArrayList();
                allColumnNameList.addAll(columnNameList);
                ArrayList newColumnNameList = new ArrayList();
                boolean levelStarted[] = new boolean[] { false, false };

                for (int i = 0, size = beanList.size(); i < size; i++) {
                    tEntry = (DataBean) beanList.get(i);

                    if (tEntry.getCollections() != null && tEntry.getCollections().size() > 0) {

                        ArrayList subList;
                        Enumeration cKeys = new IteratorEnumeration(tEntry.getCollections().keySet().iterator());
                        String cName;
                        StringBuffer tempResult = new StringBuffer();

                        while (cKeys.hasMoreElements()) {
                            cName = cKeys.nextElement().toString();

                            subList = tEntry.getCollection(cName);

                            for (int j = 0; j < subList.size(); j++) {


                                if (subList.get(j) instanceof DataBean) {
                                    subEntry = (DataBean) subList.get(j);

                                    if (subEntry.getCollections() != null && subEntry.getCollections().size() > 0) {
                                        ArrayList subSubList;
                                        Enumeration csKeys =
                                            new IteratorEnumeration(subEntry.getCollections().keySet().iterator());
                                        String csName;
                                        while (csKeys.hasMoreElements()) {
                                            csName = csKeys.nextElement().toString();

                                            subSubList = subEntry.getCollection(csName);

                                            for (int k = 0; k < subSubList.size(); k++) {
                                                if (subSubList.get(k) instanceof DataBean) {
                                                    subSubEntry = (DataBean) subSubList.get(k);

                                                    if (!levelStarted[0]) {
                                                        newColumnNameList.addAll(getColumnsFromEnumeration(subEntry.getColumnNames()));
                                                        levelStarted[0] = true;
                                                    }

                                                    if (!levelStarted[1]) {
                                                        newColumnNameList.addAll(getColumnsFromEnumeration(subSubEntry.getColumnNames()));
                                                        levelStarted[1] = true;
                                                    }

                                                    tempResult.setLength(0);
                                                    tempResult.append(toCSVLine(tEntry, columnNameList, false));
                                                    tempResult.append(",");
                                                    tempResult.append(toCSVLine(subEntry, subEntry.getColumnNames()));
                                                    tempResult.append(",");
                                                    tempResult.append(toCSVLine(subSubEntry,
                                                                                subSubEntry.getColumnNames()));
                                                    entryBuffer.append(tempResult.toString());
                                                } else {
                                                    if (!levelStarted[0]) {
                                                        newColumnNameList.addAll(getColumnsFromEnumeration(subEntry.getColumnNames()));
                                                        levelStarted[0] = true;
                                                    }
                                                    tempResult.setLength(0);
                                                    tempResult.append(toCSVLine(tEntry, columnNameList, false));
                                                    tempResult.append(",");
                                                    tempResult.append(toCSVLine(subEntry, subEntry.getColumnNames()));
                                                    tempResult.append(subSubList.get(k).toString());
                                                    tempResult.append(closeLine());
                                                    entryBuffer.append(tempResult.toString());
                                                }

                                            }
                                        }
                                    } else {

                                        if (!levelStarted[0]) {
                                            newColumnNameList.addAll(getColumnsFromEnumeration(subEntry.getColumnNames()));
                                            levelStarted[0] = true;
                                        }

                                        tempResult.setLength(0);
                                        tempResult.append(toCSVLine(tEntry, columnNameList, false));
                                        tempResult.append(",");
                                        tempResult.append(toCSVLine(subEntry, subEntry.getColumnNames()));
                                        entryBuffer.append(tempResult.toString());
                                    }
                                } else {
                                    tempResult.setLength(0);
                                    tempResult.append(toCSVLine(tEntry, columnNameList, false));
                                    tempResult.append(",");
                                    tempResult.append(subList.get(j).toString());
                                    tempResult.append(closeLine());
                                    entryBuffer.append(tempResult.toString());
                                }
                            }
                        }
                    } else {
                        entryBuffer.append(toCSVLine(tEntry, columnNameList, true));
                    }
                }

                allColumnNameList.addAll(newColumnNameList);

                result.append(toCSVHeaderLine(allColumnNameList));
                result.append(entryBuffer.toString());
            }


        } else {

            if (attributes.isValid("totalCount"))
                result.append("{\"totalCount\": " + attributes.getString("totalCount") + ",");
            else if( beanList != null )
                result.append("{\"totalCount\": " + String.valueOf(beanList.size()) + ",");

            if (attributes.isValid("status"))
                result.append("\"status\": " + attributes.getString("status") + ",");

            if (attributes.isValid("message"))
                result.append("\"message\": \"" + attributes.getString("message") + "\",");

            if (attributes.isValid("identifier"))
                result.append("\"identifier\": \"" + attributes.getString("identifier") + "\",");

            if (attributes.isValid("label"))
                result.append("\"label\": \"" + attributes.getString("label") + "\",");


            if (beanList != null && columnNameList != null) {
                String tKey;
                result.append("\"items\":[");
                DataBean tEntry;

                boolean hasColumns = false;
                DataBean subEntry;
                for (int i = 0, size = beanList.size(); i < size; i++) {
                    tEntry = (DataBean) beanList.get(i);
                    if (i > 0)
                        result.append(",");

                    result.append("{");
                    hasColumns = columnNameList.size() > 0;
                    for (int j = 0; j < columnNameList.size(); j++) {
                        tKey = columnNameList.get(j).toString();

                        if (j > 0)
                            result.append(",");
                        result.append("\"");
                        result.append(tKey);
                        result.append("\":\"");
                        result.append(org.json.simple.JSONObject.escape(tEntry.getString(tKey)));
                        result.append("\"");
                    }


                    if (tEntry.getCollections() != null) {
                        Enumeration cKeys = new IteratorEnumeration(tEntry.getCollections().keySet().iterator());
                        String cName;

                        if (hasColumns) {
                            result.append(",");
                        }
                        ArrayList subList;
                        int cCount = 0;
                        while (cKeys.hasMoreElements()) {
                            cName = cKeys.nextElement().toString();

                            subList = tEntry.getCollection(cName);
                            if( subList != null ){
                                
                                if (cCount > 0) {
                                    result.append(",");
                                }
                                
                                result.append("\"");
                                result.append(cName);
                                result.append("\":[");
    
                                for (int j = 0; j < subList.size(); j++) {
                                    if (j > 0) {
                                        result.append(",");
                                    }
    
                                    if (subList.get(j) instanceof DataBean) {
                                        subEntry = (DataBean) subList.get(j);
    
                                        Enumeration innerColumNameList = subEntry.getColumnNames();
    
                                        if (innerColumNameList != null) {
                                            result.append("{");
                                            int k = 0;
                                            while (innerColumNameList.hasMoreElements()) {
                                                tKey = innerColumNameList.nextElement().toString();
    
                                                if (k++ > 0)
                                                    result.append(",");
                                                result.append("\"");
                                                result.append(tKey);
                                                result.append("\":\"");
                                                result.append(org.json.simple.JSONObject.escape(subEntry.getString(tKey)));
                                                result.append("\"");
    
                                            }
                                            result.append("}");
                                        } else {
                                            result.append("\"");
                                            result.append(subList.get(j).toString());
                                            result.append("\"");
                                        }
                                    } else {
                                        result.append("\"");
                                        result.append(subList.get(j).toString());
                                        result.append("\"");
                                    }
                                }
                            }
                            
                            result.append("]");
                            cCount++;
                        }
                    }

                    result.append("}");
                }
                result.append("]}");
            }
        }


        return (result.toString());
    }

    public static DataBean getParameters(HttpServletRequest req) {
        DataBean result = new DataBean();

        Enumeration parameterNames = req.getParameterNames();

        if (parameterNames != null) {
            String tStr;
            String vStr;
            while (parameterNames.hasMoreElements()) {
                tStr = parameterNames.nextElement().toString();
                vStr = req.getParameter(tStr);

                if (vStr != null) {
                    result.setValue(tStr, vStr);
                }
            }
        }

        return (result);
    }

    public static ArrayList getParameterList(HttpServletRequest req) {
        ArrayList result = new ArrayList();

        Enumeration parameterNames = req.getParameterNames();

        if (parameterNames != null) {
            String tStr;
            String vStr;
            while (parameterNames.hasMoreElements()) {
                tStr = parameterNames.nextElement().toString();
                vStr = req.getParameter(tStr);

                if (vStr != null) {
                    DataBean tmp = new DataBean();
                    tmp.setValue("name", tStr);
                    tmp.setValue("value", vStr);

                    result.add(tmp);
                }
            }
        }

        return (result);
    }

    public static String toCSVHeaderLine(ArrayList columns) {
        StringBuffer result = new StringBuffer();

        for (int i = 0, size = columns.size(); i < size; i++) {
            result.append("\"");
            result.append(columns.get(i).toString());
            result.append("\"");

            if (i < size - 1) {
                result.append(",");
            }
        }

        result.append((char) 13);
        result.append((char) 10);

        return (result.toString());
    }

    public static String toCSVFinishLine() {
        StringBuffer result = new StringBuffer();

        result.append((char) 13);
        result.append((char) 10);

        return (result.toString());
    }

    public static String toCSVLine(DataBean target, ArrayList columns, boolean addClose) {
        StringBuffer result = new StringBuffer();

        for (int i = 0, size = columns.size(); i < size; i++) {
            //result.append("\"");
            result.append(StringEscapeUtils.escapeCsv(target.getString(columns.get(i).toString())));
            //result.append("\"");

            if (i < size - 1) {
                result.append(",");
            }
        }
        if (addClose) {
            result.append((char) 13);
            result.append((char) 10);
        }

        return (result.toString());
    }

    public static String closeLine() {
        StringBuffer result = new StringBuffer();
        result.append((char) 13);
        result.append((char) 10);

        return (result.toString());
    }

    public static String toCSVLine(DataBean target, Enumeration columns) {
        StringBuffer result = new StringBuffer();

        String tKey;
        int count = 0;

        while (columns.hasMoreElements()) {
            if (count++ > 0) {
                result.append(",");
            }

            tKey = columns.nextElement().toString();

            result.append("\"");
            result.append(target.getString(tKey));
            result.append("\"");


        }

        result.append((char) 13);
        result.append((char) 10);

        return (result.toString());
    }

    public static ArrayList getColumnsFromEnumeration(Enumeration columns) {
        ArrayList result = new ArrayList();

        String tKey;

        while (columns.hasMoreElements()) {

            tKey = columns.nextElement().toString();
            result.add(tKey);

        }


        return (result);
    }

    /**
     *partially match from the beginning of a parameter name
     * @param context
     * @param name
     * @return
     */
    public static String matchParameterName(HttpServletRequest context, String name) {
        String result = null;

        Enumeration pList = context.getParameterNames();
        String tToken;

        while (pList.hasMoreElements()) {
            tToken = pList.nextElement().toString();

            if (tToken.toUpperCase().startsWith(name.toUpperCase())) {
                result = tToken;
                break;
            }
        }
        return (result);
    }

    /**
     *read the sort order set by the ui control for sorting of results
     * it looks for the pattern  sort(<columnname>[+-])
     * @param context
     * @param args
     */
    public static void readSortArguments(HttpServletRequest context, DataBean args) {
        String sortParameterName = matchParameterName(context, "sort(");
        writeLog(1, "sortParameterName: " + sortParameterName);
        if (sortParameterName != null) {
            String sortStr = sortParameterName;
            String direction = sortStr.substring("sort(".length(), "sort(".length() + 1);
            String fieldName = sortStr.substring("sort(".length() + 1);
            fieldName = fieldName.substring(0, fieldName.indexOf(")"));


            String sortValue = fieldName + "+";

            if (direction.equals("-"))
                sortValue = sortValue + "desc";
            else
                sortValue = sortValue + "asc";

            args.setValue("sort", sortValue);
        }
    }

    /**
     *Read the paging (start,count) variables from the request
     * for dojo this in the http header as the "Range" header.
     * @param context
     * @param args
     */
    public static void readPagingArguments(HttpServletRequest context, DataBean args) {
        String start = "0";
        String rows = "25";
        String range = context.getHeader("Range");
        writeLog(1, "range: " + context.getHeader("Range"));

        if (range != null && range.length() > 0 && range.indexOf("items=") == 0) {
            //items=0-24
            String subRange = range.substring("items=".length());

            writeLog(1, "subrange: " + subRange);
            int idx = subRange.indexOf("-");

            if (idx > -1) {
                start = subRange.substring(0, idx);
                rows = subRange.substring(idx + 1);
            }
        }

        writeLog(1, "paging: " + start + " " + rows);
        args.setValue("start", start);
        args.setValue("rows", rows);
    }


    public static BufferedReader openFile(String fileName) {
        BufferedReader result = null;

        try {
            result = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {

        }

        return (result);
    }

    public static String closeFile(BufferedReader tFile) {
        String result = null;

        try {
            tFile.close();

        } catch (IOException e) {
        }

        return (result);
    }

    public static String readCSVLine(BufferedReader tFile) {
        String result = null;

        try {
            result = tFile.readLine();
        } catch (IOException e) {
            result = e.getMessage();
        }

        return (result);
    }

    public static ArrayList<String> getCSVHeaderFields(String record, String delimiter) {
        ArrayList<String> result = splitFields(record, delimiter);

        for (int i = 0, size = result.size(); i < size; i++) {
            result.set(i, removeQuotes(result.get(i)));
        }

        return (result);
    }

    public static DataBean getCSVRecord(String record, ArrayList<String> columns) {
        DataBean result = new DataBean();

        ArrayList<String> values = splitFields(record, ",");

        for (int i = 0, size = columns.size(), fSize = values.size(); i < size; i++) {

            if (i < fSize) {
                result.setValue(columns.get(i), removeQuotes(values.get(i)));
            } else {
                result.setValue(columns.get(i), "");
            }
        }

        return (result);
    }

    public static ArrayList<DataBean> readCSVFile(MessageClient messageService, InputStream in) {
        BufferedReader fileIn = new BufferedReader(new InputStreamReader(in));

        return (readCSVFile(messageService, fileIn));
    }

    public static ArrayList<DataBean> readCSVFile(MessageClient messageService, String fileName) {
        BufferedReader fileIn = openFile(fileName);

        return (readCSVFile(messageService, fileIn));
    }

    public static ArrayList<DataBean> readCSVFile(MessageClient messageService, BufferedReader fileIn) {
        ArrayList<DataBean> result = new ArrayList<DataBean>();


        if (fileIn != null) {
            String buffer = readCSVLine(fileIn);
            messageService.sendMessage("import","data", new DataBean("name","read header: " + buffer));
            String tStr = null;
            ArrayList<String> columns = getCSVHeaderFields(buffer, ",");

            while ((buffer = readCSVLine(fileIn)) != null) {
                messageService.sendMessage("import","data", new DataBean("name","read line: " + buffer));
                DataBean newRec = getCSVRecord(buffer, columns);
                result.add(newRec);
            }

            closeFile(fileIn);
        }

        return (result);
    }

    public static String removeQuotes(String field) {
        String result = field.trim();

        if (result.startsWith("\""))
            result = result.substring(1);

        if (result.endsWith("\""))
            result = result.substring(0, result.length() - 1);

        return (result);
    }


    public static String hashUserKey(String original) {
        String result = original;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            result = sb.toString();
        } catch (Exception e) {

        }

        return (result);
    }


    /**
     * default encoding for cookie
     */

    /**
     *  Encode the string into a hash of specified format.
     * @param md
     * @param s
     * @return
     */
    public static String MessageDigestToHex(String s) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            result = sb.toString();
        } catch (NoSuchAlgorithmException exception) {
            writeLog(1, "unable to get message digest: " + exception.toString());
        }
        return result;
    }


    public static String getCookie(HttpServletRequest request, String cookieName) {
        String result = null;

        try {
            //Cookie[] cookieList = (Cookie[])getPortletRequest().getCookies();
            Cookie[] cookieList = request.getCookies();

            if (cookieList != null) {
                for (int i = cookieList.length - 1; i >= 0; i--) {
                    writeLog(1,
                             "cookie name: " + cookieList[i].getName() + " " + cookieList[i].getPath() + " max age: " +
                             cookieList[i].getMaxAge() + " domain: " + cookieList[i].getDomain() + " path: " +
                             cookieList[i].getPath() + " value: " + cookieList[i].getValue());

                    if (cookieList[i].getName().equalsIgnoreCase(cookieName)) {
                        result = cookieList[i].getValue();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            writeLog(1, "get cookie error : " + e.toString());
        }

        return (result);
    }

    public static void setCookie(DataBean args, HttpServletResponse response, String cookieName, String value) {
        setCookie(args, response, cookieName, value, args.getString("expires", "600000"));
    }

    public static void setCookie(DataBean args, HttpServletResponse response, String cookieName, String value,
                                 String maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(Integer.parseInt(maxAge));
        cookie.setPath(args.getString("path", "/"));
        response.addCookie(cookie);
    }
    
    public static String toJson(DataBean target) {
        
            StringBuilder result = new StringBuilder("{");
            Enumeration columNameList = target.getColumnNames();

            String tKey;
            boolean hasColumns = false;
            if (columNameList != null) {
                int k = 0;
                while (columNameList.hasMoreElements()) {
                    tKey = columNameList.nextElement().toString();

                    if (k++ > 0)
                        result.append(",");
                    result.append("\"");
                    result.append(tKey);
                    result.append("\":\"");
                    result.append(StringEscapeUtils.escapeJson(target.getString(tKey)));
                    result.append("\"");
                    if (!hasColumns)
                        hasColumns = true;
                }

            }

            if (target.getCollections() != null) {
                Enumeration cKeys = new IteratorEnumeration(target.getCollections().keySet().iterator());
                String cName;

                if (hasColumns) {
                    result.append(",");
                }

                if (!hasColumns)
                    hasColumns = true;
                

                ArrayList subList;
                DataBean subEntry;
                int counter = 0;
                while (cKeys.hasMoreElements()) {
                    cName = cKeys.nextElement().toString();

                    subList = target.getCollection(cName);
                    
                    if( subList != null ){
                        if( counter++ > 0 ){
                            result.append(",");
                        }
                        result.append("\"");
                        result.append(cName);
                        result.append("\":[");
    
                        for (int j = 0; j < subList.size(); j++) {
                            if (j > 0) {
                                result.append(",");
                            }
    
                            if (subList.get(j) instanceof DataBean) {
                                subEntry = (DataBean) subList.get(j);
    
                                result.append(toJson(subEntry));
                            } else {
                                result.append("\"");
                                result.append(subList.get(j).toString());
                                result.append("\"");
                            }
                        }
    
                        result.append("]");
                    }

                }
            }


            if (target.getStructures() != null) {
                Enumeration cKeys = new IteratorEnumeration(target.getStructures().keySet().iterator());
                String cName;


                DataBean subEntry;

                while (cKeys.hasMoreElements()) {
                    cName = cKeys.nextElement().toString();

                    subEntry = target.getStructure(cName);
                    
                    if (hasColumns) {
                        result.append(",");
                    }
                    else {
                        hasColumns = true;
                    }
                    result.append("\"");
                    result.append(cName);
                    result.append("\":");
                    result.append(toJson(subEntry));
                }
            }

            result.append("}");

            return (result.toString());
        }
        
        public static String toJsonArray(ArrayList list) {
            StringBuilder result = new StringBuilder("[");

            if (list != null) {
                
                for(int i = 0,size = list.size();i < size;i++){
                    if (i > 0) {
                        result.append(",");
                    }

                    if (list.get(i) instanceof DataBean) {
                        DataBean subEntry = (DataBean) list.get(i);

                        result.append(toJson(subEntry));
                    } else {
                        result.append("\"");
                        result.append(StringEscapeUtils.escapeJson(list.get(i).toString()));
                        result.append("\"");
                    }
                }
                
                
            }


            

            result.append("]");

            return (result.toString());
        }
        


    public static void main(String[] args) {
        DataBean test = new DataBean();
        test.setValue("id", 1);
        test.setValue("name", "name");

        DataBean ctest = new DataBean();
        ctest.setValue("id", "c1");
        ctest.setValue("name", "cname");

        test.addToCollection("children", ctest);

        DataBean c2test = new DataBean();
        c2test.setValue("id", "c2");
        c2test.setValue("name", "c2name");

        test.addToCollection("children", c2test);


        DataBean c3test = new DataBean();
        c3test.setValue("id", "c3");
        c3test.setValue("name", "c3name");

        test.setStructure("subtarget", c3test);

        DataBean c4test = new DataBean();
        c4test.setValue("id", "c4");
        c4test.setValue("name", "c4name");

        c3test.addToCollection("children", c4test);

        System.out.println(Helper.toJson(test));

        System.exit(0);

    }

}
/*end DataStructure*/

package com.jsclosures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.iterators.IteratorEnumeration;


public class Configuration extends DataBean
{
     public Configuration()
     {
          super();
     }

     public Configuration(String filename)
     {
          super();
          setValue("pathname","");
          setValue("filename",filename);
     }

     public Configuration(String path,String filename)
     {
          super();
          setValue("pathname",path);
          setValue("filename",filename);
     }

     public String toString()
     {
          return(toString(" = "));
     }

     public String toString(String d)
     {
          StringBuffer result = new StringBuffer();
          HashMap values = getValues();
          Enumeration keys = new IteratorEnumeration(values.keySet().iterator());
          String tStr;
          while(keys.hasMoreElements())
          {
               tStr = (String)keys.nextElement();
               result.append(tStr + d + getValue(tStr) + ",");
          }

          return(result.toString());
     }

     public String[] parse(String which,String delimiter)
     {
          String result[] = null;

          if(!isValid(which))
          {
               ArrayList tList = Helper.splitFields(getString(which),delimiter);

               result = new String[tList.size()];

               for(int i = 0;i < tList.size();i++)
               {
                    result[i] = tList.get(i).toString();
               }
          }

          return(result);
     }

     /**
          always true.
      */
     public boolean isValid()
     {
          return(true);
     }

     private String readGlobalFile(String newConfiguration)
     {
          String pn = getString("pathname");
          String fn = getString("filename");

          reset();
          setValue("pathname",pn);
          setValue("filename",newConfiguration);
          return(open());
     }

     public String open()
     {
          String result = null;
          try
          {
              File tFile = isValid("pathname") ? new File(getString("pathname"),getString("filename")) : new File(getString("filename"));
              
               BufferedReader br = new BufferedReader(new FileReader(tFile));

               String buffer;

               while((buffer = br.readLine()) != null)
               {
                    parseConfiguration(buffer);
               }

               br.close();

               if(isValid("globalconfigurationfile"))
               {
                    readGlobalFile(getString("globalconfigurationfile"));
               }
          }
          catch(IOException e)
          {
               result = "Error in open location " + e.getMessage();
          }
          return(result);
     }
     
     public String openLocal(Class className,String name)
     {
          InputStream in = getResourceAsStream(STANDALONE,className,name);
          
          return( open(in) );
     }

     public String open(InputStream in)
     {

          String result = null;

          if(in != null)
          {
               try
               {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String buffer;

                    while((buffer = br.readLine()) != null)
                    {
                         parseConfiguration(buffer);
                    }

                    br.close();

                    if(isValid("globalconfigurationfile"))
                    {
                         readGlobalFile(getString("globalconfigurationfile"));
                    }
               }
               catch(IOException e)
               {
                    result = "Error in open location " + e.getMessage();
               }
          }
          else
          {
               result = "Invalid Input Stream";

          }
          return(result);
     }

     public void parseConfiguration(String buffer)
     {
          int idx = buffer.indexOf("=");

          if(idx > -1)
          {
               String name = new String(buffer.substring(0,idx));

               if(!name.startsWith("#"))
               {
                    String value = new String(buffer.substring(idx + 1));
                    setValue(name,value);
               }
          }
     }
     
     public static int ENTERPRISE = 0;
     public static int STANDALONE = 1;
     
     public static InputStream getResourceAsStream(int mode,String className,String name)
     {
          InputStream result = null;

          try
          {
               //j2ee
               if(mode == ENTERPRISE)
               {
                    result = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
               }
               else if(mode == STANDALONE)
               {
                    result = Class.forName(className,true,Thread.currentThread().getContextClassLoader()).getResourceAsStream(name);
               }
               else
               {
                    result = Class.forName(className).getResourceAsStream(name);
               }
          }
          catch(Exception e)
          {
               Helper.writeLog(1,"getResourceAsStream: " + e.toString());
          }

          return(result);
     }
     
     public static InputStream getResourceAsStream(int mode,Class className,String name)
     {
          InputStream result = null;

          try
          {
               //j2ee
               if(mode == ENTERPRISE)
               {
                    result = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
               }
               else if(mode == STANDALONE)
               {
                    result = className.getResourceAsStream(name);
               }
               else
               {
                    result = className.getResourceAsStream(name);
               }
          }
          catch(Exception e)
          {
               Helper.writeLog(1,"getResourceAsStream: " + e.toString());
          }

          return(result);
     }
}

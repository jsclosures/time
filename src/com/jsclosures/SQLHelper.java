package com.jsclosures;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
header portlet will grab current page name
 */
public class SQLHelper
{
     public SQLHelper()
     {
          super();
          //
     }
     
     
     Connection connection;
     
     public void setConnection(Connection conn)
     {
       connection = conn;
     }
     
     public Connection getConnection(boolean mode)
     {
         return( connection );
     }
     
     public void release(Connection conn)
     {
       
     }
     public DataBean readOrderStatus(DataBean context)
     {
          DataBean result = new DataBean();
          
          ArrayList<DataBean> resultList = new ArrayList<DataBean>();
          ArrayList columNameList = new ArrayList();
          
          result.setCollection("resultlist",resultList);
          result.setCollection("columnlist",columNameList);
          
          Connection conn = getConnection(true);
          
          if( conn != null)
          {
               try
               {
                    String primaryKeyName = "id";
                    String tableName = "order_status";
                    columNameList.add("id");
                    columNameList.add("description");
                    columNameList.add("version");
                    StringBuffer sql = new StringBuffer("SELECT " + primaryKeyName + ",description,version FROM " + tableName);
                    ArrayList bindVariables = new ArrayList();
                    
                    if( context.isValid("description") || context.isValid("version") )
                    {
                         sql.append(" WHERE ");
                         
                         boolean hasFilter = false;
                           
                         if( context.isValid("description") )
                         {
                              if( hasFilter )
                                   sql.append(" AND ");
                                   
                              sql.append(" upper(description) like ?");
                              bindVariables.add(context.getString("description").toUpperCase() + "%");
                              hasFilter = true;
                         }  
                         
                        if( context.isValid("version") )
                        {
                             if( hasFilter )
                                  sql.append(" AND ");
                                  
                             sql.append(" version = ?");
                             bindVariables.add(context.getString("version"));
                             hasFilter = true;
                        }   
                    }
                    int counter = 1;     
                    PreparedStatement pStmt = conn.prepareStatement(sql.toString());
                    for(int i = 0;i < bindVariables.size();i++)
                         pStmt.setString(counter++,bindVariables.get(i).toString());
                         
                    ResultSet rSet = pStmt.executeQuery();
                    DataBean tmp;
                    String tKey;
                    while( rSet.next() )
                    {
                         tmp = new DataBean();
                         for(int j = 0;j < columNameList.size();j++)
                         {
                              tKey = columNameList.get(j).toString();
                              
                              if( tKey.equalsIgnoreCase("id") )
                                   tmp.setValue(tKey,rSet.getString(primaryKeyName));
                              else
                                   tmp.setValue(tKey,rSet.getString(tKey));
                         }
                         
                         resultList.add(tmp);
                    }
                    
                    rSet.close();
                    pStmt.close();
                    
               }
               catch(SQLException e)
               {
                    result.setValue("error",e.toString());
               }
               
               release(conn);
          }
          
          return( result );
     }
     
    public DataBean readOrderType(DataBean context)
    {
         DataBean result = new DataBean();
         
         ArrayList<DataBean> resultList = new ArrayList<DataBean>();
         ArrayList columNameList = new ArrayList();
         
         result.setCollection("resultlist",resultList);
         result.setCollection("columnlist",columNameList);
         
         Connection conn = getConnection(true);
         
         if( conn != null)
         {
              try
              {
                   String primaryKeyName = "id";
                   String tableName = "order_type";
                   columNameList.add("id");
                   columNameList.add("description");
                   columNameList.add("version");
                   StringBuffer sql = new StringBuffer("SELECT " + primaryKeyName + ",description,version FROM " + tableName);
                   ArrayList bindVariables = new ArrayList();
                   
                   if( context.isValid("description") || context.isValid("version") )
                   {
                        sql.append(" WHERE ");
                        
                        boolean hasFilter = false;
                          
                        if( context.isValid("description") )
                        {
                             if( hasFilter )
                                  sql.append(" AND ");
                                  
                             sql.append(" upper(description) like ?");
                             bindVariables.add(context.getString("description").toUpperCase() + "%");
                             hasFilter = true;
                        }  
                        
                       if( context.isValid("version") )
                       {
                            if( hasFilter )
                                 sql.append(" AND ");
                                 
                            sql.append(" version = ?");
                            bindVariables.add(context.getString("version"));
                            hasFilter = true;
                       }   
                   }
                   int counter = 1;     
                   PreparedStatement pStmt = conn.prepareStatement(sql.toString());
                   for(int i = 0;i < bindVariables.size();i++)
                        pStmt.setString(counter++,bindVariables.get(i).toString());
                        
                   ResultSet rSet = pStmt.executeQuery();
                   DataBean tmp;
                   String tKey;
                   while( rSet.next() )
                   {
                        tmp = new DataBean();
                        for(int j = 0;j < columNameList.size();j++)
                        {
                             tKey = columNameList.get(j).toString();
                             
                             if( tKey.equalsIgnoreCase("id") )
                                  tmp.setValue(tKey,rSet.getString(primaryKeyName));
                             else
                                  tmp.setValue(tKey,rSet.getString(tKey));
                        }
                        
                        resultList.add(tmp);
                   }
                   
                   rSet.close();
                   pStmt.close();
                   
              }
              catch(SQLException e)
              {
                   result.setValue("error",e.toString());
              }
              
              release(conn);
         }
         
         return( result );
    }
     
     
  
  public DataBean readFromTable(DataBean context,Connection conn,String tableName,String primaryKeyName,String sql,ArrayList columNameList,ArrayList bindVariables)
  {
       DataBean result = new DataBean();
       
                 
       if( conn != null)
       {
            try
            {
                 int counter = 1;     
                 PreparedStatement pStmt = conn.prepareStatement(sql.toString());
                 pStmt.setInt(counter++,Helper.parseInt(bindVariables.get(0).toString()));
                 for(int i = 1;i < bindVariables.size();i++)
                      pStmt.setString(counter++,bindVariables.get(i).toString());
                      
                 ResultSet rSet = pStmt.executeQuery();
                 String tKey;
                 if( rSet.next() )
                 {
                      for(int j = 0;j < columNameList.size();j++)
                      {
                           tKey = columNameList.get(j).toString();
                           
                           if( tKey.equalsIgnoreCase("id") )
                                result.setValue(tKey,rSet.getString(primaryKeyName));
                           else
                                result.setValue(tKey,rSet.getString(tKey));
                      }
                 }
                 
                 rSet.close();
                 pStmt.close();
                 
            }
            catch(SQLException e)
            {
                 result.setValue("error",e.toString());
            }
            
       }
       
       return( result );
  }
  
  public ArrayList readListFromTable(DataBean context,Connection conn,String tableName,String primaryKeyName,String sql,ArrayList columNameList,ArrayList bindVariables)
  {
       ArrayList result = new ArrayList();
       
                 
       if( conn != null)
       {
            try
            {
                 int counter = 1;     
                 PreparedStatement pStmt = conn.prepareStatement(sql);
                 if( bindVariables.size() > 0 )
                   pStmt.setInt(counter++,Helper.parseInt(bindVariables.get(0).toString()));
                 for(int i = 1;i < bindVariables.size();i++)
                 {
                     pStmt.setString(counter++,bindVariables.get(i).toString());
                 }
                      
                 ResultSet rSet = pStmt.executeQuery();
                 String tKey;
                 DataBean tmp;
                 
                 while( rSet.next() )
                 {
                      tmp = new DataBean();
                      
                      for(int j = 0;j < columNameList.size();j++)
                      {
                           tKey = columNameList.get(j).toString();
                           
                           if( tKey.equalsIgnoreCase("id") )
                                tmp.setValue(tKey,rSet.getString(primaryKeyName));
                           else
                                tmp.setValue(tKey,rSet.getString(tKey));
                      }
                      
                      result.add(tmp);
                 }
                 
                 rSet.close();
                 pStmt.close();
            }
            catch(SQLException e)
            {
                 //result.setValue("error",e.toString());
                 DataBean tmp = new DataBean();
                 tmp.setValue("id",0);
                 tmp.setValue("ridername",e.toString() + " sql: " + sql);
                 result.add(tmp);
            }
            
       }
       
       return( result );
  }
  
  public ArrayList readParentListFromTable(DataBean context,Connection conn,String tableName,String primaryKeyName,String sql,ArrayList columNameList,ArrayList bindVariables)
  {
  ArrayList result = new ArrayList();
  
     
  if( conn != null)
  {
   try
   {
     int counter = 1;     
     PreparedStatement pStmt = conn.prepareStatement(sql);
     pStmt.setInt(counter++,Helper.parseInt(bindVariables.get(0).toString()));
        pStmt.setInt(counter++,Helper.parseInt(bindVariables.get(1).toString()));
     for(int i = 2;i < bindVariables.size();i++)
     {
       pStmt.setString(counter++,bindVariables.get(i).toString());
     }
       
     ResultSet rSet = pStmt.executeQuery();
     String tKey;
     DataBean tmp;
     
     while( rSet.next() )
     {
       tmp = new DataBean();
       
       for(int j = 0;j < columNameList.size();j++)
       {
         tKey = columNameList.get(j).toString();
         
         if( tKey.equalsIgnoreCase("id") )
           tmp.setValue(tKey,rSet.getString(primaryKeyName));
         else
           tmp.setValue(tKey,rSet.getString(tKey));
       }
       
       result.add(tmp);
     }
     
     rSet.close();
     pStmt.close();
   }
   catch(SQLException e)
   {
     //result.setValue("error",e.toString());
     DataBean tmp = new DataBean();
     tmp.setValue("id",0);
     tmp.setValue("description",e.toString());
     result.add(tmp);
     context.setValue("error",e.toString());
   }
   
  }
  
  return( result );
  }
  
  public DataBean writeOrderStatus(DataBean context)
  {
    DataBean result = new DataBean();

    String action = context.getString("action", "");
    Connection conn = getConnection(true);

    if (conn != null && action.length() > 0)
    {
      String primaryKeyName = "id";
      String tableName = "order_status";


      if (action.equalsIgnoreCase("DELETE"))
      {
        try
        {
          int counter = 1;
          PreparedStatement pStmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKeyName + " = ?");
          pStmt.setInt(counter++, context.getInt("id"));
          int rCount = pStmt.executeUpdate();

          pStmt.close();

          conn.commit();

          result.setValue("result", "DELETED");
        }
        catch (SQLException e)
        {
          result.setValue("error", e.toString());
        }
      }
      else if (action.equalsIgnoreCase("SAVE"))
      {
        try
        {
          int counter = 1;
          PreparedStatement pStmt = conn.prepareStatement("UPDATE " + tableName + " set description = ?,version = ? WHERE " + primaryKeyName + " = ?");
          pStmt.setString(counter++, context.getString("description", "").trim());
          pStmt.setInt(counter++, context.getInt("version"));
          pStmt.setInt(counter++, context.getInt("id"));
          int rCount = pStmt.executeUpdate();

          pStmt.close();

          if (rCount == 0)
          {
            counter = 1;

            if (context.isValid("id") && context.getInt("id") > 0)
            {
              pStmt = conn.prepareStatement("INSERT " + tableName + "(" + primaryKeyName + ",description,version) VALUES(?,?,?)");
              pStmt.setInt(counter++, context.getInt("id"));
            }
            else
              pStmt = conn.prepareStatement("INSERT " + tableName + "(description,version) VALUES(?,?)");

            pStmt.setString(counter++, context.getString("description", "").trim());
            pStmt.setInt(counter++, context.getInt("version"));

            rCount = pStmt.executeUpdate();

            pStmt.close();
          }

          conn.commit();

          result.setValue("result", "SAVED");

        }
        catch (SQLException e)
        {
          result.setValue("result", e.toString());
        }
      }

      release(conn);
    }

    return (result);
  }
  
    public DataBean writeOrderType(DataBean context)
    {
      DataBean result = new DataBean();

      String action = context.getString("action", "");
      Connection conn = getConnection(true);

      if (conn != null && action.length() > 0)
      {
        String primaryKeyName = "id";
        String tableName = "order_type";


        if (action.equalsIgnoreCase("DELETE"))
        {
          try
          {
            int counter = 1;
            PreparedStatement pStmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKeyName + " = ?");
            pStmt.setInt(counter++, context.getInt("id"));
            int rCount = pStmt.executeUpdate();

            pStmt.close();

            conn.commit();

            result.setValue("result", "DELETED");
          }
          catch (SQLException e)
          {
            result.setValue("error", e.toString());
          }
        }
        else if (action.equalsIgnoreCase("SAVE"))
        {
          try
          {
            int counter = 1;
            PreparedStatement pStmt = conn.prepareStatement("UPDATE " + tableName + " set description = ?,version = ? WHERE " + primaryKeyName + " = ?");
            pStmt.setString(counter++, context.getString("description", "").trim());
            pStmt.setInt(counter++, context.getInt("version"));
            pStmt.setInt(counter++, context.getInt("id"));
            int rCount = pStmt.executeUpdate();

            pStmt.close();

            if (rCount == 0)
            {
              counter = 1;

              if (context.isValid("id") && context.getInt("id") > 0)
              {
                pStmt = conn.prepareStatement("INSERT " + tableName + "(" + primaryKeyName + ",description,version) VALUES(?,?,?)");
                pStmt.setInt(counter++, context.getInt("id"));
              }
              else
                pStmt = conn.prepareStatement("INSERT " + tableName + "(description,version) VALUES(?,?)");

              pStmt.setString(counter++, context.getString("description", "").trim());
              pStmt.setInt(counter++, context.getInt("version"));

              rCount = pStmt.executeUpdate();

              pStmt.close();
            }

            conn.commit();

            result.setValue("result", "SAVED");

          }
          catch (SQLException e)
          {
            result.setValue("result", e.toString());
          }
        }

        release(conn);
      }

      return (result);
    }
    
    public static byte[] readBlob(DataBean ctx,ResultSet rSet,String column)
    throws SQLException
    {
             byte[] result = new byte[]{};
             
             Blob blob = null;
        
             
             if( blob == null )
                      blob = rSet.getBlob(column);
                      
             if(blob != null)
             {
                      try
                      {
                               InputStream bin = blob.getBinaryStream();

                               byte buffer[] = new byte[1024];
                      
                               ByteArrayOutputStream bout = new ByteArrayOutputStream();
                               int counter = 0;
                               int length = 0; 
                               int j;
                               while ((length = bin.read(buffer)) != -1) 
                               {
                                            for(j = 0;j < length;j++)
                                            {
                                                     bout.write((byte)buffer[j]);
                                                     counter++;
                                            }
                               }
                               
                               bin.close();
                               
                               result = bout.toByteArray();
                            
                               ctx.setValue("_blobinfo"+ column,"READ BLOB SIZE: " + result.length + " " + counter + " " + bin.available());
                      }
                      catch(java.io.IOException e)
                      {
                               ctx.setValue("error","READ BLOB ERROR" + column +": " + e.toString() + e.getMessage());
                      }
             }

             return(result);
    }
}

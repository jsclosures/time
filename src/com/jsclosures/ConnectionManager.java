package com.jsclosures;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Calendar;


public class ConnectionManager {
	
	public static Connection getConnection(RestService context,String which) throws SQLException {
		Connection con = null;
		
		Configuration conf = context != null ? context.getConfiguration() : new Configuration();
		
		String dataSourcePath = conf != null && conf.isValid("datasourcename") ? conf.getString("datasourcename") : "jdbc/webuiDB";
		
		if( dataSourcePath.length() > 0 ) {
		    long cTime = Calendar.getInstance().getTime().getTime();
		   try
		   {
				context.writeLog(1,"MS --->  Starting get connection: " + dataSourcePath + " " + Calendar.getInstance().getTime());
				javax.naming.InitialContext ic = new javax.naming.InitialContext();
				javax.sql.DataSource dataSource = (javax.sql.DataSource)ic.lookup(dataSourcePath);
				con = dataSource.getConnection();
				if( context != null ) context.writeLog(1,"MS --->  After success get connection: got connection in " + (Calendar.getInstance().getTime().getTime()-cTime) + "msec");
				
				//ic.close();
		   }
		   catch(Exception e)
		   {
				if( context != null ) context.writeLog(1,"MS ---> Error get connection: " + e.toString() + " failed to get connection for " + (Calendar.getInstance().getTime().getTime()-cTime) + "msec");
		   }
		}
		
		return con;
	}

        public static void close(Connection con){
            try{
                if(con != null){
                   con.close();
                }
            }catch(Exception e){
			    //context.writeLog(1,"simple close connection error: " + e.toString());
			}
        }
}

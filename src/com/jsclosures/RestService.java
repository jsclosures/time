package com.jsclosures;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONStringer;


public interface RestService
{

	public void setConfiguration(Configuration conf);
	
	public Configuration getConfiguration();
	
	public String getConfiguration(String which);

	public void writeLog(int level, String message);

}

package com.unico.gcdapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Servlet's init method is used to initialize the application.
 * @author NAVEEN
 *
 */
public class InitializationServlet extends HttpServlet {
	public void init() throws ServletException {
		AppProperties prop;
		try {
			prop = AppProperties.getAppPropertiies();
			prop.addProperty("JMS_CONN_URL", getInitParameter("JMS_CONN_URL"));
			System.out.println("InitializationServlet : prop.getProperty(JMS_CONN_URL)" + prop.getProperty("JMS_CONN_URL"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

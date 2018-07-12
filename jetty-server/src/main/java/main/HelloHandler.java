package main;


import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Created by kongxiangwen on 5/18/18 w:20.
 */



public class HelloHandler extends AbstractHandler
{
	final String greeting;
	final String body;

	private static Logger log = Logger.getLogger(HelloHandler.class.getClass());
	public HelloHandler()
	{
		this("Hello World");
	}

	public HelloHandler( String greeting )
	{

		this(greeting, null);
		log.info("Hello:"+greeting);
	}

	public HelloHandler( String greeting, String body )
	{
		this.greeting = greeting;
		this.body = body;
	}


	public void handle( String target,
						Request baseRequest,
						HttpServletRequest request,
						HttpServletResponse response ) throws IOException,
			ServletException
	{
		response.setContentType("text/html; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		out.println("<h1>" + greeting + "</h1>");
		if (body != null)
		{
			out.println(body);

		}
		//baseRequest.getPathInfo()
		log.info("handing:"+request.getPathInfo()+":"+greeting);
		baseRequest.setHandled(true);
	}
}

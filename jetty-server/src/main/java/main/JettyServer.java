package main;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyServer extends AbstractHandler
{
	private static Logger log = Logger.getLogger(JettyServer.class.getClass());
	public void handle(String target,
					   Request baseRequest,
					   HttpServletRequest request,
					   HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Hello World</h1>");
	}


	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);




		log.info("start jetty server");
		ContextHandler context = new ContextHandler("/");
		context.setContextPath("/");
		context.setHandler(new HelloHandler("Root Hello"));

		ContextHandler contextFR = new ContextHandler("/fr");
		contextFR.setHandler(new HelloHandler("Bonjoir"));


		HelloHandler h = new HelloHandler("ddd");


		ContextHandler contextIT = new ContextHandler("/it");
		contextIT.setHandler(new HelloHandler("Bongiorno"));


		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { context, contextFR, contextIT});
		server.setHandler(contexts);

		//server.setHandler(new JettyServer());
		server.start();
		server.join();
	}




	public static class HelloServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<h1>Hello SimpleServlet</h1>");
		}
	}

	public static class HelloPrintingFilter implements Filter {

		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
				throws IOException, ServletException {
			System.out.print("hello from filter");
		}


		public void init(FilterConfig arg0) throws ServletException {

		}


		public void destroy() {}
	}




}
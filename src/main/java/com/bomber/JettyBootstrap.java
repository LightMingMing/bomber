package com.bomber;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

public class JettyBootstrap {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
		classList.add(AnnotationConfiguration.class.getName());

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setResourceBase("./src/main/webapp");
		context.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*/ironrhino-[^/]*\\.jar$");
		context.setInitParameter(DefaultServlet.CONTEXT_INIT + "dirAllowed", "false");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{new ShutdownHandler("password"), context});

		server.setHandler(handlers);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}

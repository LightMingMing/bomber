package com.bomber;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.ironrhino.core.util.AppInfo;

public class JettyBootstrap {

	public static void main(String[] args) throws Exception {

		AppInfo.initialize();

		Server server = new Server(8080);

		Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
		classList.add(AnnotationConfiguration.class.getName());

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase("./src/main/webapp");
		webAppContext.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*/ironrhino-[^/]*\\.jar$");
		webAppContext.setInitParameter(DefaultServlet.CONTEXT_INIT + "dirAllowed", "false");
		webAppContext.setDefaultsDescriptor("");

		HandlerList handlers = new HandlerList();
		handlers.addHandler(new ShutdownHandler("password"));
		handlers.addHandler(webAppContext);

		server.setHandler(handlers);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}

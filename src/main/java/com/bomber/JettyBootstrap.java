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

		new JettyBootstrap().startup(8080, "password");
	}

	public void startup(int port, String shutdownToken) throws Exception {
		Server server = new Server(port);

		Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
		classList.add(AnnotationConfiguration.class.getName());

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase("./src/main/webapp");
		webAppContext
				.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*/ironrhino-[^/]*\\.jar$|.*/classes/.*");
		webAppContext.setInitParameter(DefaultServlet.CONTEXT_INIT + "dirAllowed", "false");

		HandlerList handlers = new HandlerList();
		handlers.addHandler(new ShutdownHandler(shutdownToken));
		handlers.addHandler(webAppContext);

		server.setHandler(handlers);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}

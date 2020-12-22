package com.bomber;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
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

		server.setHandler(context);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}

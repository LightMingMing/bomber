package com.bomber;

import java.util.Arrays;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
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

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase("./src/main/webapp");
		webAppContext.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*/ironrhino-[^/]*\\.jar$");
		webAppContext.setInitParameter(DefaultServlet.CONTEXT_INIT + "dirAllowed", "false");

		ContextHandlerCollection contexts = new ContextHandlerCollection();

		HandlerCollection handlers = new HandlerCollection();
		handlers.addHandler(new ShutdownHandler("password"));
		handlers.addHandler(webAppContext);
		handlers.addHandler(contexts);

		WebAppProvider provider = new WebAppProvider();
		provider.setMonitoredDirectories(
				Arrays.asList("./src/main/webapp", "./src/main/resources", "./build/classes/java/main"));
		provider.setExtractWars(false);
		provider.setScanInterval(2);

		DeploymentManager deployer = new DeploymentManager();
		deployer.setContexts(contexts);
		deployer.addAppProvider(provider);

		server.addBean(deployer);
		server.setHandler(handlers);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}

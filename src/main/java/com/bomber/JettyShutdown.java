package com.bomber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

/**
 * @see org.eclipse.jetty.server.handler.ShutdownHandler
 */
public class JettyShutdown {

	private final Logger logger = LoggerFactory.getLogger(JettyShutdown.class);

	public static void main(String[] args) {
		new JettyShutdown().attemptShutdown(8080, "password");
	}

	public void attemptShutdown(int port, String shutdownCookie) {
		try {
			URL url = new URL("http://localhost:" + port + "/shutdown?token=" + shutdownCookie);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			connection.setRequestMethod("POST");
			connection.getResponseCode();
			connection.disconnect();
			logger.info("Shutting down " + url + ": " + connection.getResponseMessage());
		} catch (SocketException e) {
			logger.debug("Not running");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

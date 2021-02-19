package com.bomber;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

/**
 * @see org.eclipse.jetty.server.handler.ShutdownHandler
 */
public class JettyShutdown {

	private static final String ANSI_RESET = "\u001B[0m";

	private static final String ANSI_GREEN = "\u001B[32m";

	private static final String ANSI_RED = "\u001B[31m";

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
			info("Shutting down " + url + ": " + connection.getResponseMessage());
		} catch (SocketException e) {
			warn();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void info(String msg) {
		System.out.println(ANSI_GREEN + msg + ANSI_RESET);
	}

	private void warn() {
		System.out.println(ANSI_RED + "Not running " + ANSI_RESET);
	}
}

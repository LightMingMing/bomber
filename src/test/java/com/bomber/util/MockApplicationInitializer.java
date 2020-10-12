package com.bomber.util;

import org.ironrhino.core.servlet.MainAppInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MockApplicationInitializer {

	public static void setApplicationContext(ApplicationContext ctx) {
		ServletContext mock = MainAppInitializer.SERVLET_CONTEXT = mock(ServletContext.class);
		given(mock.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).willReturn(ctx);
	}
}

package io.github.resilience4j.circuitbreaker;

// Fix java.lang.NoClassDefFoundError if resilience4j-all.jar is removed
// TODO upgrade ironrhino
public class CallNotPermittedException extends RuntimeException {

	private static final long serialVersionUID = 4258982742865398968L;

}

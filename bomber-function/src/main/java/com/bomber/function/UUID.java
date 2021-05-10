package com.bomber.function;

/**
 * UUID
 *
 * @author MingMing Zhao
 */
@FuncInfo(optionalArgs = "noHyphen")
public class UUID implements Producer<String> {

	private static final char hyphen = '-';

	private final boolean noHyphen;

	public UUID() {
		this(true);
	}

	public UUID(boolean noHyphen) {
		this.noHyphen = noHyphen;
	}

	@Override
	public String execute() {
		String uuid = java.util.UUID.randomUUID().toString();
		return noHyphen ? uuid.replace(hyphen, '\0') : uuid;
	}

}

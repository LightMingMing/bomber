package com.bomber.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;

import com.bomber.functions.Function;
import com.bomber.functions.FunctionMetadataHelper;
import com.bomber.manager.PayloadManager;
import com.bomber.model.Payload;
import com.bomber.model.PayloadOption;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class PayloadAction extends EntityAction<Payload> {

	private static final long serialVersionUID = 7361376054414253701L;

	@Autowired
	private PayloadManager payloadManager;

	@Getter
	@Setter
	private Payload payload;

	@Getter
	@Setter
	private int rows = 100;

	@Getter
	private String content;

	@Getter
	@Setter
	private List<String> columns;

	private Function instance(PayloadOption option) {
		try {
			return FunctionMetadataHelper.instance(option.getFunctionName(), option.getArgumentValues());
		} catch (IllegalAccessException | InstantiationException e) {
			throw new IllegalArgumentException("Failed to instance a function", e);
		}
	}

	public String preview() {
		payload = payloadManager.get(this.getUid());

		List<Function> functions = payload.getOptions().stream().map(this::instance).collect(Collectors.toList());

		StringJoiner joiner = new StringJoiner("\r\n");
		for (int i = 0; i < rows; i++) {
			joiner.add(functions.stream().map(Function::execute).collect(Collectors.joining(", ")));
		}
		this.content = joiner.toString();
		this.columns = payload.getOptions().stream().map(PayloadOption::getKey).collect(Collectors.toList());
		return "preview";
	}

	public String download() throws IOException {
		if (columns == null || columns.isEmpty()) {
			throw new IllegalArgumentException("columns is null or empty");
		}
		payload = payloadManager.get(this.getUid());
		if (payload == null) {
			throw new IllegalArgumentException("payload does not exists");
		}
		List<Function> functions = payload.getOptions().stream().filter(option -> columns.contains(option.getKey()))
				.map(this::instance).collect(Collectors.toList());

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + payload.getId() + ".txt");

		PrintWriter writer = response.getWriter();
		for (int i = 0; i < rows; i++) {
			String row = functions.stream().map(Function::execute).collect(Collectors.joining(", "));
			writer.write(row);

			if (i != rows - 1) {
				writer.write("\r\n");
			}
			if (i != 0 && (i & 127) == 0) {
				writer.flush();
			}
		}
		return NONE;
	}
}

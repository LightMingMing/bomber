package com.bomber.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;

import com.bomber.functions.FunctionExecutor;
import com.bomber.functions.FunctionOption;
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

	public String preview() {
		payload = payloadManager.get(this.getUid());
		if (payload == null) {
			throw new IllegalArgumentException("payload does not exist");
		}

		List<FunctionOption> options = payload.getOptions().stream().map(PayloadOption::map)
				.collect(Collectors.toList());
		this.columns = options.stream().map(FunctionOption::getKey).collect(Collectors.toList());

		FunctionExecutor executor = new FunctionExecutor(options);

		StringJoiner joiner = new StringJoiner("\r\n");
		for (int i = 0; i < rows; i++) {
			StringJoiner joinerInline = new StringJoiner(", ");
			Map<String, String> context = executor.execute();
			for (String column : columns) {
				joinerInline.add(context.get(column));
			}
			joiner.add(joinerInline.toString());
		}
		this.content = joiner.toString();
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

		List<FunctionOption> options = payload.getOptions().stream().map(PayloadOption::map)
				.collect(Collectors.toList());
		FunctionExecutor executor = new FunctionExecutor(options, columns);

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + payload.getId() + ".txt");

		PrintWriter writer = response.getWriter();
		for (int i = 0; i < rows; i++) {
			StringJoiner joinerInline = new StringJoiner(", ");
			Map<String, String> context = executor.execute();
			for (String column : columns) {
				joinerInline.add(context.get(column));
			}
			writer.write(joinerInline.toString());

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

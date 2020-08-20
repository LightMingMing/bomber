package com.bomber.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

	@Getter
	private List<String> formattedArgumentValues;

	protected String formatArgumentValues(PayloadOption option) {
		if (option == null)
			return null;
		String args = option.getArgumentValues();

		Map<String, String> params = new HashMap<>();
		if (args != null && !args.isEmpty()) {
			String[] pairs = args.split(", *");
			for (String pair : pairs) {
				String[] arr = pair.split("=", 2);
				if (arr.length == 2) {
					params.put(arr[0], arr[1]);
				}
			}
		}

		StringJoiner joiner = new StringJoiner("\n");

		String requiredArgs = option.getRequiredArgs();
		if (requiredArgs != null && !requiredArgs.isEmpty()) {
			joiner.add("# required args");
			String[] arr = requiredArgs.split(", *");
			for (String arg : arr) {
				joiner.add(arg + "=" + params.getOrDefault(arg, ""));
			}
		}

		String optionalArgs = option.getOptionalArgs();
		if (optionalArgs != null && !optionalArgs.isEmpty()) {
			joiner.add("# optional args");
			String[] arr = optionalArgs.split(", *");
			for (String arg : arr) {
				joiner.add(arg + "=" + params.getOrDefault(arg, ""));
			}
		}

		return joiner.toString();
	}

	@Override
	protected String doInput() throws Exception {
		String parent = super.doInput();
		if (INPUT.equals(parent)) {
			Payload payload = this.getEntity();
			List<PayloadOption> options = payload.getOptions();
			if (options != null && !options.isEmpty()) {
				formattedArgumentValues = options.stream().map(this::formatArgumentValues).collect(Collectors.toList());
			}
		}
		return parent;
	}

	@Override
	public String save() {
		if (!makeEntityValid()) {
			return INPUT;
		}
		payload = getEntity();
		if (payload.getOptions() != null) {
			for (PayloadOption option : payload.getOptions()) {
				List<String> args = new ArrayList<>();
				String content = option.getArgumentValues();
				if (content == null) {
					continue;
				}
				String[] lines = content.split("\n");
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					if (line == null || line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					String[] pair = line.split("=", 2);
					if (pair.length != 2) {
						this.addActionError("line " + i + " is invalid");
						return INPUT;
					}
					if (pair[1].isEmpty()) {
						continue;
					}
					// TODO strong check ?
					args.add(line);
				}
				option.setArgumentValues(String.join(", ", args));
			}
		}
		payloadManager.save(payload);
		return SUCCESS;
	}

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

package com.bomber.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;

import com.bomber.functions.core.DefaultFunctionExecutor;
import com.bomber.functions.core.FunctionContext;
import com.bomber.functions.core.FunctionExecutor;
import com.bomber.manager.PayloadManager;
import com.bomber.model.Payload;
import com.bomber.model.PayloadOption;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class PayloadAction extends EntityAction<Payload> {

	private static final long serialVersionUID = 7361376054414253701L;

	private static final String DEFAULT_DELIMITER = ",";

	@Autowired
	private PayloadManager payloadManager;

	@Getter
	@Setter
	private Payload payload;

	@Getter
	@Setter
	private int rows = 100;

	@Getter
	@Setter
	private String delimiter = DEFAULT_DELIMITER;

	@Getter
	private String content;

	@Getter
	@Setter
	private List<String> columns;

	protected String formatArgumentValues(PayloadOption option) {
		if (option == null)
			return null;
		List<String> args = option.getArgumentValues();
		Map<String, String> params = new HashMap<>();
		if (args != null && !args.isEmpty()) {
			for (String pair : args) {
				String[] arr = pair.split("=", 2);
				if (arr.length == 2) {
					params.put(arr[0], arr[1]);
				}
			}
		}

		Set<String> added = new HashSet<>();
		StringJoiner joiner = new StringJoiner("\n");

		String requiredArgs = option.getRequiredArgs();
		if (requiredArgs != null && !requiredArgs.isEmpty()) {
			joiner.add("# required args");
			String[] arr = requiredArgs.split(", *");
			for (String arg : arr) {
				joiner.add(arg + "=" + params.getOrDefault(arg, ""));
				added.add(arg);
			}
		}

		String optionalArgs = option.getOptionalArgs();
		if (optionalArgs != null && !optionalArgs.isEmpty()) {
			joiner.add("# optional args");
			String[] arr = optionalArgs.split(", *");
			for (String arg : arr) {
				joiner.add(arg + "=" + params.getOrDefault(arg, ""));
				added.add(arg);
			}
		}

		if (params.size() > added.size()) {
			joiner.add("# customized args");
			params.forEach((k, v) -> {
				if (!added.contains(k)) {
					joiner.add(k + "=" + v);
				}
			});
		}

		return joiner.toString();
	}

	// shortcut to create
	public String quickCreate() {
		payload = payloadManager.get(this.getUid());
		payload.setId(null);
		List<PayloadOption> options = payload.getOptions();
		if (options != null && !options.isEmpty()) {
			options.forEach(option -> option.setContent(formatArgumentValues(option)));
		}
		return INPUT;
	}

	@Override
	protected String doInput() throws Exception {
		String parent = super.doInput();
		if (INPUT.equals(parent)) {
			Payload payload = this.getEntity();
			List<PayloadOption> options = payload.getOptions();
			if (options != null && !options.isEmpty()) {
				options.forEach(option -> option.setContent(formatArgumentValues(option)));
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
				String content = option.getContent();
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
					if (pair[1].trim().isEmpty()) {
						continue;
					}
					// TODO strong check ?
					args.add(pair[0].trim() + "=" + pair[1].trim());
				}
				option.setArgumentValues(args);
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

		List<FunctionContext> all = payload.getOptions().stream().map(PayloadOption::map).collect(Collectors.toList());

		FunctionExecutor executor = new DefaultFunctionExecutor(all);
		List<Map<String, String>> result;
		try {
			result = executor.execute(rows);
		} finally {
			executor.shutdown();
		}

		this.columns = new ArrayList<>();
		all.stream().map(FunctionContext::retKeys).forEach(columns::addAll);

		StringJoiner joiner = new StringJoiner("\r\n");
		for (Map<String, String> context : result) {
			StringJoiner joinerInline = new StringJoiner(", ");
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

		List<FunctionContext> all = payload.getOptions().stream().map(PayloadOption::map).collect(Collectors.toList());
		FunctionExecutor executor = new DefaultFunctionExecutor(all, columns);

		List<Map<String, String>> result;
		try {
			result = executor.execute(rows);
		} finally {
			executor.shutdown();
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + payload.getId() + ".txt");

		PrintWriter writer = response.getWriter();

		for (int i = 0; i < rows; i++) {
			StringJoiner joinerInline = new StringJoiner(delimiter);
			Map<String, String> context = result.get(i);
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

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
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;

import com.bomber.function.model.FunctionContext;
import com.bomber.manager.FunctionConfigureManager;
import com.bomber.model.FunctionConfigure;
import com.bomber.model.FunctionDefinition;
import com.bomber.service.PayloadGenerateService;
import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class FunctionConfigureAction extends EntityAction<FunctionConfigure> {

	private static final long serialVersionUID = 7361376054414253701L;

	private static final String DEFAULT_DELIMITER = ",";

	@Autowired
	private FunctionConfigureManager functionConfigureManager;

	@Autowired
	private PayloadGenerateService payloadGenerateService;

	@Getter
	@Setter
	private FunctionConfigure functionConfigure;

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

	protected String formatArgumentValues(FunctionDefinition functionDefinition) {
		if (functionDefinition == null)
			return null;
		List<String> args = functionDefinition.getArgumentValues();
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

		String requiredArgs = functionDefinition.getRequiredArgs();
		if (requiredArgs != null && !requiredArgs.isEmpty()) {
			joiner.add("# required args");
			String[] arr = requiredArgs.split(", *");
			for (String arg : arr) {
				joiner.add(arg + "=" + params.getOrDefault(arg, ""));
				added.add(arg);
			}
		}

		String optionalArgs = functionDefinition.getOptionalArgs();
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
		functionConfigure = functionConfigureManager.get(this.getUid());
		functionConfigure.setId(null);
		List<FunctionDefinition> functionDefinitions = functionConfigure.getFunctionDefinitions();
		if (functionDefinitions != null && !functionDefinitions.isEmpty()) {
			functionDefinitions.forEach(each -> each.setContent(formatArgumentValues(each)));
		}
		return INPUT;
	}

	@Override
	protected String doInput() throws Exception {
		String parent = super.doInput();
		if (INPUT.equals(parent)) {
			FunctionConfigure functionConfigure = this.getEntity();
			List<FunctionDefinition> functionDefinitions = functionConfigure.getFunctionDefinitions();
			if (functionDefinitions != null && !functionDefinitions.isEmpty()) {
				functionDefinitions.forEach(each -> each.setContent(formatArgumentValues(each)));
			}
		}
		return parent;
	}

	@Override
	public String save() {
		if (!makeEntityValid()) {
			return INPUT;
		}
		functionConfigure = getEntity();
		if (functionConfigure.getFunctionDefinitions() != null) {
			for (FunctionDefinition functionDefinition : functionConfigure.getFunctionDefinitions()) {
				List<String> args = new ArrayList<>();
				String content = functionDefinition.getContent();
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
				functionDefinition.setArgumentValues(args);
			}
		}
		functionConfigureManager.save(functionConfigure);
		return SUCCESS;
	}

	public String preview() {
		functionConfigure = functionConfigureManager.get(this.getUid());
		if (functionConfigure == null) {
			throw new IllegalArgumentException("payload does not exist");
		}

		this.columns = new ArrayList<>();
		functionConfigure.getFunctionDefinitions().stream().map(FunctionDefinition::map).map(FunctionContext::retKeys)
				.forEach(columns::addAll);

		List<Map<String, String>> result = payloadGenerateService.generate(this.getUid(), 0, rows);

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

		List<Map<String, String>> result = payloadGenerateService.generate(this.getUid(), 0, rows, columns);

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + this.getUid() + ".txt");

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

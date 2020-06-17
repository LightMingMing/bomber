package com.bomber.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;

import com.bomber.model.ApplicationInstance;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class ApplicationInstanceAction extends EntityAction<ApplicationInstance> {

	@Getter
	@Setter
	private ApplicationInstance applicationInstance;

	// shortcut to create
	public String quickCreate() {
		applicationInstance = getEntityManager(ApplicationInstance.class).get(this.getUid());
		applicationInstance.setId(null);
		return INPUT;
	}
}

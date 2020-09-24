package com.bomber.service;

public interface BomberService {

	void execute(BomberRequest request);

	void pauseExecute(String id);

	void continueExecute(String id);

}

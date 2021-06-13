package com.bomber.service;

public interface BomberService {

	void execute(BomberRequest request);

	void pauseExecute(Long id);

	void continueExecute(Long id);

}

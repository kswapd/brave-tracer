package services;

import interfaces.BarService;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

public class BarServiceImpl implements BarService {

	public String sayBar(String name) {
		System.out.println("init : " + name);
		return "Bar dubbo provider " + name;
	}

}

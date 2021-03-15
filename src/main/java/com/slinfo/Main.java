package com.slinfo;

import java.io.UnsupportedEncodingException;

import com.slinfo.cara.CaraModule;
import com.slinfo.helper.Path;
import com.slinfo.util.log.SLogger;

import spark.servlet.SparkApplication;

public class Main implements SparkApplication {
	
	public void init() {
		
		System.out.println("Server Starting...");
		
		try {
			SLogger.setup(Path.local() + "/log");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		CaraModule.boot();
//		Connection.setup();
		
		System.out.println("Everything is booted.");
	}
}

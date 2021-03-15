package com.slinfo.helper;

import java.util.ArrayList;
import java.util.List;

import com.slinfo.resource.Resource;
import com.slinfo.util.db.DBConfig;
import com.slinfo.util.db.DBConnection;

public class Connection {
	
	private static String prontoWebAPIBaseURL;
	
	public static void setup() {
		
		List<DBConfig> list = new ArrayList<DBConfig>();
		
		DBConfig pronto = new DBConfig("SIP", Resource.connectivity.getString("pronto.db"),
				Resource.connectivity.getString("pronto.ip"), Resource.connectivity.getString("pronto.port"),
				Resource.connectivity.getString("pronto.path"), Resource.connectivity.getString("pronto.server"),
				Resource.connectivity.getString("pronto.user"), Resource.connectivity.getString("pronto.password"), true);
		
		list.add(pronto);
		
		try {
			List<String> errors = DBConnection.setup(list);
			if (!errors.isEmpty()) {
				StringBuilder build = new StringBuilder();
				for (String er : errors) {
					build.append(er);
					build.append(",");
				}
				build.replace(build.length() - 1, build.length(), "");
				throw new Exception("Failed To Connect To: " + build.toString());
			}
			System.out.println("Connectivity Setup Complete!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connectivity Setup InComplete! " + e.getMessage());
		}
	}
	
	public static String getProntoWebAPIBaseURL() {
		return prontoWebAPIBaseURL;
	}
	
	
}

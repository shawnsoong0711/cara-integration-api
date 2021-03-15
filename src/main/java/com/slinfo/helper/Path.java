package com.slinfo.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.slinfo.resource.Resource;

public class Path {

	public static String local() throws UnsupportedEncodingException {
		String path = Resource.systemConfig.getString("localpath");
		String fullPath = URLDecoder.decode(path, "UTF-8");
		return fullPath;
	}
	
}

package com.slinfo.cara;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.slinfo.helper.Connection;
import com.slinfo.helper.HttpClient;
import com.slinfo.helper.ResourceCollection;

public class CaraApi {
	
	private static ResourceCollection<String> select(String env, String page, String query, String code, String desc) {
		List<String> resultList = new ArrayList<>();

		HttpClient httpClient = new HttpClient(Connection.getProntoWebAPIBaseURL() + "/stocks");
		httpClient.setMethodType("GET");
		httpClient.addQueryParam("c", env);
		httpClient.addQueryParam("p", page);
		httpClient.addQueryParam("q", query);

		if (code != null) {
			httpClient.addQueryParam("code", code);
		} else {
//			if (stkCodeSet != null) {
//				for (String codeInSet : stkCodeSet) {
//					httpClient.addQueryParam("code", codeInSet);
//				}
//			} else if (stkCodeList != null) {
//				for (String codeInList : stkCodeList) {
//					httpClient.addQueryParam("code", codeInList);
//				}
//			}
		}

		if (desc != null) {
			httpClient.addQueryParam("desc", desc);
		}

		httpClient.connect();

		if (httpClient.responseStatus() != null && httpClient.responseStatus() == 200) {
			JsonArray jsonArray = new JsonParser().parse(httpClient.responseBody()).getAsJsonArray();
			for(JsonElement jsonElement : jsonArray) {
//			add into return result
				resultList.add(null);
			}

			List<String> xPaginationHeaderValues = httpClient.responseHeader("X-PAGINATION");
			String paginationInfo = null;
			if (xPaginationHeaderValues != null) {
				paginationInfo = xPaginationHeaderValues.get(0);
			}

			return new ResourceCollection<>(resultList, paginationInfo);
		} else {
			return new ResourceCollection<>(resultList, null);
		}
	}

}

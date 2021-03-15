package com.slinfo.cara;

import static spark.Spark.get;

import com.slinfo.util.json.JsonUtil;

public class CaraController {

	public CaraController() {
		get("/api/v1.0/test", (request, response) -> {
			return true;
		}, JsonUtil.json());
	}

}

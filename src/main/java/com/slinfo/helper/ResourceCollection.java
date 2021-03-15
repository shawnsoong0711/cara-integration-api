package com.slinfo.helper;

import java.util.Arrays;
import java.util.List;

public class ResourceCollection<R> {
	private final R[] resources;
	private final String paginationInfo;

	public ResourceCollection(R[] resources, String paginationInfo) {
		this.resources = resources;
		this.paginationInfo = paginationInfo;
	}
	
	public ResourceCollection(List<R> resourceList, String paginationInfo) {
		resources = (R[]) resourceList.toArray();
		this.paginationInfo = paginationInfo;
	}

	public R[] getResources() {
		return resources;
	}

	public List<R> getResourcesAsList() {
		if (resources == null) {
			return null;
		}
		return Arrays.asList(resources);
	}

	public String getPaginationInfo() {
		return paginationInfo;
	}
}

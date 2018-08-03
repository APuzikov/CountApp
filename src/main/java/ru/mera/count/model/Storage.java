package ru.mera.count.model;

import java.util.HashMap;

public class Storage {

	private  HashMap<String, Integer> requestCount;

	public Storage(){
		requestCount = new HashMap<>();
		requestCount.put("get", 0);
		requestCount.put("post", 0);
		requestCount.put("put", 0);
		requestCount.put("delete", 0);
	}

	public HashMap<String, Integer> getRequestCount() {
		return requestCount;
	}

	public void addCount(String key) {
		requestCount.merge(key, 1, Integer::sum);
	}
}

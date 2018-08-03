package ru.mera.count.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageModel {

	private Integer get;
	private Integer post;
	private Integer put;
	private Integer delete;

	public StorageModel() {
	}

	@JsonProperty("GET")
	public Integer getGet() {
		return get;
	}

	public void setGet(int get) {
		this.get = get;
	}

	@JsonProperty("POST")
	public Integer getPost() {
		return post;
	}

	public void setPost(int post) {
		this.post = post;
	}

	@JsonProperty("PUT")
	public Integer getPut() {
		return put;
	}

	public void setPut(int put) {
		this.put = put;
	}

	@JsonProperty("DELETE")
	public Integer getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}
}

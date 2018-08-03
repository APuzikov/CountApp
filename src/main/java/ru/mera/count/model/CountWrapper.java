package ru.mera.count.model;

import java.time.Instant;

public class CountWrapper {

	private int count;
	private Instant timestamp;

	public CountWrapper() {
	}

	public CountWrapper(int count, Instant timestamp) {
		this.count = count;
		this.timestamp = timestamp;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}

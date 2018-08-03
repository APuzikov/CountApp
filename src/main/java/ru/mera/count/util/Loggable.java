package ru.mera.count.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loggable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected void logInfo(String message){
		logger.info(message);
	}
}

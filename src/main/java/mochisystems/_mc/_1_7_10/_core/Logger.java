package mochisystems._mc._1_7_10._core;

import org.apache.logging.log4j.LogManager;

public class Logger {
	 
	public static org.apache.logging.log4j.Logger logger = LogManager.getLogger("Msys");

	public static void trace(String msg) {
		Logger.logger.trace(msg);
	}
 
	public static void info(String msg) {
		Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		Logger.logger.warn(msg);
	}
	
	public static void debugInfo(String msg) {
		info(msg);
	}
 
}
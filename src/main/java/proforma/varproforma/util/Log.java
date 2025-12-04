package proforma.varproforma.util;

import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Log {
	private static Level level= Level.INFO;
	private static Logger logger;
	private static StreamHandler sh;
	
	public static void setLevel(Level level) {
		Log.level= level;
		sh.setLevel(level);
		logger.setLevel(level);
	}

	static {
		logger= Logger.getLogger(Log.class.getName());
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		sh= new StreamHandler(System.out, new Formatter() {
			@Override public String format(LogRecord record) {
				return Instant.now()+": "+record.getMessage()+"\n";
			}
		});
		sh.setLevel(level);
		logger.addHandler(sh);
	}
	public static void debug(String s){
		log(Level.FINEST, s);
	}
	public static void info(String s){
		log(Level.INFO, s);
	}
	public static void warn(String s){
		log(Level.WARNING, s);
	}
	public static void error(String s){
		log(Level.SEVERE, s);
	}
	
	private synchronized static void log(Level level, String msg) {
		sh.flush();
		logger.log(level, msg);
		sh.flush();
	}

}

package natlang.debug;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up and manages loggers
 * 
 * @author swartjes
 * Created on 29-jun-2005
 */
public class LogFactory {
	
	public static Level LEVEL = Level.FINE;
	public static String LOGPATH = "log/";
	
	private static List<String> loggers = new ArrayList<String>();
	public static long timeOffset = 0;
	
	/** 
	 * Closes all handlers of the logger for this package
	 * 
	 * @param loggerName the name of the logger
	 */
	public static void closeLogger(String loggerName) {
		String pkg = LogFactory.makeLoggerName(loggerName);
		if (LogFactory.loggers.contains(pkg)) {
			Logger l = Logger.getLogger(pkg);
			Handler[] hlist = l.getHandlers();

			for (Handler element : hlist) {
				System.out.println("Handler " + element);
				element.close();
			}
		}
	}
	
	/**
	 * Generates index HTML file containing all the log files as links
	 *
	 */
	public static void generateIndex() {
		
		// Generate general index
		
		// Generate agent-specific index
		try {
			FileWriter out = new FileWriter(getIndex());
			PrintWriter p = new PrintWriter(out);
			
			for (int i = 0; i < LogFactory.loggers.size(); i++) {
				String currLogger = LogFactory.loggers.get(i);
				StringBuffer b = new StringBuffer(100);
				b.append("<A HREF=\"./").append(currLogger).append(".html\">"); 
				b.append(LogFactory.loggers.get(i));
				b.append("</A><BR>");
				p.print(b.toString());
			}
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getIndex() {
		return LOGPATH + "_index.html";
	}
	
	/**
	 * Returns a logger for the package of the object; sets it up if not used before
	 * @param src the Object that is getting the logger
	 * @return the Logger
	 */
	public static Logger getLogger(Object src) {
		String pkg = LogFactory.makeLoggerName(src.getClass().getName());
		if (LogFactory.loggers.contains(pkg)) {
			return Logger.getLogger(pkg);
		}
		else {
			Logger newLogger = LogFactory.setupLogger(pkg);
			// There is a new logger. Adjust index file.
			LogFactory.generateIndex();
			return newLogger;
		}		
	}
	
	/**
	 * @deprecated 
	 * Use getLogger(Object src) instead.
	 * 
	 * Returns the logger for this package; sets it up if not used before
	 * 
	 * @param loggerName the name of the logger
	 * @return the Logger
	 */
	@Deprecated
	public static Logger getLogger(String loggerName) {
		String pkg = LogFactory.makeLoggerName(loggerName);
		if (LogFactory.loggers.contains(pkg)) {
			return Logger.getLogger(pkg);
		}
		else {
			Logger newLogger = LogFactory.setupLogger(pkg);
			// There is a new logger. Adjust index file.
			LogFactory.generateIndex();
			return newLogger;
		}
	}
	
	/*public static Logger getLogger(String AgentName, String loggerName){
		return getLogger(AgentName + "." + loggerName);
	}*/

	private static String makeLoggerName(String pkg) {
			return pkg;
	}
	
	
	/** 
	 * Creates a new HTML logger
	 * 
	 * @param pkg the package name of the logger
	 * @return the Logger
	 */
	private static Logger setupLogger(String pkg) {
		// Set timer
		if (LogFactory.timeOffset == 0) {
			Date d = new Date();
			LogFactory.timeOffset = d.getTime();
		}
		// Make logger
		Logger logger = Logger.getLogger(pkg);
		logger.setLevel(LEVEL);

		try {
			FileHandler handler = new FileHandler(LOGPATH + pkg + ".html");
			HTMLLogFormatter formatter = new HTMLLogFormatter();
			formatter.setTitle(pkg);
			
			handler.setFormatter(formatter);
			logger.addHandler(handler);	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.config("Starting logger: " + pkg);		
		LogFactory.loggers.add(pkg);
		return logger;
	}

}

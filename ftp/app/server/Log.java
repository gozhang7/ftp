package server;

import java.util.logging.Logger;

public class Log
{
	Logger log;
	
	public Log(Logger arg)
	{
		this.log = arg;
	}
	
	public void severe(String s) {
		log.severe(s);
	}

	public void warning(String s) {
		log.info(s);
	}

	public void info(String s) {
		log.info(s);
	}
}

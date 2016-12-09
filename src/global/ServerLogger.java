package global;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

// assumes the current class is called MyLogger
public final class ServerLogger{
	public final static Logger LOGGER = Logger.getLogger(ServerLogger.class.getName());
	
	public final static void init(){
        FileHandler fileTxt;
		try {
			fileTxt = new FileHandler("ServerOutput.txt");
	        LOGGER.addHandler(fileTxt);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final static void log(String info){
		LOGGER.info(info);
	}
	
}
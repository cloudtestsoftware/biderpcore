package cms.service.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthenticationException extends Exception {

	static Log logger = LogFactory.getLog(DaoException.class);
	private String message;
	private String name;
	
	public AuthenticationException(String message, String name) {
		super();
		this.message = message;
		this.name = name;
		if(message.toLowerCase().contains("error")){
			logger.error(name + " :" +message);
		}else{
			logger.info(name + " :" +message);
		}
	}
	
	public AuthenticationException(String message) {
		super();
		this.message = message;
		if(message.toLowerCase().contains("error")){
			logger.error(message);
		}else{
			logger.info(message);
		}
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

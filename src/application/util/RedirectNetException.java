package application.util;

public class RedirectNetException extends Exception {
	private static final long serialVersionUID = 3751360402981883142L;
	private String cookie=null;
	
	public RedirectNetException(String cookie) {
		this.cookie=cookie;
	}
	public String getMessage() {
		return "cookieŒ¥…Ë÷√≥…π¶£¨cookie£∫"+cookie;
	}
}

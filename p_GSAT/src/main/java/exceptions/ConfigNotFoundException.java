package exceptions;

public class ConfigNotFoundException extends Exception {
	public ConfigNotFoundException(String path) {
		super("Config at path: " + path + " could not be found");
	}
}

package exceptions;

public class ConfigReadException extends Exception {

	public ConfigReadException(String field) {
		super("Error while reading " + field + " from config");
	}
}

package exceptions;

/**
 * This exception is thrown if a path is needed but not set.
 * 
 * @author Ben Kohr
 *
 */
public class MissingPathException extends Exception {

	public PathUsage usage;
	
	/**
	 * Constructor calling the super constructor.
	 * It also specifies the type of path missing.
	 * 
	 * @author Ben Kohr
	 */
	public MissingPathException(PathUsage usage) {
		super("Missing path detected.");
		this.usage = usage;
	}
	
}

package exception;

/**
 * @author fayss
 *
 */
public class WrongFileTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongFileTypeException(String message) {
		super(message);
	}
}

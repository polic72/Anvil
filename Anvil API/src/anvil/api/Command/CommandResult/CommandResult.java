package anvil.api.Command.CommandResult;


/**
 * Represents a base class for a CommandResult.
 * <p>
 * By default, 3 CommandResults exist: Failed, Success, and Warning. Each of which have their own properties.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see Failed
 * @see Success
 * @see Warning
 */
public abstract class CommandResult{
	protected String message;
	
	
	/**
	 * Super constructs a CommandResult implementation.
	 * 
	 * @param message String object containing the default message for the CommandResult.
	 */
	protected CommandResult(String message){
		this.message = message;
	}
	
	
	/**
	 * Sets the message for the CommandResult implementation. 
	 * 
	 * @param message String object containing the new message for the CommandResult implementation.
	 */
	protected void setMessage(String message){
		this.message = message;
	}
	
	
	/**
	 * Gets the message of the current CommandResult implementation.
	 * 
	 * @return String object containing the message for the current CommandResult implementation.
	 */
	public String getMessage(){
		return message;
	}
	
	
	@Override
	public String toString(){
		return message;
	}
}
















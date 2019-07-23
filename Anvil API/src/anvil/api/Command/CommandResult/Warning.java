package anvil.api.Command.CommandResult;


/**
 * Represents a "Successful" (with ""s) Command execution, but with a warning.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandResult
 */
public class Warning extends CommandResult{
	
	
	/**
	 * Constructs a Warning Result with the default message: "The command was run with warnings.".
	 */
	public Warning(){
		super("The command was run with warnings.");
	}
	
	
	/**
	 * Constructs a Warning Result with the given message.
	 * 
	 * @param message String object containing the message of the Result.
	 */
	public Warning(String message){
		super(message);
	}
	
	
	@Override
	public void setMessage(String message){
		super.setMessage(message);
	}
	
	
	@Override
	public String getMessage(){
		return super.getMessage();
	}
}

















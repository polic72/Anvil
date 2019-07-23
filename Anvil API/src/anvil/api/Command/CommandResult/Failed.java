package anvil.api.Command.CommandResult;


/**
 * Represents a Failed Command execution.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandResult
 */
public class Failed extends CommandResult{
	
	
	/**
	 * Constructs a Failed Result with the default message: "The command seems to have failed!".
	 */
	public Failed(){
		super("The command seems to have failed!");
	}
	
	
	/**
	 * Constructs a Failed Result with the given message.
	 * 
	 * @param message String object containing the message of the Result.
	 */
	public Failed(String message){
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
















package anvil.api.Command.CommandResult;


/**
 * Represents a Successful Command execution.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandResult
 */
public class Success extends CommandResult{
	
	
	/**
	 * Constructs a Success Result with the default message: "The command was run successfully.".
	 */
	public Success(){
		super("The command was run successfully.");
	}
	
	
	@Override
	public String getMessage(){
		return super.getMessage();
	}
}

















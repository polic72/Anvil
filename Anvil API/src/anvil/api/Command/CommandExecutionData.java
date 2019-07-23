package anvil.api.Command;

import anvil.api.Command.CommandResult.CommandResult;


/**
 * Represents the Execution data for a CommandType. This is where the actual execution of a Command can be 
 * specified.
 * <p>
 * The execution of a Command should be considered event-based, meaning that it should either be entirely 
 * self-contained, or take into account all possible constraints necessary for functionality.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandType
 * @see CommandResult
 */
public interface CommandExecutionData{
	
	
	/**
	 * This is run by the wrapper's Command parser when the command is called. Should be considered event-based, 
	 * meaning that it should either entirely self-contained, or take into account all possible constraints 
	 * necessary for functionality.
	 * <p>
	 * If an exception is thrown by any part of this method, it will be caught, and a Failed object (with the 
	 * message "The Command threw a(n) [Exception name]!") is returned. This will be given to Anvil to process 
	 * and to the caller of the command.
	 * 
	 * @param <T> Any class that extends the CommandResult class.
	 * @param args String array containing the arguments for the command instance.
	 * @return Object extending {@link CommandResult} representing the success of the Command.
	 */
	public abstract <T extends CommandResult> T execute(String[] args);
}


















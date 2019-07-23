package anvil.api.Command;


/**
 * Represents an instance class for wrapper commands.
 * <p>
 * The difference between a Command and a CommandType is that the type is a template for the command itself. 
 * Whichever template the command fits, is the type it is.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandType
 */
public class Command{
	private String entire_commandString;
	private CommandType type;
	
	private String[] arguments;
	
	
	/**
	 * Constructs a Command object with the given CommandType and commandString.
	 * 
	 * @param commandString String object containing the entire Command instance.
	 * @param type CommandType object representing the type of Command this object is. The given <em>type</em> 
	 * should be available in the {@link CommandType#getKnownCommandTypes()} to be considered proper.
	 * @throws IllegalArgumentException When the command_string doesn't match the type.
	 */
	public Command(String commandString, CommandType type){
		this.entire_commandString = commandString;
		
		String[] split_commandString = commandString.split(String.valueOf(CommandType.ARG_DELIMITER));
		
		if (!(CommandType.TYPE_DELIMITER + type.getName()).equals(split_commandString[0])){
			throw new IllegalArgumentException("The name of the type doesn't match the name in the given "
					+ "String.");
		}
		
		
		String[] argument_templates = type.getArguments();
		
		if (split_commandString.length != (argument_templates.length + 1)){
			throw new IllegalArgumentException("The arguments of the type don't match the arguments in the "
					+ "given String.");
		}
		
		
		arguments = new String[split_commandString.length - 1];
		
		for (int i = 0; i < argument_templates.length; ++i){
			if (!split_commandString[i + 1].matches(argument_templates[i])){
				throw new IllegalArgumentException("The arguments of the type don't match the arguments in the "
						+ "given String.");
			}
			
			arguments[i] = split_commandString[i + 1];
		}
		
		
		this.type = type;
	}
	
	
	/**
	 * Constructs a Command object with the given CommandType.
	 * <p>
	 * While a Command object can be made anywhere, only Anvil's generated Command objects actually represent 
	 * commands run for their intended purpose. They should not be created or used in a PlugIn, but they can be.
	 * 
	 * @param commandString String object containing the entire Command instance.
	 * @throws IllegalArgumentException When there is no existing CommandType that satisfies the commandString.
	 */
	public Command(String commandString) throws IllegalArgumentException{
		this.entire_commandString = commandString;
		
		String[] split_commandString = commandString.split(String.valueOf(CommandType.ARG_DELIMITER));
		
		
		CommandType types[] =  CommandType.getKnownCommandTypeGroup(split_commandString[0].substring(1));
		
		if (types != null){
			for (int i = 0; i < types.length; ++i){
				if (!(CommandType.TYPE_DELIMITER + types[i].getName()).equals(split_commandString[0])){
					continue;
				}
				
				
				String[] argument_templates = types[i].getArguments();
				
				if (split_commandString.length != (argument_templates.length + 1)){
					continue;
				}
				
				for (int q = 0; q < argument_templates.length; ++q){
					if (!split_commandString[q + 1].matches(argument_templates[q])){
						continue;
					}
				}
				
				
				this.type = types[i];
				
				break;
			}
		}
		
		
		if (type == null){
			throw new IllegalArgumentException("There was no CommandType found that satisfied the given "
					+ "commandString.");
		}
		
		
		arguments = new String[split_commandString.length - 1];
		
		for (int i = 1; i < split_commandString.length; ++i){
			arguments[i - 1] = split_commandString[i];
		}
	}
	
	
	/**
	 * Gets the entire_commandString of the Command.
	 * 
	 * @return String object containing the entire command.
	 */
	public String getCommandString(){
		return entire_commandString;
	}
	
	
	/**
	 * Gets the type of the Command.
	 * 
	 * @return CommandType object containing the known CommandType of the Command.
	 */
	public CommandType getType(){
		return type;
	}
	
	
	/**
	 * Gets the 0-indexed argument of the Command.
	 * 
	 * @param index Integer representing the index of the desired argument.
	 * @return String object containing the argument of the given index. Null if an illegal index is given.
	 */
	public String getArgumentValue(int index){
		if (index < 0){
			return null;
		}
		else if (index >= arguments.length){
			return null;
		}
		
		return arguments[index];
	}
	
	
	/**
	 * Gets the number of the arguments the Command has.
	 * 
	 * @return Integer representing the number of the arguments the Command has.
	 */
	public int getArgumentCount(){
		return arguments.length;
	}
	
	
	/**
	 * Gets the arguments of the Command.
	 * 
	 * @return String array containing all the argument values of the Command.
	 */
	public String[] getArguments(){
		return arguments.clone();
	}
	
	
	@Override
	public String toString(){
		return entire_commandString;
	}
	
	
	@Override
	public boolean equals(Object other){
		if (other == null){
			return false;
		}
		else if (!(other instanceof Command)){
			return false;
		}
		
		
		Command other_command = (Command)other;
		
		if (!(entire_commandString.equals(other_command.getCommandString()))){
			return false;
		}
		
		
		return true;
	}
	
	
//	public static void main(String[] args){
//		CommandExecutionData data2 = new CommandExecutionData(){
//			
//			@Override
//			@SuppressWarnings("unchecked")
//			public <T extends CommandResult> T execute(String[] args) {
//				return (T) new Success();
//			}
//		};
//		
//		
//		CommandType hmm = new CommandType("!test2", CommandPermissions.PLAYER, data2);
//		
//		CommandType.addCommandTypeToKnowns(hmm);
//		
//		
//		
//		Command test = new Command("!wef");
		
		
		
//		//This is all done in a PlugIn.
//		CommandExecutionData data = new CommandExecutionData(){
//			
//			@SuppressWarnings("unchecked")
//			@Override
//			public <T extends CommandResult> T execute(String[] args){
//				System.out.println("Hey beter");
//				
//				throw new IllegalAccessError();
//				
////				return (T) new Success();
//			}
//		};
//		
//		CommandType beter = new CommandType("!test \\p{ASCII}{1,} \\p{ASCII}{1,}",
//				CommandPermissions.PLAYER, data);
//		
//		CommandType.addCommandTypeToKnowns(beter);
//		
//		
//		CommandExecutionData data2 = new CommandExecutionData(){
//			
//			@Override
//			@SuppressWarnings("unchecked")
//			public <T extends CommandResult> T execute(String[] args) {
//				return (T) new Success();
//			}
//		};
//		
//		
//		CommandType hmm = new CommandType("!test2", CommandPermissions.PLAYER, data2);
//		
//		CommandType.addCommandTypeToKnowns(hmm);
//		
//		
//		
//		
//		
//		
//		
//		
//		//This is made by ServerOutputLine interpretation, and raw CLI input.
//		Command command = new Command("!test ewfdjefwejfiewf wef");//, beter);
//		
////		System.out.println(command.getArgumentValue(-1));
////		System.out.println(command.getArgumentValue(2));
////		
////		for (int i = 0; i < command.getArgumentCount(); ++i){
////			System.out.println(command.getArgumentValue(i));
////		}
//		
//		
//		//Start this interpreter after every Command object is made.
//		Thread interpreter = new Thread(){
//			CommandResult result = null;
//			
//			@Override
//			public void run(){
//				Thread executioner = new Thread(){
//					
//					@Override
//					public void run(){
//						try{
//							result = command.getType().getExecutionData().execute(command.getArguments());	
//						}
//						catch (Throwable e){
//							result = new Failed("The Command threw a(n) " + e.getClass().getSimpleName() + "!");
//						}
//					}
//					
//				};
//				
//				executioner.start();
//				try{
//					executioner.join();
//				}
//				catch (InterruptedException e){
//					//This won't happen.
//				}
//				
//				
//				//Do the sending to the monitor and the Player here.
//				System.out.println(result.getMessage());
//			}
//			
//		};
//		
//		
//		interpreter.start();
//	}
}


















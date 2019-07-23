package anvil.api.Command;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Represents a type of wrapper command recognized by Anvil.
 * <p>
 * Wrapper commands are treated separately from in-game commands, as they don't have any auto-fills and use a 
 * separate delimiter. Fiddling with the CommandPermissions changes who can call what commands.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandPermissions
 * @see CommandExecutionData
 * @see Command
 */
public class CommandType{
	private static LinkedList<CommandTypeNode> allCommandTypes = new LinkedList<>();
	
	public static final char TYPE_DELIMITER = '!';
	public static final char ARG_DELIMITER = ' ';
	
	private String name;
	private String[] arguments;
	
	private CommandPermissions permissions;
	
	private CommandExecutionData data;
	
	
	/**
	 * Constructs a CommandType object with the given name and arguments.
	 * <p>
	 * The name is checked with {@link String#equals(Object)} while each argument is checked with regex 
	 * patterns.
	 * 
	 * @param name String object containing the name of the CommandType. Multiple CommandTypes can have the 
	 * same name. Cannot be null or contain '{@value #TYPE_DELIMITER}'/'{@value #ARG_DELIMITER}'.
	 * @param arguments Array of String objects, each containing a regex pattern representing an argument. 
	 * @param permissions The permission level of the CommandType. This baselines who can run the command.
	 * @param data Execution data of the CommandType; what happens when the Command is called.
	 * @throws IllegalArgumentException When the name or arguments are illegal.
	 * 
	 * @see CommandExecutionData
	 * @see CommandPermissions
	 */
	public CommandType(String name, String[] arguments, CommandPermissions permissions, 
			CommandExecutionData data){
		if (name == null){
			throw new IllegalArgumentException("The name cannot be null.");
		}
		else if (name.contains(String.valueOf(TYPE_DELIMITER))){
			throw new IllegalArgumentException("The name cannot contain \'" + TYPE_DELIMITER + "\'.");
		}
		else if (name.contains(String.valueOf(ARG_DELIMITER))){
			throw new IllegalArgumentException("The name cannot contain \'" + ARG_DELIMITER + "\'.");
		}
		else{
			this.name = name;
		}
		
		
		if (arguments == null || arguments.length == 0){
			this.arguments = new String[0];
		}
		else{
			for (int i = 0; i < arguments.length; ++i){
				if(arguments[i] == null){
					throw new IllegalArgumentException("No individual argument can be null.");
				}
				else if(arguments[i].isEmpty()){
					throw new IllegalArgumentException("No individual argument can be empty.");
				}
				else if (arguments[i].contains(String.valueOf(TYPE_DELIMITER))){
					throw new IllegalArgumentException("No argument can contain \'" + TYPE_DELIMITER + "\'.");
				}
				else if (arguments[i].contains(String.valueOf(ARG_DELIMITER))){
					throw new IllegalArgumentException("No argument can contain \'" + ARG_DELIMITER + "\'.");
				}
			}
			
			
			this.arguments = arguments;
		}
		
		
		this.permissions = permissions;
		
		
		this.data = data;
	}
	
	
	/**
	 * Constructs a CommandType object with the given <em>commandTypeString</em>. The 
	 * <em>commandTypeString</em> is interpreted exactly how it would appear in the {@link #toString()} method.
	 * <p>
	 * The name is checked with {@link String#equals(Object)} while each argument is checked with regex 
	 * patterns.
	 * 
	 * @param commandTypeString String object containing the CommandType.
	 * @param permissions The permission level of the CommandType. This baselines who can run the command.
	 * @param data Execution data of the CommandType; what happens when the Command is called.
	 * @throws IllegalArgumentException When the given <em>commandTypeString</em> is not properly formatted.
	 * 
	 * @see CommandPermissions
	 * @see CommandExecutionData
	 */
	public CommandType(String commandTypeString, CommandPermissions permissions, CommandExecutionData data){
		if (!commandTypeString.startsWith(String.valueOf(TYPE_DELIMITER))){
			throw new IllegalArgumentException("The given String object does not start with " + TYPE_DELIMITER
					+ ".");
		}
		
		
		if (!commandTypeString.contains(String.valueOf(ARG_DELIMITER))){
			String temp = commandTypeString.substring(1);
			
			if (temp.isEmpty()){
				throw new IllegalArgumentException("The name cannot be empty.");
			}
			else if(temp.contains(String.valueOf(TYPE_DELIMITER))){
				throw new IllegalArgumentException("The name cannot contain \'" + TYPE_DELIMITER + "\'.");
			}
			
			name = temp;
			arguments = new String[0];
		}
		else{
			String[] splits = commandTypeString.split(String.valueOf(ARG_DELIMITER));
			
			
			String temp = splits[0].substring(1);
			
			if (temp == null){
				throw new IllegalArgumentException("The name cannot be null.");
			}
			else if (temp.contains(String.valueOf(TYPE_DELIMITER))){
				throw new IllegalArgumentException("The name cannot contain \'" + TYPE_DELIMITER + "\'.");
			}
			else if (temp.contains(String.valueOf(ARG_DELIMITER))){
				throw new IllegalArgumentException("The name cannot contain \'" + ARG_DELIMITER + "\'.");
			}
			else{
				this.name = temp;
			}
			
			
			arguments = new String[splits.length - 1];
			
			for (int i = 1; i < splits.length; ++i){
				if(splits[i].isEmpty()){
					throw new IllegalArgumentException("No 2 consecutive \'" + ARG_DELIMITER + "\' can be "
							+ "in the commandTypeString.");
				}
				else if (splits[i].contains(String.valueOf(TYPE_DELIMITER))){
					throw new IllegalArgumentException("No argument can contain \'" + TYPE_DELIMITER + "\'.");
				}
				
				arguments[i - 1] = splits[i];
			}
		}
		
		
		this.permissions = permissions;
		
		
		this.data = data;
	}
	
	
	/**
	 * Tells whether or not a given String object passes the constraints provided by the CommandType.
	 * <p>
	 * The name of the CommandType must pass the {@link String#equals(Object)} check, while each argument needs 
	 * to pass a regex pattern. (Watch out for PatternSyntaxExceptions, they might trip up some arguments).
	 * <p>
	 * Refer to <a href="https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">here</a> for 
	 * more information on regex Patterns.
	 * 
	 * @param commandString String object to be tested. Must include the {@value #TYPE_DELIMITER} at the 
	 * beginning.
	 * @return Whether or not the given String object passes the CommandType constraints.
	 */
	public boolean passes(String commandString){
		String[] commandString_parts = commandString.split(String.valueOf(ARG_DELIMITER));
		
		if (!commandString_parts[0].equals(String.valueOf(TYPE_DELIMITER) + name)){
			return false;
		}
		else if (arguments.length != commandString_parts.length - 1){
			return false;
		}
		
		
		boolean passes = true;
		for (int i = 1; i < commandString_parts.length; ++i){
			
			if(!commandString_parts[i].matches(arguments[i - 1])){
				passes = false;
				break;
			}
		}
		
		return passes;
	}
	
	
	/**
	 * Adds a given CommandType to the wrapper's list of known CommandTypes.
	 * <p>
	 * Each added CommandType object should be mutually exclusive in terms of the passes() method. This means 
	 * that no 2 CommandType objects should allow the same Command or String pass. Failing to do this will 
	 * cause inconsistencies in CommandType detection.
	 * <p>
	 * If <em>newCommandType</em> has the same <em>name</em> as any other known CommandType object, they are 
	 * considered to be in the same group.
	 * 
	 * @param newCommandType CommandType object to add.
	 * @return Whether or not the add was successful.
	 */
	public static boolean addCommandTypeToKnowns(CommandType newCommandType){
		Iterator<CommandTypeNode> iterator = allCommandTypes.iterator();
		CommandTypeNode node = null;
		
		Iterator<CommandType> innerator = null;	//HA *dab*
		CommandType temp = null;
		
		boolean foundName = false;
		while (iterator.hasNext()){
			node = iterator.next();
			
			if (node.name.equals(newCommandType.name)){
				
				innerator = node.list.iterator();
				
				while (innerator.hasNext()){
					temp = innerator.next();
					
					if (temp.equals(newCommandType)){
						return false;
					}
				}
				
				
				foundName = true;
				break;
			}
		}
		
		
		if (!foundName){
			allCommandTypes.add(new CommandTypeNode(newCommandType.name, new LinkedList<>()));
			
			return addCommandTypeToKnowns(newCommandType);
		}
		else{
			node.list.add(newCommandType);
			
			return true;
		}
	}
	
	
	/**
	 * Gets an array of CommandType objects in the known CommandTypes of the wrapper by a given name. These 
	 * were added using the {@link #addCommandTypeToKnowns(CommandType)} method.
	 * 
	 * @param name String object containing the name of the desired CommandType group. All members of the group 
	 * will have a name that passes the String.equals check with the given name.
	 * @return Array containing all known CommandType objects with the given name. Null if there are none.
	 */
	public static CommandType[] getKnownCommandTypeGroup(String name){
		Iterator<CommandTypeNode> iterator = allCommandTypes.iterator();
		CommandTypeNode node = null;
		
		boolean found = false;
		while (iterator.hasNext()){
			node = iterator.next();
			
			if (node.name.equals(name)){
				found = true;
				
				break;
			}
		}
		
		
		if (found){
			CommandType[] returnArray = new CommandType[0];
			
			return node.list.toArray(returnArray);
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * Gets a copy of all the known CommandTypes as an array. These were added using the 
	 * {@link #addCommandTypeToKnowns(CommandType)} method.
	 * 
	 * @return Array containing all known CommandType objects.
	 */
	public static CommandType[] getKnownCommandTypes(){
		LinkedList<CommandType> returnList = new LinkedList<>();
		
		
		Iterator<CommandTypeNode> iterator = allCommandTypes.iterator();
		CommandTypeNode node = null;
		
		while(iterator.hasNext()){
			node = iterator.next();
			
			returnList.addAll(node.list);
		}
		
		CommandType[] temp = new CommandType[0];
		return returnList.toArray(temp);
	}
	
	
	/**
	 * Gets a String object containing the <em>name</em> of the CommandType.
	 * 
	 * @return String object containing the <em>name</em> of the CommandType.
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * Gets a clone of the arguments for the CommandType as a String array.
	 * 
	 * @return Clone of the arguments for the CommandType as a String array.
	 */
	public String[] getArguments(){
		return arguments.clone();
	}
	
	
	/**
	 * Gets the argument at the given index as a String object. If the index is out of bounds for the arguments 
	 * array, null will be returned instead.
	 * 
	 * @param index Integer representing the index of the argument to get.
	 * @return String object containing the argument at the given index. Null if the index is out of bounds.
	 */
	public String getArgument(int index){
		try{
			return arguments[index];
		}
		catch (IndexOutOfBoundsException e){
			return null;
		}
	}
	
	
	/**
	 * Gets the permissions of the CommandType.
	 * <p>
	 * This tells Anvil who can run this CommandType, and stops those who can't.
	 * 
	 * @return CommandPermissions enumeration that represent the permissions of the CommandType.
	 * 
	 * @see CommandPermissions
	 */
	public CommandPermissions getPermissions(){
		return permissions;
	}
	
	
	/**
	 * Gets the execution data of the CommandType.
	 * 
	 * @return CommandExecutionData object containing the execution data of the CommandType.
	 * 
	 * @see CommandExecutionData
	 */
	public CommandExecutionData getExecutionData(){
		return data;
	}
	
	
	/**
	 * Gets whether or not a given CommandType object is equal to the calling CommandType object.
	 * 
	 * @param other The object to compare to. Should be an instance of CommandType and not be null.
	 * @return Whether or not the given CommandType object is equal to the calling CommandType object.
	 */
	@Override
	public boolean equals(Object other){
		if (other == null){
			return false;
		}
		else if (!(other instanceof CommandType)){
			return false;
		}
		
		
		CommandType _other = (CommandType)other;
		
		if (name.equals(_other.name)){
			boolean booleanToReturn = true;
			
			for (int i = 0; i < _other.arguments.length; ++i){
				
				if (!arguments[i].equals(_other.arguments[i])){
					booleanToReturn = false;
					
					break;
				}
			}
			
			return booleanToReturn;
		}
		
		return false;
	}
	
	
	/**
	 * Gets a String object containing the CommandType with name, arguments, and delimiters.
	 * 
	 * @return String object containing the entire CommandType with name, arguments, and delimiters.
	 */
	@Override
	public String toString(){
		String stringToReturn = TYPE_DELIMITER + name;
		
		for (int i = 0; i < arguments.length; ++i){
			stringToReturn += ARG_DELIMITER + arguments[i];
		}
		
		return stringToReturn;
	}
	
	
//	public static void main(String[] args){
//		String[] yeet = {"\\p{ASCII}{1,}", "(we|you)"};
//		
//		CommandType test = new CommandType("help", yeet);
//		
//		CommandType.addCommandTypeToKnowns(test);
//		CommandType.addCommandTypeToKnowns(new CommandType("!ty pb BB"));
//		
//		CommandType[] yeetin = CommandType.getKnownCommandTypes();
//		
//		for (int i = 0; i < yeetin.length; ++i){
//			if (yeetin[i].passes("!help hey we")){
//				System.out.println(yeetin[i]);
//			}
//		}
		
//		System.out.println("name: " + test.name);
//		System.out.println("# of args: " + test.arguments.length);
//		
//		for (int i = 0; i < test.arguments.length; ++i){
//			System.out.println(test.arguments[i]);
//		}
//		
//		System.out.println(test);
//		
//		
//		System.out.println(CommandType.addCommandType(test));
//		System.out.println(CommandType.allCommandTypes.getFirst().list.getFirst());
//		
//		CommandType other = new CommandType("!help trevor");
//		
//		System.out.println(CommandType.addCommandType(other));
//		System.out.println(CommandType.allCommandTypes.getFirst().list.getLast());
//		
//		System.out.println(CommandType.allCommandTypes.getFirst().list.size());
		
		
//		CommandType.addCommandType(test);
//		
//		CommandType[] hey = CommandType.getKnownCommandTypeGroup("help");
//		
//		for (int i = 0; i < hey.length; ++i){
//			System.out.println(hey[i]);
//		}
//	}
	
	
	
	
	
	
	
	
	/**
	 * Represents a node for the allCommandTypes static variable. This allows allCommandTypes to function as 
	 * a linked dictionary, where the name is the key, and the LinkedList of CommandTypes is the value.
	 * 
	 * @author Garrett Stonis
	 * @version 1.0
	 */
	private static class CommandTypeNode{
		public String name;
		public LinkedList<CommandType> list;
		
		public CommandTypeNode(String name, LinkedList<CommandType> list){
			this.name = name;
			this.list = list;
		}
	}
}



















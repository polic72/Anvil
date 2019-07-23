package anvil.api.ServerOutputLine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anvil.api.PlugIn;
import anvil.api.Internal.Player;
import anvil.api.Internal.Runner;


/**
 * Represents a ServerOutputLine from the Minecraft server in a Runner. These are the lines that the server 
 * return to the user by running the server. They can be retrieved with the {@link Runner#getNextOutputLine()} 
 * method.
 * <p>
 * Minecraft servers give fairly useful information in their output lines. The only annoying thing is that are 
 * so many possibilities for types of output lines! To deal with this issue, you can create your own types by 
 * implementing the {@link ServerOutputLineType} interface. Or you can use one of the {@link Defaults}.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see ServerOutputLineType
 * @see Defaults
 * @see Runner
 */
public class ServerOutputLine{
	private static LinkedList<Class<? extends ServerOutputLineType>> known_types = new LinkedList<>();
	
	
	private ServerOutputLineType type;
	private String entireLine;
	
	private String timeStamp;
	private String threadStamp;
	
	private String contents;
	private String subContents;
	private Player causeUser;
	private Player recipient;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private boolean loaded_values = false;
	
	private static final String ENTIRE_REGEX = "\\[((0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])\\] "
			+ "\\[(\\p{ASCII}{1,})\\]: (\\p{ASCII}{1,})";
	
	
	/**
	 * Constructs a ServerOutputLine object with a ServerOutputLineType and the contents of the line.
	 * <p>
	 * While a ServerOutputLine object can be made anywhere, only Anvil's generated output lines will be passed 
	 * to each {@link PlugIn#interpretServerOutputLine(String, ServerOutputLine)} method.
	 * 
	 * @param entire_line String object containing every part of the ServerOutputLine. Literally the entire line 
	 * that the server returns.
	 * @param type The ServerOutputLineType enumeration that represents what kind of ServerOutputLine the 
	 * created ServerOutputLine will be. Null is a generic type.
	 * @throws IllegalArgumentException When the type doesn't match the entire_line.
	 */
	public ServerOutputLine(String entire_line, ServerOutputLineType type) throws IllegalArgumentException{
		entireLine = entire_line;
		
		pattern = Pattern.compile(ENTIRE_REGEX);
		matcher = pattern.matcher(entire_line);
		
		matcher.find();
		
		timeStamp = matcher.group(1);
		threadStamp = matcher.group(3);
		
		contents = matcher.group(4);
		
		
		if (type != null){
			if (!type.isLegal(contents)){
				throw new IllegalArgumentException("The given contents don't match the acceptedFormat of the "
						+ "given ServerOutputLineType.");
			}
			
			this.type = type;
		}
		else{
			this.type = null;
		}
	}
	
	
	/**
	 * Constructs a ServerOutputLine object with the contents of the line.
	 * <p>
	 * Searches through all the known ServerOutputLineTypes to find the correct match. Will be generic if a 
	 * suitable type can't be found. Uses the isLegal method with the contents to check.
	 * <p>
	 * While a ServerOutputLine object can be made anywhere, only Anvil's generated output lines will be passed 
	 * to each {@link PlugIn#interpretServerOutputLine(String, ServerOutputLine)} method.
	 * 
	 * @param entire_line String object containing every part of the ServerOutputLine. Literally what the entire 
	 * line that the server returns.
	 */
	public ServerOutputLine(String entire_line){
		entireLine = entire_line;
		
		pattern = Pattern.compile(ENTIRE_REGEX);
		matcher = pattern.matcher(entire_line);
		
		matcher.find();
		
		timeStamp = matcher.group(1);
		threadStamp = matcher.group(3);
		
		contents = matcher.group(4);
		
		
		Class<?>[] param_types = new Class<?>[1];
		param_types[0] = String.class;
		
		Object[] params = new Object[] {contents};
		
		for (Class<? extends ServerOutputLineType> cl : known_types){
			Method meth = null;
			
			try{
				meth = cl.getMethod("isLegal", param_types);
			}
			catch (NoSuchMethodException | SecurityException e1){
				//This can't happen.
			}
			
			boolean isLegal = false;
			
			for (Object obj : cl.getEnumConstants()){
				try{
					isLegal = (boolean)meth.invoke(obj, params);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
					//This can't happen.
				}
				
				
				if (isLegal){
					type = cl.cast(obj);
					
					break;
				}
			}
			
			
			if (type != null){
				break;
			}
		}
	}
	
	
	/**
	 * Gets the entire line of the ServerOutputLine. This is exactly what the server returns.
	 * 
	 * @return String object containing the entire line of the ServerOutputLine.
	 */
	public String getEntireLine(){
		return entireLine;
	}
	
	
	/**
	 * Gets the type of the ServerOutputLine.
	 * <p>
	 * The type is what distinguishes each output line from each other one. It identifies where the output 
	 * line's useful information is. Based on {@link #addKnownType(Class)}, each ServerOutputLineType must be an 
	 * enumeration, forcing simple classification. If the type is null, there was no known ServerOutputLineType 
	 * enumeration that properly classified the line, making it generic and locking most of its information.
	 * 
	 * @return The ServerOutputLineType representing the type of the ServerOutputLine. Null if generic.
	 * 
	 * @see ServerOutputLineType
	 */
	public ServerOutputLineType getType(){
		return type;
	}
	
	
	/**
	 * Gets the timeStamp of the ServerOutputLine.
	 * <p>
	 * The timeStamp is not necessarily the exact time the ServerOutputLine was created, but rather the time 
	 * the Minecraft server mentioned in the output line. ("[12:34:56] ...")
	 * 
	 * @return String object containing the timeStamp of the ServerOutputLine.
	 */
	public String getTimeStamp(){
		return timeStamp;
	}
	
	
	/**
	 * Gets the threadStamp of the ServerOutputLine.
	 * <p>
	 * The threadStamp is the thread that the Minecraft server mentions in the output line. ("... 
	 * [example thread/INFO]: ...")
	 * 
	 * @return String object containing the threadStamp of the ServerOutputLine.
	 */
	public String getThreadStamp(){
		return threadStamp;
	}
	
	
	/**
	 * Gets the contents of the ServerOutputLine.
	 * <p>
	 * The contents of the ServerOutputLine are everything that the Minecraft server gives per output line 
	 * <em>without</em> the timeStamp or threadStamp.
	 * 
	 * @return String object containing the contents of the ServerOutputLine.
	 */
	public String getContents(){
		return contents;
	}
	
	
	/**
	 * Gets the subContents of the ServerOutputLine.
	 * <p>
	 * For more information on subContents, see {@link ServerOutputLineType#getSubContentsIndex()}.
	 * 
	 * @return String object containing the subContents of the ServerOutputLine. Null if none.
	 * @throws IllegalStateException When the ServerOutputLine is generic.
	 */
	public String getSubContents(){
		if (type == null){
			throw new IllegalStateException("The ServerOutputLine is generic and has no inner data.");
		}
		
		
		if (!loaded_values){
			LoadValues();
			
			loaded_values = true;
		}
		
		return subContents;
	}
	
	
	/**
	 * Gets the causeUser of the ServerOutputLine. Null means that the server itself caused the line.
	 * <p>
	 * For more information on causeUser, see {@link ServerOutputLineType#getCauseUserIndex()}.
	 * 
	 * @return Player object containing the causeUser of the ServerOutputLine. Null if none.
	 * @throws IllegalStateException When the ServerOutputLine is generic.
	 */
	public Player getCauseUser(){
		if (type == null){
			throw new IllegalStateException("The ServerOutputLine is generic and has no inner data.");
		}
		
		
		if (!loaded_values){
			LoadValues();
			
			loaded_values = true;
		}
		
		return causeUser;
	}
	
	
	/**
	 * Gets the recipient of the ServerOutputLine.
	 * <p>
	 * For more information on recipient, see {@link ServerOutputLineType#getRecipientIndex()}.
	 * 
	 * @return Player object containing the recipient of the ServerOutputLine. Null if none.
	 * @throws IllegalStateException When the ServerOutputLine is generic.
	 */
	public Player getRecipient(){
		if (type == null){
			throw new IllegalStateException("The ServerOutputLine is generic and has no inner data.");
		}
		
		
		if (!loaded_values){
			LoadValues();
			
			loaded_values = true;
		}
		
		return recipient;
	}
	
	
	/**
	 * Gets whether or not the ServerOutputLine is a death message.
	 * 
	 * @return Whether or not the ServerOutputLine is a death message.
	 * @throws IllegalStateException When the ServerOutputLine is generic.
	 */
	public boolean isDeathMessage(){
		if (type == null){
			throw new IllegalStateException("The ServerOutputLine is generic and has no inner data.");
		}
		
		return type.isDeathMessage();
	}
	
	
	/**
	 * Loads all the inner values into the object. This isn't done in the constructor just in case the data 
	 * isn't used later on and wastes processor power.
	 * 
	 * @throws IllegalArgumentException When the Players don't load properly.
	 */
	private void LoadValues() throws IllegalArgumentException{
		pattern = Pattern.compile(type.getAcceptedFormat());
		matcher = pattern.matcher(contents);
		matcher.find();
		
		if (type.getSubContentsIndex() == -1){
			subContents = null;
		}
		else{
			subContents = matcher.group(type.getSubContentsIndex());
		}
		
		try {
			if (type.getRecipientIndex() == -1){
				recipient = null;
			}
			else{
				recipient = new Player(matcher.group(type.getRecipientIndex()));
			}
			
			if (type.getCauseUserIndex() == -1){
				causeUser = null;
			}
			else{
				causeUser = new Player(matcher.group(type.getCauseUserIndex()));
			}	
		}
		catch (IOException e){
			//If this happens, you have more to worry about.
		}
	}
	
	
	/**
	 * Adds a new instance of a ServerOutputLineType to Anvil. Cannot be undone.
	 * <p>
	 * All new ServerOutputLineTypes must extend the Enum class (being an enum counts), and implement the 
	 * ServerOutputLineType interface to be added.
	 * <p>
	 * All of the known types will be checked to see if any of them pertain to the contents given. The Defaults 
	 * are checked last, but then the load order is as random as the PlugIns being loaded. To do this properly,
	 * all additions should be made in the {@link PlugIn#onLoad()} method.
	 * <p>
	 * Some notes on creating unobtrusive ServerOutputLineTypes:
	 * <ul>
	 * <li>Make the acceptedFormats as specific as possible, as not to take over unintentional lines.</li>
	 * <li>Take full advantage of regex patterns, don't make a bunch of enumerations where they aren't needed.
	 * </li>
	 * <li>List any added types for other developers/users. Anvil will use the first match it comes across every 
	 * time. Can cause confusion if developers/users were expecting to use 1 specific type.</li>
	 * <li>Don't try to overwrite other types, apart from the Defaults. Even then, only overwrite them if you 
	 * need functionality that the Defaults don't provide in their current state.</li>
	 * <li>Don't do anything too fancy in the ServerOutputLineType method overrides, everything complicated is 
	 * handled by {@link ServerOutputLine} anyways.</li>
	 * </ul>
	 * 
	 * @param <T> Any class that extends Enum (being an enum counts) and implements the ServerOutputLineType 
	 * interface.
	 * @param newType The class of the new ServerOutputLineType to add to the known list.
	 */
	public static <T extends Enum<?> & ServerOutputLineType> void addKnownType(Class<T> newType){
		known_types.add(newType);
	}
	
	
	
	
	
//	public static void main(String[] args){
//		ServerOutputLine.addKnownType(Defaults.class);
////		ServerOutputLine.addKnownType(Player.class);
//		
//		
//		ServerOutputLine num1 = new ServerOutputLine("[19:37:21] [Beter]: Done (15.559s)! For help, type "
//				+ "\"help\"", Defaults.DONE_LOADING);
//		
//		System.out.println("|" + num1.getSubContents() + "|");
//		
//		
//		ServerOutputLine num2 = new ServerOutputLine("[19:37:21] [Beter]: [polic72: Made polic72 no longer a "
//				+ "server operator]");
//		
//		System.out.println("|" + num2.type + "|");
//		
//		
////		//This is a compilation error; the file originally there was a regular class implementing the 
////		//interface.
////		ServerOutputLine.addKnownType(Tetsing.class);
////		
////		ServerOutputLine num3 = new ServerOutputLine("[19:37:21] [Beter]: Beter");
////		
////		System.out.println("|" + num3.type.getClass().isEnum() + "|");
//	}
}























package anvil.api.ServerOutputLine;


/**
 * Represents an interface for enums that contain ServerOutputLine entries.
 * <p>
 * All ServerOutputLine enums must implement this interface to successfully represent ServerOutputLines.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see ServerOutputLine
 */
public interface ServerOutputLineType{
	
	/**
	 * Gets the accepted format for this ServerOutputLineType enumeration as a String object.
	 * <p>
	 * The acceptedFormat is the regex pattern for the entire ServerOutputLine <em>without</em> the timeStamp or 
	 * threadStamp ("[00:00:00] [example thread/INFO]: ") at the beginning. This is NOT the contents of the 
	 * ServerOutputLine, but rather the regex pattern that the contents should be able to match.
	 * <p>
	 * Example:
	 * <ul>
	 * <li>ServerOutputLine = "[12:34:56] [Server thread/INFO]: Made user123 a server operator"</li>
	 * <li>contents = "Made user123 a server operator"</li>
	 * <li>acceptedFormat = "Made " + Player.USERNAME_PATTERN + " a server operator"
	 * </ul>
	 * 
	 * @return String object containing the accepted format for this ServerOutputLineType enumeration.
	 * 
	 * @see ServerOutputLine#getContents()
	 */
	public String getAcceptedFormat();
	
	
	/**
	 * Gets the index of the sub-contents of the ServerOutputLine.
	 * <p>
	 * The subContentsIndex is a number that tells which regex group in the acceptedFormat references the 
	 * sub-contents of the ServerOutputLine. -1 means there is none.
	 * <p>
	 * The subContents of ServerOutputLine has no distinct convention to follow, meaning it can store any 
	 * information that the PlugIn creator desires. An example use of it is to store the name of the weapon a 
	 * Player/mob used to kill a Player. In fact, this is exactly what applicable death messages do when handled 
	 * by the Defaults.
	 * 
	 * @return Integer representing the index of the subContents of the ServerOutputLine.
	 * 
	 * @see Defaults#DEATH_SLAIN_USING
	 * @see ServerOutputLine#getSubContents()
	 */
	public int getSubContentsIndex();
	
	
	/**
	 * Gets the index of the causeUser of the ServerOutputLine.
	 * <p>
	 * The causeUserIndex is a number that tells which regex group in the acceptedFormat references the causing 
	 * user. -1 means there is none (caused by the server itself).
	 * <p>
	 * The causeUser of a ServerOutputLine is the Player that caused the ServerOutputLine to be printed. This 
	 * can mean anything from the caller of the command to the Player that was speaking.
	 * <p>
	 * Example from Minecraft Server command-line:
	 * <ul>
	 * <li>command = "op user123"</li>
	 * <li>ServerOutputLine = "[12:34:56] [Server thread/INFO]: Made user123 a server operator"</li>
	 * <li>causeUser = "[Server]"</li>
	 * </ul>
	 * <p>
	 * Example from in-game command:
	 * <ul>
	 * <li>command = "/op user123"</li>
	 * <li>ServerOutputLine = "[12:34:56] [Server thread/INFO]: [other_user: Made user123 a server 
	 * operator]"</li>
	 * <li>causeUser = "other_user"</li>
	 * </ul>
	 * 
	 * @return Integer representing the index of the recipient of the ServerOutputLine.
	 * 
	 * @see ServerOutputLine#getCauseUser()
	 */
	public int getCauseUserIndex();
	
	
	/**
	 * Gets the index of the recipient of the ServerOutputLine.
	 * <p>
	 * The recipientIndex is a number that tells which regex group in the acceptedFormat references the 
	 * recipient user. -1 means there is none.
	 * <p>
	 * The recipient of a ServerOutputLine is the Player that receives the reason the ServerOutputLine 
	 * appeared. Think of this as a command, and the recipient is the Player that gets the command done to them.
	 * <p>
	 * Example from Minecraft Server command-line:
	 * <ul>
	 * <li>command = "op user123"</li>
	 * <li>ServerOutputLine = "[12:34:56] [Server thread/INFO]: Made user123 a server operator"</li>
	 * <li>recipient = "user123"</li>
	 * </ul>
	 * <p>
	 * Example from in-game command:
	 * <ul>
	 * <li>command = "/op user123"</li>
	 * <li>ServerOutputLine = "[12:34:56] [Server thread/INFO]: [other_user: Made user123 a server 
	 * operator]"</li>
	 * <li>recipient = "user123"</li>
	 * </ul>
	 * 
	 * @return Integer representing the index of the recipient of the ServerOutputLine.
	 * 
	 * @see ServerOutputLine#getRecipient()
	 */
	public int getRecipientIndex();
	
	
	/**
	 * Checks to see if the given <em>ServerOutputLineString</em> is legal.
	 * <p>
	 * This should only check if the the given String object matches the acceptedFormat.
	 * 
	 * @param ServerOutputLineString String object containing the given <em>ServerOutputLineString</em> to check.
	 * @return Whether of not the given <em>ServerOutputLineString</em> is legal.
	 */
	public boolean isLegal(String ServerOutputLineString);
	
	
	/**
	 * Tells whether or not this ServerOutputLineType is a death message.
	 * <p>
	 * A death message is simply something the Minecraft server tells everyone when someone dies from some 
	 * reason. The purpose of this is to better streamline events that occur when a Player dies, which tends to 
	 * be a common ServerOutputLine PlugIn feature.
	 * <p>
	 * All the default Minecraft death messages (as of Minecraft 1.14) are handled in the Defaults.
	 * 
	 * @return Whether or not this ServerOutputLineType is a death message.
	 * 
	 * @see Defaults
	 */
	public boolean isDeathMessage();
}




















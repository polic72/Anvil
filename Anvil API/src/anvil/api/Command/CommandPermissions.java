package anvil.api.Command;


/**
 * Represents the permission levels of Players using the command being created.
 * <p>
 * When creating a CommandType, the permission represents who can use the command.
 * <ul>
 * <li>Server: Only the wrapper can use the command.</li>
 * <li>Admin: The wrapper and any opped Players can use the command.</li>
 * <li>Player: The wrapper and any Player of any kind can use the command.</li>
 * </ul>
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see CommandType
 */
public enum CommandPermissions{
	/**
	 * Only the wrapper can use the command.
	 */
	SERVER,
	
	/**
	 * The wrapper and any opped Players can use the command.
	 */
	ADMIN,
	
	/**
	 * The wrapper and any Player of any kind can use the command.
	 */
	PLAYER
}



















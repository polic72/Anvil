package anvil.api.ServerOutputLine;

import anvil.api.Internal.Player;


/**
 * Represents all the default recognized ServerOutputLines for the Minecraft Server.
 * <p>
 * This enum class implements the {@link ServerOutputLineType} interface, and therefore follows all its 
 * conventions.
 * <p>
 * These Defaults are interpreted last in the ServerOutputLine listener, meaning that each enumeration can be 
 * overwritten as necessary.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see ServerOutputLineType
 * @see ServerOutputLine
 */
public enum Defaults implements ServerOutputLineType{
	
	// {{ Commands
	
	/**
	 * When a Player is banned by the Server.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being banned.
	 * <br/>
	 * <b>SubContents:</b> The reason the player was banned.
	 */
	BAN_SERVER("Banned " + Player.USERNAME_PATTERN + ": (\\p{ASCII}{1,})", -1, 1, 2),
	
	/**
	 * When a Player is banned by another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player banning the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player being banned.
	 * <br/>
	 * <b>SubContents:</b> The reason the player was banned.
	 */
	BAN_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Banned " + Player.USERNAME_PATTERN
			+ ": (\\p{ASCII}{1,})\\]", 1, 2, 3),
	
	
	/**
	 * When a Player is de-opped by the Server.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being de-opped.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEOPPED_SERVER("Made " + Player.USERNAME_PATTERN + " no longer a server operator", -1, 1, -1),
	
	/**
	 * When a Player is de-opped by another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player de-opping the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player being de-opped.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEOPPED_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Made " + Player.USERNAME_PATTERN
			+ " no longer a server operator\\]", 1, 2, -1),
	
	
	/**
	 * When the Server sets a Player's game mode.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player whose game mode is changing.
	 * <br/>
	 * <b>SubContents:</b> The game mode the Player is changing to.
	 */
	GAMEMODE_SERVER("Set " + Player.USERNAME_PATTERN + "'s game mode to "
			+ "(Survival|Creative|Adventure|Spectator) Mode", -1, 1, 2),
	
	/**
	 * When a Player sets another Player's game mode.
	 * <p>
	 * <b>CauseUser:</b> The Player changing the other Player's game mode.
	 * <br/>
	 * <b>Recipient:</b> The Player whose game mode is changing.
	 * <br/>
	 * <b>SubContents:</b> The game mode the Player is changing to.
	 */
	GAMEMODE_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Set " + Player.USERNAME_PATTERN + "'s game mode to "
			+ "(Survival|Creative|Adventure|Spectator) Mode\\]", 1, 2, 3),
	
	/**
	 * When a Player sets their own game mode.
	 * <p>
	 * <b>CauseUser:</b> The Player changing the other Player's game mode (same as Recipient).
	 * <br/>
	 * <b>Recipient:</b> The Player whose game mode is changing (same as CauseUser).
	 * <br/>
	 * <b>SubContents:</b> The game mode the Player is changing to.
	 */
	GAMEMODE_PLAYER_SELF("\\[" + Player.USERNAME_PATTERN + ": Set own game mode to "
			+ "(Survival|Creative|Adventure|Spectator) Mode\\]", 1, 1, 2),
	
	
	/**
	 * When the Server gives a Player an item.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player receiving the item(s).
	 * <br/>
	 * <b>SubContents:</b> The name of the item(s) being given.
	 */
	GAVE_SERVER("Gave \\p{Digit}{1,} \\[(\\p{Alpha}{1,})\\] to " + Player.USERNAME_PATTERN, -1, 2, 1),
	
	/**
	 * When a Player gives another Player an item.
	 * <p>
	 * <b>CauseUser:</b> The Player giving the items to the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player receiving the item(s).
	 * <br/>
	 * <b>SubContents:</b> The name of the item(s) being given.
	 */
	GAVE_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Gave \\p{Digit}{1,} \\[(\\p{ASCII}{1,})\\] to "
			+ Player.USERNAME_PATTERN + "\\]", 1, 3, 2),
	
	
	/**
	 * When a Player is unbanned by the Server.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being unbanned.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	PARDON_SERVER("Unbanned " + Player.USERNAME_PATTERN, -1, 1, -1),
	
	
	/**
	 * When a Player is unbanned by another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player unbanning the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player being unbanned.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	PARDON_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Unbanned " + Player.USERNAME_PATTERN + "\\]",
			1, 2, -1),
	
	
	/**
	 * When the Server reloads the data packs.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	RELOADING_SERVER("Reloading!", -1, -1, -1),
	
	/**
	 * When a Player reloads the data packs.
	 * <p>
	 * <b>CauseUser:</b> The Player reloading the data packs.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	RELOADING_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Reloading!\\]", 1, -1, -1),
	
	
	/**
	 * When the Server opps a Player.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being opped.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	OPPED_SERVER("Made " + Player.USERNAME_PATTERN + " a server operator", -1, 1, -1),
	
	/**
	 * When a Player opps another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player opping the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player being opped.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	OPPED_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Made " + Player.USERNAME_PATTERN
			+ " a server operator\\]", 1, 2, -1),
	
	
	/**
	 * When the Server saves the game.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	SAVED_SERVER("Saved the game", -1, -1, -1),
	
	/**
	 * When a Player saves the game.
	 * <p>
	 * <b>CauseUser:</b> The Player saving the game.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	SAVED_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Saved the game\\]", 1, -1, -1),
	
	/**
	 * When the game is being saved.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	SAVING("Saving the game \\(this may take a moment!\\)", -1, -1, -1),
	
	
	/**
	 * When the Server gives a Player a tag.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player receiving the tag.
	 * <br/>
	 * <b>SubContents:</b> The name of the tag being given.
	 */
	TAG_ADD_SERVER("Added tag \'(\\p{ASCII}{1,})\' to " + Player.USERNAME_PATTERN, -1, 2, 1),
	
	/**
	 * When a Player gives another Player a tag.
	 * <p>
	 * <b>CauseUser:</b> The Player giving the other Player the tag.
	 * <br/>
	 * <b>Recipient:</b> The Player receiving the tag.
	 * <br/>
	 * <b>SubContents:</b> The name of the tag being given.
	 */
	TAG_ADD_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Added tag \'(\\p{ASCII}{1,})\' to "
			+ Player.USERNAME_PATTERN + "\\]", 1, 3, 2),
	
	/**
	 * When the Server removes a tag from a Player.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player losing the tag.
	 * <br/>
	 * <b>SubContents:</b> The name of the tag being removed.
	 */
	TAG_REMOVE_SERVER("Removed tag \'(\\p{ASCII}{1,})\' from " + Player.USERNAME_PATTERN, -1, 2, 1),
	
	/**
	 * When a Player removes a tag from another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player removing the tag from the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player losing the tag.
	 * <br/>
	 * <b>SubContents:</b> The name of the tag being removed.
	 */
	TAG_REMOVE_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Removed tag \'(\\p{ASCII}{1,})\' from "
			+ Player.USERNAME_PATTERN + "\\]", 1, 3, 2),
	
	
	/**
	 * When the Server teleports a Player to another Player.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being teleported.
	 * <br/>
	 * <b>SubContents:</b> The Player that is being teleported to.
	 */
	TELEPORT_SERVER("Teleported " + Player.USERNAME_PATTERN + " to " + Player.USERNAME_PATTERN, -1, 1, 2),
	
	/**
	 * When a Player teleports a Player to another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player teleporting the Player to the other Player. (<em>YEET</em>)
	 * <br/>
	 * <b>Recipient:</b> The Player being teleported.
	 * <br/>
	 * <b>SubContents:</b> The Player that is being teleported to.
	 */
	TELEPORT_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Teleported " + Player.USERNAME_PATTERN + " to "
			+ Player.USERNAME_PATTERN + "\\]", 1, 2, 3),
	
	
	/**
	 * When the Server sets the time of the world.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The time the world is being set to.
	 */
	TIMESET_SERVER("Set the time to (\\p{Alnum}{1,})", -1, -1, 1),
	
	/**
	 * When a Player sets the time of the world.
	 * <p>
	 * <b>CauseUser:</b> The Player setting the time of the world.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The time the world is being set to.
	 */
	TIMESET_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Set the time to (\\p{Alnum}{1,})\\]", 1, -1, 2),
	
	
	/**
	 * When the Server sets the weather.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The weather the world is being set to.
	 */
	WEATHER_SERVER("Set the weather to (clear|rain|rain & thunder)", -1, -1, 1),
	
	/**
	 * When a Player sets the weather.
	 * <p>
	 * <b>CauseUser:</b> The Player setting the weather of the world.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The weather the world is being set to.
	 */
	WEATHER_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Set the weather to (clear|rain|rain & thunder)\\]", 
			1, -1, 2),
	
	
	/**
	 * When a Player is white-listed by the Server.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being white-listed.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	WHITELIST_ADD_SERVER("Added " + Player.USERNAME_PATTERN + "to the whitelist", -1, 1, -1),
	
	/**
	 * When a Player is white-listed by another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player white-listing the other Player.
	 * <br/>
	 * <b>Recipient:</b> The Player being white-listed.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	WHITELIST_ADD_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Added " + Player.USERNAME_PATTERN
			+ "to the whitelist\\]", 1, 2, -1),
	
	
	/**
	 * When a Player is removed from the white-list by the Server.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> The Player being removed from the white-list.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	WHITELIST_REMOVE_SERVER("Removed " + Player.USERNAME_PATTERN + "from the whitelist", -1, 1, -1),
	
	/**
	 * When a Player is removed from the white-list by another Player.
	 * <p>
	 * <b>CauseUser:</b> The Player removing the other Player from the white-list.
	 * <br/>
	 * <b>Recipient:</b> The Player being removed from the white-list.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	WHITELIST_REMOVE_PLAYER("\\[" + Player.USERNAME_PATTERN + ": Removed " + Player.USERNAME_PATTERN
			+ "from the whitelist\\]", 1, 2, -1),
	
	// }} Commands
	
	
	
	
	
	
	
	// {{ Player-based
	
	/**
	 * When a Player makes an advancement.
	 * <p>
	 * <b>CauseUser:</b> The Player that made the advancement (same as Recipient).
	 * <br/>
	 * <b>Recipient:</b> The Player receiving the advancement (same as CauseUser).
	 * <br/>
	 * <b>SubContents:</b> The advancement the Player achieved.
	 */
	PLAYER_ADVANCEMENT(Player.USERNAME_PATTERN + " has made the advancement \\[(\\p{ASCII}{1,})\\]",
			1, 1, 2),
	
	
	/**
	 * When a Player joins the Server.
	 * <p>
	 * <b>CauseUser:</b> The Player that joined the game.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	PLAYER_JOINED(Player.USERNAME_PATTERN + " joined the game", 1, -1, -1),
	
	/**
	 * When a Player leaves the Server.
	 * <p>
	 * <b>CauseUser:</b> The Player that left the game.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	PLAYER_LEFT(Player.USERNAME_PATTERN + " left the game", 1, -1, -1),
	
	
	/**
	 * When a Player logs in to the Server.
	 * <p>
	 * <b>CauseUser:</b> The Player that logged in to the Server.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The IP address of the Player logging in.
	 */
	PLAYER_LOGGED_IN(Player.USERNAME_PATTERN + "\\[(\\p{ASCII}{9,})\\] logged in with entity id \\d{1,} at "
			+ "\\p{ASCII}{1,}", 1, -1, 2),
	
	/**
	 * When a Player loses connection to the Server.
	 * <p>
	 * <b>CauseUser:</b> The Player that lost connection to the Server.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The reason the Player lost connection.
	 */
	PLAYER_LOST_CONNECTION(Player.USERNAME_PATTERN + " lost connection: (\\p{Alpha}{1,})", 1, -1, 2),
	
	
	/**
	 * When a Player uses the /say command to say something.
	 * <p>
	 * <b>CauseUser:</b> The Player using the /say command.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> What the Player said.
	 */
	PLAYER_SAY_MESSAGE("\\[" + Player.USERNAME_PATTERN + "\\] (\\p{ASCII}{1,})", 1, -1, 2),
	
	/**
	 * When a Player says something.
	 * <p>
	 * <b>CauseUser:</b> The Player saying something.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> What the Player said.
	 */
	PLAYER_SPOKEN_MESSAGE("<" + Player.USERNAME_PATTERN + "> \\p{ASCII}{1,}", 1, -1, 2),
	
	
	/**
	 * When a Player logs in to the Server, and the Server checks the UUID of the Player.
	 * <p>
	 * <b>CauseUser:</b> The Player logging in to the Server.
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The UUID of the Player.
	 */
	PLAYER_UUID("UUID of player " + Player.USERNAME_PATTERN + " is " + Player.UUID_PATTERN, 1, -1, 2),
	
	
	// }} Player-based
	
	
	
	
	
	
	
	// {{ Deaths
	
	/**
	 * When a Player is killed by arrows shot by a dispenser or /summon.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_ARROW(Player.USERNAME_PATTERN + " was shot by arrow", -1, 1, -1),
	
	/**
	 * When a Player is killed by arrows shot by another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> What the Player/mob killed the Player with.
	 */
	DEATH_SHOT_BOW(Player.USERNAME_PATTERN + " was shot by " + Player.USERNAME_PATTERN
			+ " using (\\p{ASCII}{1,})", 2, 1, 3),
	
	/**
	 * When a Player is killed by arrows shot by another Player/mob with a renamed bow.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SHOT(Player.USERNAME_PATTERN + " was shot by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by a cactus.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_PRICK(Player.USERNAME_PATTERN + "was pricked to death", -1, 1, -1),
	
	/**
	 * When a Player is killed by a cactus.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_HUG(Player.USERNAME_PATTERN + " hugged a cactus", -1, 1, -1),
	
	/**
	 * When a Player is killed by a cactus and another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_WALK_CAC(Player.USERNAME_PATTERN + " walked into a cactus while trying to escape "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by dragon's breath.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_DRAGONS_BREATH(Player.USERNAME_PATTERN + " was roasted in dragon breath", -1, 1, -1),
	
	/**
	 * When a Player is killed by dragon's breath and another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_DRAGONS_BREATH_BY(Player.USERNAME_PATTERN + " was roasted in dragon breath by "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by drowning.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_DROWNED(Player.USERNAME_PATTERN + " drowned", -1, 1, -1),
	
	/**
	 * When a Player is killed by drowning and another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_DROWNED_ESC(Player.USERNAME_PATTERN + " drowned whilst trying to escape "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by suffocation (being in a solid block).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SUFFOCATE(Player.USERNAME_PATTERN + " suffocated in a wall", -1, 1, -1),
	
	
	/**
	 * When a Player is killed by entity cramming damage.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUISH(Player.USERNAME_PATTERN + " was squished too much", -1, 1, -1),
	
	/**
	 * When a Player is killed by entity cramming damage (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUASH(Player.USERNAME_PATTERN + " was squashed by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by a high-speed collision with elytra.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_KINETIC(Player.USERNAME_PATTERN + " experienced kinetic energy", -1, 1, -1),
	
	/**
	 * When a Player is killed by removing elytra while flying.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_RMV_ELYTRA(Player.USERNAME_PATTERN + " removed an elytra while flying", -1, 1, -1),
	
	
	/**
	 * When a Player is killed by tnt (activated by redstone, fire, etc.) or an end crystal.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BLEW_UP(Player.USERNAME_PATTERN + " blew up", -1, 1, -1),
	
	/**
	 * When a Player is killed by tnt (activated by Player/mob) or a creeper explosion.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BLOWN_UP(Player.USERNAME_PATTERN + " was blown up by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by a bed explosion in the Nether/End.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_GAME_DESIGN(Player.USERNAME_PATTERN + " was killed by \\[Intentional Game Design\\]", -1, 1, -1),
	
	
	/**
	 * When a Player is killed by a short fall, ender pearl damage, or riding a falling entity.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_GROUND(Player.USERNAME_PATTERN + " hit the ground too hard", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling from a high place.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_PLACE(Player.USERNAME_PATTERN + " fell from a high place", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling off a ladder.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_LAD(Player.USERNAME_PATTERN + " fell off a ladder", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling off vines.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_VINE(Player.USERNAME_PATTERN + " fell off some vines", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling out of water (like from a waterfall).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_WATER(Player.USERNAME_PATTERN + " fell out of the water", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling into fire (unknown actually).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_FIRE(Player.USERNAME_PATTERN + " fell into a patch of fire", -1, 1, -1),
	
	/**
	 * When a Player is killed by falling to their doom (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_DOOM(Player.USERNAME_PATTERN + " was doomed to fall by " + Player.USERNAME_PATTERN, 
			2, 1, -1),
	
	/**
	 * When a Player is killed by falling into cactus (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_CAC(Player.USERNAME_PATTERN + " fell into a patch of cacti", -1, 1, -1),
	
	/**
	 * When a Player is killed by being shot off of vines by a Player/mob (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SHOT_VINE(Player.USERNAME_PATTERN + " was shot off some vines by " + Player.USERNAME_PATTERN,
			2, 1, -1),
	
	/**
	 * When a Player is killed by being shot off of a ladder by a Player/mob (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SHOT_LAD(Player.USERNAME_PATTERN + " was shot off a ladder by " + Player.USERNAME_PATTERN, 
			2, 1, -1),
	
	/**
	 * When a Player is killed by a falling from creeper explosion, ghast fireball, or Player-lit tnt.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BLOWN_PLACE(Player.USERNAME_PATTERN + " was blown from a high place by " + Player.USERNAME_PATTERN,
			2, 1, -1),
	
	
	/**
	 * When a Player is killed by a falling anvil.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUASH_ANV(Player.USERNAME_PATTERN + " was squashed by a falling anvil", -1, 1, -1),
	
	/**
	 * When a Player is killed by a falling anvil while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUASH_ANV_FIGHT(Player.USERNAME_PATTERN + " was squashed by a falling anvil whilst fighting "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by a custom block that inflicts damage when falling.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUASH_BLCK(Player.USERNAME_PATTERN + " was squashed by a falling block", -1, 1, -1),
	
	/**
	 * When a Player is killed by a custom block that inflicts damage when falling, and while fighting 
	 * another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SQUASH_BLCK_FIGHT(Player.USERNAME_PATTERN + " was squashed by a falling block whilst fighting "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by fire (specifically the fire-block damage, not burning damage).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FLAMES(Player.USERNAME_PATTERN + " went up in flames", -1, 1, -1),
	
	/**
	 * When a Player is killed by fire (specifically the burning damage, not fire-block damage).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BURN(Player.USERNAME_PATTERN + " burned to death", -1, 1, -1),
	
	/**
	 * When a Player is killed by fire (specifically the burning damage, not fire-block damage) while fighting  
	 * another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_CRISP(Player.USERNAME_PATTERN + " was burnt to a crisp whilst fighting " + Player.USERNAME_PATTERN,
			2, 1, -1),
	
	/**
	 * When a Player is killed by fire (specifically the fire-block damage, not burning damage) while fighting 
	 * another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_WALK_FIRE(Player.USERNAME_PATTERN + " walked into a fire whilst fighting " + Player.USERNAME_PATTERN,
			2, 1, -1),
	
	
	/**
	 * When a Player is killed by a firework explosion.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BANG(Player.USERNAME_PATTERN + " went off with a bang", -1, 1, -1),
	
	/**
	 * When a Player is killed by a firework explosion while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BANG_FIGHT(Player.USERNAME_PATTERN + " went off with a bang whilst fighting "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by lava (specifically the lava-block damage, not burning damage).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_LAVA(Player.USERNAME_PATTERN + " tried to swim in lava", -1, 1, -1),
	
	/**
	 * When a Player is killed by lava (specifically the lava-block damage, not burning damage) while fighting 
	 * another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_LAVA_ESC(Player.USERNAME_PATTERN + " tried to swim in lava while trying to escape "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by lightning.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_LIGHT(Player.USERNAME_PATTERN + " was struck by lightning", -1, 1, -1),
	
	/**
	 * When a Player is killed by lightning while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_LIGHT_FIGHT(Player.USERNAME_PATTERN + " was struck by lightning", 2, 1, -1),
	
	
	/**
	 * When a Player is killed by magma-block damage.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FLOOR_LAVA(Player.USERNAME_PATTERN + " discovered floor was lava", -1, 1, -1),
	
	/**
	 * When a Player is killed by magma-block damage while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_DNGR_ZONE(Player.USERNAME_PATTERN + " walked into danger zone due to " + Player.USERNAME_PATTERN,
			2, -1, -1),
	
	
	/**
	 * When a Player is killed by another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_SLAIN(Player.USERNAME_PATTERN + " was slain by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by another Player/mob using a renamed weapon.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_SLAIN_USING(Player.USERNAME_PATTERN + " was slain by " + Player.USERNAME_PATTERN
			+ " using (\\p{ASCII}{1,})", 2, 1, 3),
	
	/**
	 * When a Player is killed by another Player/mob (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FINISH_OFF(Player.USERNAME_PATTERN + " got finished off by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by another Player/mob using a renamed weapon (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_FINISH_OFF_USING(Player.USERNAME_PATTERN + " got finished off by " + Player.USERNAME_PATTERN
			+ " using (\\p{ASCII}{1,})", 2, 1, 3),
	
	
	/**
	 * When a Player is killed by a fireball (specifically the damage from the fireball itself).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FIREBALL(Player.USERNAME_PATTERN + " was fireballed by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by a fireball (specifically the damage from the fireball itself) while the enemy 
	 * uses a renamed weapon. Talk about rare!
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_FIREBALL_USING(Player.USERNAME_PATTERN + " was fireballed by " + Player.USERNAME_PATTERN
			+ " using (\\p{ASCII}{1,})", 2, 1, 3),
	
	
	/**
	 * When a Player is killed by a potion of harming from a dispenser, drinking it, or the /effect command.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_MAGIC(Player.USERNAME_PATTERN + " was killed by magic", -1, 1, -1),
	
	/**
	 * When a Player is killed by a potion of harming from another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_MAGIC_USING(Player.USERNAME_PATTERN + " was killed by " + Player.USERNAME_PATTERN + " using magic",
			2, 1, -1),
	
	
	/**
	 * When a Player is killed by a witch, guardian, evoker, ranged ender dragon attack, or a splash potion 
	 * (that last one might not be true).
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> What the Player was killed with.
	 */
	DEATH_KILLED_USING(Player.USERNAME_PATTERN + " was killed by " + Player.USERNAME_PATTERN + " using "
			+ "(\\p{ASCII}{1,})", 2, 1, 3),
	
	
	/**
	 * When a Player is killed by starvation.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_STARVE(Player.USERNAME_PATTERN + " starved to death", -1, 1, -1),
	
	
	/**
	 * When a Player is killed by a sweet berry bush.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BERRY(Player.USERNAME_PATTERN + " was poked to death by a sweet berry bush", -1, 1, -1),
	
	/**
	 * When a Player is killed by a sweet berry bush while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_BERRY_ESC(Player.USERNAME_PATTERN + " was poked to death by a sweet berry bush whilst trying to "
			+ "escape " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by thorns (the enchantment or guardian) damage.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player. Yes, even though the recipient is the 
	 * instigator.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_HURT_TRY(Player.USERNAME_PATTERN + " was killed while trying to hurt " + Player.USERNAME_PATTERN,
			2, 1, -1),
	
	/**
	 * When a Player is killed by thorns (the enchantment or guardian) damage while using a renamed weapon.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player. Yes, even though the recipient is the 
	 * instigator.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_HURT_TRY_WITH(Player.USERNAME_PATTERN + " was killed by (\\p{ASCII}{1,}) trying to hurt "
			+ Player.USERNAME_PATTERN,
			3, 1, 2),
	
	
	/**
	 * When a Player is killed by trident thrown from another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_IMPALED(Player.USERNAME_PATTERN + " was impaled by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by a renamed trident thrown from another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_IMPALED_WITH(Player.USERNAME_PATTERN + " was impaled by " + Player.USERNAME_PATTERN
			+ " with (\\p{ASCII}{1,})", 2, 1, 3),
	
	
	/**
	 * When a Player is killed by falling into the void, or killed with the /kill command.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_WORLD(Player.USERNAME_PATTERN + " fell out of the world", -1, 2, -1),
	
	/**
	 * When a Player is killed by falling into the void from a high place (actually unknown).
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_FELL_PLC_WRLD(Player.USERNAME_PATTERN + " fell from a high place and fell out of the world",
			-1, 1, -1),
	
	/**
	 * When a Player is killed by falling into the void, or killed with the /kill command, after being hit by 
	 * another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_LIVE_SAME_WRLD(Player.USERNAME_PATTERN + " didn't want to live in the same world as "
			+ Player.USERNAME_PATTERN, 2, 1, -1),
	
	
	/**
	 * When a Player is killed by the wither effect.
	 * <p>
	 * <b>CauseUser:</b> [none]
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_WITHERED(Player.USERNAME_PATTERN + " withered away", -1, 1, -1),
	
	/**
	 * When a Player is killed by the wither effect while fighting another Player/mob.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_WITHERED_FIGHT(Player.USERNAME_PATTERN + " withered away whilst fighting " + Player.USERNAME_PATTERN, 
			2, 1, -1),
	
	
	/**
	 * When a Player is killed by a snowball, egg, ender pearl, or wither skull. Only the last one actually 
	 * does damage, so it's the only one that would ever be seen.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> [none]
	 */
	DEATH_PUMMELED(Player.USERNAME_PATTERN + " was pummeled by " + Player.USERNAME_PATTERN, 2, 1, -1),
	
	/**
	 * When a Player is killed by a renamed snowball, egg, ender pearl, or wither skull. Only the last one 
	 * actually does damage, so it's the only one that would ever be seen.
	 * <p>
	 * <b>CauseUser:</b> The Player/mob that killed the Player.
	 * <br/>
	 * <b>Recipient:</b> The Player that died.
	 * <br/>
	 * <b>SubContents:</b> The name of the renamed weapon.
	 */
	DEATH_PUMMELED_USING(Player.USERNAME_PATTERN + " was pummeled by " + Player.USERNAME_PATTERN + " using "
			+ "(\\p{ASCII}{1,})", 2, 1, 3),
	
	// }} Deaths
	
	
	
	
	
	
	
	
//	//Fails:
//	CANT_KEEP_UP("Can't keep up! Did the system time change, or is the server overloaded\\? Running "
//			+ "\\d{1,}ms behind, skipping \\d{1,} tick\\(s\\)", -1, -1, false),
//	
//	MOVED_TOO_QUICKLY(Player.USERNAME_PATTERN + " moved too quickly! \\p{ASCII}{1,}", -1, -1, true),
//	
//	NOTHING_CHANGED("Nothing changed, \\p{ASCII}{1,}", -1, -1, false),	//Op failure.
//	TAG_FAIL("No entity was found", -1, -1, false),
//	TELL_RAW_FAIL("No player was found", -1, -1, false),
//	
//	UNKNOWN_COMMAND("Unknown command at position \\d{1,}: \\p{ASCII}{1,}", -1, -1, false),
//	
//	
//	//Startups:
//	AMBIGUITY("Ambiguity between arguments \\p{ASCII}{1,}", -1, -1, false),
//	DONE("Done \\p{ASCII}{27,}", -1, -1, false),
//	FOUND_DATAPACK("Found new data pack \\{Alnum}{1,}, loading it automatically", -1, -1, false),
//	GAME_TYPE("Default game type: \\p{Alpha}{5,}", -1, -1, false),
//	GENERATE("Generating keypair", -1, -1, false),
//	LOAD("Loaded \\d{1,} recipes|Loaded \\d{1,} advancements|Loading properties", -1, -1, false),
//	PREPARE("Preparing level \"\\w{1,}\"|Preparing start region for level \\d{1,}"
//			+ "|Preparing spawn area: \\p{ASCII}{1,5}%", -1, -1, false),
//	RELOADING_RESOURCE("Reloading ResourceManager: \\p{ASCII}{7,}", -1, -1, false),
//	STARTING("Starting minecraft server version \\p{ASCII}{1,}|Starting Minecraft server on \\p{ASCII}{1,}",
//			-1, -1, false),
//	STDOUT("\\[STDOUT\\]: \\p{ASCII}{1,}", -1, -1, false),
//	TIME_ELAPSED("Time elapsed: \\d{1,} ms", -1, -1, false),
//	USING("Using default channel type", -1, -1, false),
//	
//	
//	//Stops:
//	SAVING_END("Saving players|Saving worlds|Saving chunks for level \\p{ASCII}{9,}", -1, -1, false),
//	STOPPING("Stopping the server|Stopping server", -1, -1, false);
	
	
	
	
	
	
	
	
	// {{ Server
	
	/**
	 * When the server is done loading up.
	 * <p>
	 * <b>CauseUser:</b> [Server]
	 * <br/>
	 * <b>Recipient:</b> [none]
	 * <br/>
	 * <b>SubContents:</b> The time it took for the server to load up.
	 */
	DONE_LOADING("Done \\((\\d{1,}\\.\\d{1,})s\\)! For help, type \"help\"", -1, -1, 1);
	
	// }} Server
	
	
	
	
	
	
	
	
	private String acceptedFormat;
	
	private int causeUserIndex;
	private int recipientIndex;
	
	private int subContentsIndex;
	
	
	/**
	 * Constructs a Defaults enumertaion.
	 * 
	 * @param acceptedFormatString String object containing the acceptable format for this 
	 * Defaults enumeration.
	 * @param causeUserIndex Index of the causeUser of the line.
	 * @param recipientIndex Index of the recipient of the line.
	 * @param subContentsIndex Index of the sub-contents of the line.
	 * @since 1.0
	 */
	private Defaults(String acceptedFormatString, int causeUserIndex, int recipientIndex, int subContentsIndex){
		this.acceptedFormat = acceptedFormatString;
		this.causeUserIndex = causeUserIndex;
		this.recipientIndex = recipientIndex;
		this.subContentsIndex = subContentsIndex;
	}
	
	
	@Override
	public String getAcceptedFormat(){
		return acceptedFormat;
	}
	
	
	@Override
	public int getSubContentsIndex(){
		return subContentsIndex;
	}
	
	
	@Override
	public int getCauseUserIndex(){
		return causeUserIndex;
	}
	
	
	@Override
	public int getRecipientIndex(){
		return recipientIndex;
	}
	
	
	@Override
	public boolean isLegal(String ServerOutputLineString){
		return ServerOutputLineString.matches(acceptedFormat);
	}
	
	
	@Override
	public boolean isDeathMessage(){
		if (this.name().contains("DEATH")){
			return true;
		}
		else{
			return false;
		}
	}
	
	
//	public static void main(String[] args){
//		Defaults test = Defaults.DEATH_ARROW;
//		
//		if (test.isDeathMessage()){
//			System.out.println("yee");
//		}
//		else{
//			System.out.println("nah");
//		}
//		
//		
//		test = Defaults.OPPED_PLAYER;
//		
//		if (test.isDeathMessage()){
//			System.out.println("yee");
//		}
//		else{
//			System.out.println("nah");
//		}
//	}
}














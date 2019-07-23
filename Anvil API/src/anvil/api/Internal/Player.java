package anvil.api.Internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;


/**
 * Represents a Minecraft Player.
 * <p>
 * The Player class communicates with Mojang's player-base servers to get related UUIDs and userNames. Each 
 * Player is cached in the wrapper and is only stored in memory (restarting the wrapper clears the cache). A 
 * Player is cached every time it interacts with the server for the first time every run-through. Command-Block 
 * "Players" are included in this list and have userName "@" and the UUID [null].
 * <p>
 * Because of the connection to Mojang's player-base servers, the wrapper only works with an Internet 
 * connection.
 * <p>
 * Cached Players are dubbed as known Players.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public class Player{
	private static LinkedList<Player> knownPlayers = new LinkedList<>();
	private static HashMap<String, LinkedList<Player>> oppedPlayers = new HashMap<>();
	private static HashMap<String, LinkedList<Player>> bannedPlayers = new HashMap<>();
	private static HashMap<String, LinkedList<Player>> whiteListedPlayers = new HashMap<>();
	
	public static final String USERNAME_PATTERN = "(\\w{3,16}|@)";
	public static final String UUID_PATTERN = 
			"(\\p{Alnum}{8}-\\p{Alnum}{4}-\\p{Alnum}{4}-\\p{Alnum}{4}-\\p{Alnum}{12})";
	
	private String userName;
	private UUID uuid;
	private HashMap<String, Boolean> is_Opped = new HashMap<>();
	private HashMap<String, Boolean> is_Banned = new HashMap<>();
	private HashMap<String, Boolean> is_WhiteListed = new HashMap<>();
	
	private boolean is_cBlock = false;
	
	
	private static boolean loaded = false;
	
	
	static HashMap<String, LinkedList<op_data>> restart_data = new HashMap<>();
	
	
	/**
	 * Constructs a Player object with a given userName.
	 * <p>
	 * If the Player is known, this object is a copy of the known object.
	 * 
	 * @param userName String object containing the userName of the Player.
	 * @throws IOException When the wrapper cannot connect to Mojang's Servers.
	 * @throws IllegalArgumentException When the userName isn't a legal userName, or when Mojang's servers 
	 * don't recognize it as a known Player.
	 */
	public Player(String userName) throws IOException, IllegalArgumentException{
		if (!loaded){
			loaded = true;
			
			firstLoad();
		}
		
		
		Player other = getKnownPlayer(userName);
		
		if (other != null){
			this.userName = userName;
			this.uuid = other.uuid;
			this.is_cBlock = other.is_cBlock;
			this.is_Opped = other.is_Opped;
			this.is_Banned = other.is_Banned;
			this.is_WhiteListed = other.is_WhiteListed;
		}
		else{
			if (!userName.matches(USERNAME_PATTERN)){
				throw new IllegalArgumentException("The given String object isn't a legal userName.");
			}
			
			
			if (userName.equals("@")){
				is_cBlock = true;
				
				this.userName = userName;
				uuid = null;
				
				knownPlayers.add(this);
			}
			else{
				String response = "";
				try{
					URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + userName);
					HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
					
					
					InputStream temp_stream = connection.getInputStream();
			        BufferedReader br = new BufferedReader(new InputStreamReader(temp_stream));
			
			        String inputLine;
			
			        while ((inputLine = br.readLine()) != null){
			            response += inputLine;
			        }
			
			        br.close();
				}
				catch (IOException e){
					throw new IOException("An internet connection to the Mojang playerbase server could not"
							+ "be made.");
				}
				
				
				if (response.isEmpty()){
					throw new IllegalArgumentException("\"" + userName + "\" is not a recognized userName, "
							+ "according to Mojang's playerbase server.");
				}
				
				
				int pos = response.indexOf("\"id\":\"") + 6;
				
				StringBuilder UUIDString = new StringBuilder(response.substring(pos, pos + 32));
				
				UUIDString = UUIDString.insert(8, "-");
				UUIDString = UUIDString.insert(13, "-");
				UUIDString = UUIDString.insert(18, "-");
				UUIDString = UUIDString.insert(23, "-");
				
				uuid = UUID.fromString(UUIDString.toString());
				this.userName = userName;
				
				
				Runner[] runnerArray = RunnerManager.getRunnerArray();
				
				for (int i = 0; i < runnerArray.length; ++i){
					is_Opped.put(runnerArray[i].getTag(), false);
					is_Banned.put(runnerArray[i].getTag(), false);
					is_WhiteListed.put(runnerArray[i].getTag(), false);
				}
				
				
				knownPlayers.add(this);
			}
		}
	}
	
	
	/**
	 * Constructs a Player object with a given UUID.
	 * <p>
	 * If the Player is known, this object is a copy of the known object.
	 * 
	 * @param uuid UUID object containing the UUID of the Player.
	 * @throws IOException When the wrapper cannot connect to Mojang's Servers.
	 * @throws IllegalArgumentException When Mojang's servers don't recognize the given UUID as an existing 
	 * Player.
	 */
	public Player(UUID uuid) throws IOException, IllegalArgumentException{
		if (!loaded){
			loaded = true;
			
			firstLoad();
		}
		
		
		Player other = getKnownPlayer(uuid);
		
		if (other != null){
			this.uuid = uuid;
			this.userName = other.userName;
			this.is_cBlock = other.is_cBlock;
			this.is_Opped = other.is_Opped;
			this.is_Banned = other.is_Banned;
			this.is_WhiteListed = other.is_WhiteListed;
		}
		else{
			if (uuid == null){
				is_cBlock = true;
				
				this.uuid = uuid;
				userName = "@";
				
				knownPlayers.add(this);
			}
			else{
				String response = "";
				try{
					String UUIDString = uuid.toString();
					UUIDString = UUIDString.replaceAll("-", "");
					
					URL url = new URL("https://api.mojang.com/user/profiles/" + UUIDString + "/names");
					HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
					
					
					InputStream temp_stream = connection.getInputStream();
			        BufferedReader br = new BufferedReader(new InputStreamReader(temp_stream));
			
			        String inputLine;
			
			        while ((inputLine = br.readLine()) != null){
			            response += inputLine;
			        }
			
			        br.close();
				}
				catch (IOException e){
					throw new IOException("An internet connection to the Mojang playerbase server could not"
							+ "be made.");
				}
				
				
				if (response.isEmpty()){
					throw new IllegalArgumentException("\"" + uuid + "\" is not a recognized UUID, "
							+ "according to Mojang's playerbase server.");
				}
				
				
				int pos = response.indexOf("\"name\":\"") + 8;
				
				userName = response.substring(pos, response.indexOf('\"', pos));
				this.uuid = uuid;
				
				
				Runner[] runnerArray = RunnerManager.getRunnerArray();
				
				for (int i = 0; i < runnerArray.length; ++i){
					is_Opped.put(runnerArray[i].getTag(), false);
					is_Banned.put(runnerArray[i].getTag(), false);
					is_WhiteListed.put(runnerArray[i].getTag(), false);
				}
				
				
				knownPlayers.add(this);
			}
		}
	}
	
	
	/**
	 * Constructs a Player object with a given userName and other information. The index of the <em>op</em>, 
	 * <em>ban</em>, and <em>white</em> arrays should align with the index of the <em>tag</em> array for which 
	 * Runner object the Player has these properties.
	 * <p>
	 * This is only meant to be run from the firstLoad() method.
	 * 
	 * @param userName String object containing the userName of the Player.
	 * @param tag String array containing the Runner tags for each Runner object in the manager.
	 * @param op Boolean array representing whether or not the Player is opped for the index of the tag.
	 * @param ban Boolean array representing whether or not the Player is banned for the index of the tag.
	 * @param white Boolean array representing whether or not the Player is white-listed for the index of the 
	 * tag.
	 * @throws IOException When the wrapper cannot connect to Mojang's Servers.
	 * @throws IllegalArgumentException When the userName isn't a legal userName, or when Mojang's servers 
	 * don't recognize it as a known Player.
	 */
	private Player(String userName, String[] tag, boolean[] op, boolean[] ban, boolean[] white)
			throws IOException, IllegalArgumentException{
		
		if (tag.length != op.length || op.length != ban.length || ban.length != white.length){
			throw new IllegalArgumentException("The given arrays are of unequal lengths, you messed up.");
		}
		
		
		Player other = getKnownPlayer(userName);
		
		if (other != null){
			throw new IllegalArgumentException("The Player \"" + other.getUserName() + "\" is already known.");
		}
		else{
			String response = "";
			try{
				URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + userName);
				HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
				
				
				InputStream temp_stream = connection.getInputStream();
		        BufferedReader br = new BufferedReader(new InputStreamReader(temp_stream));
		
		        String inputLine;
		
		        while ((inputLine = br.readLine()) != null){
		            response += inputLine;
		        }
		
		        br.close();
			}
			catch (IOException e){
				throw new IOException("An internet connection to the Mojang playerbase server could not"
						+ "be made.");
			}
			
			
			if (response.isEmpty()){
				throw new IllegalArgumentException("\"" + userName + "\" is not a recognized userName, "
						+ "according to Mojang's playerbase server.");
			}
			
			
			int pos = response.indexOf("\"id\":\"") + 6;
			
			StringBuilder UUIDString = new StringBuilder(response.substring(pos, pos + 32));
			
			UUIDString = UUIDString.insert(8, "-");
			UUIDString = UUIDString.insert(13, "-");
			UUIDString = UUIDString.insert(18, "-");
			UUIDString = UUIDString.insert(23, "-");
			
			uuid = UUID.fromString(UUIDString.toString());
			this.userName = userName;
			
			
			for (int i = 0; i < tag.length; ++i){
				is_Opped.put(tag[i], op[i]);
				
				if (op[i]){
					oppedPlayers.get(tag[i]).add(this);
				}
				
				
				is_Banned.put(tag[i], ban[i]);
				
				if (ban[i]){
					bannedPlayers.get(tag[i]).add(this);
				}
				
				
				is_WhiteListed.put(tag[i], white[i]);
				
				if (white[i]){
					whiteListedPlayers.get(tag[i]).add(this);
				}
			}
			
			
			knownPlayers.add(this);
		}
	}
	
	
	/**
	 * Sets the opped, banned, and white-listed Players. Runs every time the wrapper first interacts with a 
	 * Player.
	 * 
	 * @throws IOException Files couldn't be found or Mojang's servers couldn't be reached.
	 * @throws IllegalArgumentException A bad userName was found in the files (who's fuckin with the files??).
	 */
	private static void firstLoad() throws IllegalArgumentException, IOException{
		LinkedList<String> found_userNames_list = new LinkedList<>();
		
		
		//Player name, Runner tag, obw_data:
		LinkedHashMap<String, LinkedHashMap<String, obw_data>> obw_map = new LinkedHashMap<>();
		
		String line;
		int pos;
		String name;
		
		
		Runner[] runnerArray = RunnerManager.getRunnerArray();
		
		
		//Create the correct static objects:
		for (int i = 0; i < runnerArray.length; ++i){
			oppedPlayers.put(runnerArray[i].getTag(), new LinkedList<>());
			bannedPlayers.put(runnerArray[i].getTag(), new LinkedList<>());
			whiteListedPlayers.put(runnerArray[i].getTag(), new LinkedList<>());
		}
		
		
		//Loop through each existing Runner in the manager:
		Scanner reader = null;
		for (int i = 0; i < runnerArray.length; ++i){
			
			// {{ Ops:
			
			reader = new Scanner(new File(runnerArray[i].getRunFile().getParent() + "/ops.json"));
			
			while (reader.hasNextLine()){
				line = reader.nextLine();
				
				pos = line.indexOf("\"name\": \"");
				
				if (pos == -1){
					continue;
				}
				
				
				pos += 9;
				name = line.substring(pos, line.indexOf('\"', pos));
				
				pos = found_userNames_list.indexOf(name);	//pos is now used differently.
				
				
				if (pos == -1){	//New "name".
					found_userNames_list.add(name);
					
					obw_map.put(name, new LinkedHashMap<>());
					obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(true, false, false));
				}
				else{	//"name" already exists.
					if (obw_map.get(name).containsKey(runnerArray[i].getTag())){
						obw_map.get(name).get(runnerArray[i].getTag()).opped = true;
					}
					else{
						obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(true, false, false));
					}
				}
			}
			
			reader.close();
			
			// }} Ops:
			
			
			// {{ Banned:
			
			reader = new Scanner(new File(runnerArray[i].getRunFile().getParent() + "/banned-players.json"));
			
			while (reader.hasNextLine()){
				line = reader.nextLine();
				
				pos = line.indexOf("\"name\": \"");
				
				if (pos == -1){
					continue;
				}
				
				
				pos += 9;
				name = line.substring(pos, line.indexOf('\"', pos));
				
				pos = found_userNames_list.indexOf(name);	//pos is now used differently.
				
				if (pos == -1){	//New "name".
					found_userNames_list.add(name);
					
					obw_map.put(name, new LinkedHashMap<>());
					obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(false, true, false));
				}
				else{	//"name" already exists.
					if (obw_map.get(name).containsKey(runnerArray[i].getTag())){
						obw_map.get(name).get(runnerArray[i].getTag()).banned = true;
					}
					else{
						obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(false, true, false));
					}
				}
			}
			
			reader.close();
			
			// }} Banned:
			
			
			// {{ White-listed:
			
			reader = new Scanner(new File(runnerArray[i].getRunFile().getParent() + "/whitelist.json"));
			
			while (reader.hasNextLine()){
				line = reader.nextLine();
				
				pos = line.indexOf("\"name\": \"");
				
				if (pos == -1){
					continue;
				}
				
				
				pos += 9;
				name = line.substring(pos, line.indexOf('\"', pos));
				
				pos = found_userNames_list.indexOf(name);	//pos is now used differently.
				
				if (pos == -1){	//New "name".
					found_userNames_list.add(name);
					
					obw_map.put(name, new LinkedHashMap<>());
					obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(false, false, true));
				}
				else{	//"name" already exists.
					if (obw_map.get(name).containsKey(runnerArray[i].getTag())){
						obw_map.get(name).get(runnerArray[i].getTag()).whited = true;
					}
					else{
						obw_map.get(name).put(runnerArray[i].getTag(), new obw_data(false, false, true));
					}
				}
			}
			
			reader.close();
			
			// }} White-listed:
		}
		
		
		//Add to knowns:
		String[] names = new String[0];
		String[] tags = new String[0];
		
		obw_data[] data = new obw_data[0];
		boolean[] ops = new boolean[0];
		boolean[] bans = new boolean[0];
		boolean[] whites = new boolean[0];
		
		
		names = obw_map.keySet().toArray(names);
		LinkedHashMap<String, obw_data> temp_map = null;
		
		
		for (int i = 0; i < names.length; ++i){
			//new Player(found_userNames[i], is_ops_map.get(name)., is_bans[i], is_whites[i]);
			
			temp_map = obw_map.get(names[i]);
			
			tags = temp_map.keySet().toArray(tags);
			data = temp_map.values().toArray(data);
			
			ops = new boolean[data.length];
			bans = new boolean[data.length];
			whites = new boolean[data.length];
			
			for (int q = 0; q < data.length; ++q){
				ops[q] = data[q].opped;
				bans[q] = data[q].banned;
				whites[q] = data[q].whited;
			}
			
			
			new Player(names[i], tags, ops, bans, whites);
		}
	}
	
	
	private static class obw_data{
		public boolean opped = false;
		public boolean banned = false;
		public boolean whited = false;
		
		public obw_data(boolean op, boolean ban, boolean white){
			opped = op;
			banned = ban;
			whited = white;
		}
		
		
		@Override
		public String toString(){
			String returnString = "";
			
			if (opped){
				returnString += "|true,";
			}
			else{
				returnString += "|false,";
			}
			
			
			if (banned){
				returnString += "true,";
			}
			else{
				returnString += "false,";
			}
			
			
			if (whited){
				returnString += "true|";
			}
			else{
				returnString += "false|";
			}
			
			
			return returnString;
		}
	}
	
	
	/**
	 * Gets the known Player with the given userName.
	 * 
	 * @param userName String object containing the userName of the Player to get.
	 * @return Known Player object with the given userName, null if unknown.
	 */
	public static Player getKnownPlayer(String userName){
		Iterator<Player> iterator = knownPlayers.iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.userName == null && userName == null){
				return player;
			}
			else if (player.userName.equals(userName)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the known Player with the given UUID.
	 * 
	 * @param uuid UUID object containing the UUID of the Player to get.
	 * @return Known Player object with the given UUID, null if unknown.
	 */
	public static Player getKnownPlayer(UUID uuid){
		Iterator<Player> iterator = knownPlayers.iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.uuid == null){
				if (uuid == null){
					return player;
				}
			}
			else if (player.uuid.equals(uuid)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets an array of the known Players.
	 * 
	 * @return Array containing all the known Players.
	 */
	public static Player[] getKnownPlayers(){
		Player[] players = new Player[0];
		
		return knownPlayers.toArray(players);
	}
	
	
	/**
	 * Gets the opped Player with the given userName from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the opped Player from.
	 * @param userName String object containing the userName of the Player to get.
	 * @return Opped Player object with the given userName, null if not opped.
	 */
	public static Player getOppedPlayer(String tag, String userName){
		Iterator<Player> iterator = oppedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.userName == null && userName == null){
				return player;
			}
			else if (player.userName.equals(userName)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the opped Player with the given UUID from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the opped Player from.
	 * @param uuid UUID object containing the UUID of the Player to get.
	 * @return Opped Player object with the given UUID, null if not opped.
	 */
	public static Player getOppedPlayer(String tag, UUID uuid){
		Iterator<Player> iterator = oppedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.uuid == null){
				if (uuid == null){
					return player;
				}
			}
			else if (player.uuid.equals(uuid)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets an array of the opped Players for the Runner denoted by the <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner that has the desired opped players.
	 * @return Array containing all the opped Players in the designate Runner.
	 */
	public static Player[] getOppedPlayers(String tag){
		Player[] players = new Player[0];
		
		return oppedPlayers.get(tag).toArray(players);
	}
	
	
	/**
	 * Gets the banned Player with the given userName from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the banned Player from.
	 * @param userName String object containing the userName of the Player to get.
	 * @return Banned Player object with the given userName, null if not banned.
	 */
	public static Player getBannedPlayer(String tag, String userName){
		Iterator<Player> iterator = bannedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.userName == null && userName == null){
				return player;
			}
			else if (player.userName.equals(userName)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the banned Player with the given UUID from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the banned Player from.
	 * @param uuid UUID object containing the UUID of the Player to get.
	 * @return Banned Player object with the given UUID, null if not banned.
	 */
	public static Player getBannedPlayer(String tag, UUID uuid){
		Iterator<Player> iterator = bannedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.uuid == null){
				if (uuid == null){
					return player;
				}
			}
			else if (player.uuid.equals(uuid)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets an array of the banned Players from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner that has the desired banned players.
	 * @return Array containing all the banned Players in the designate Runner.
	 */
	public static Player[] getBannedPlayers(String tag){
		Player[] players = new Player[0];
		
		return bannedPlayers.get(tag).toArray(players);
	}
	
	
	/**
	 * Gets the white-listed Player with the given userName from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the white-listed Player from.
	 * @param userName String object containing the userName of the Player to get.
	 * @return White-listed Player object with the given userName, null if not white-listed.
	 */
	public static Player getWhiteListedPlayer(String tag, String userName){
		Iterator<Player> iterator = whiteListedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.userName == null && userName == null){
				return player;
			}
			else if (player.userName.equals(userName)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the white-listed Player with the given UUID from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to get the white-listed Player from.
	 * @param uuid UUID object containing the UUID of the Player to get.
	 * @return White-listed Player object with the given UUID, null if not white-listed.
	 */
	public static Player getWhiteListedPlayer(String tag, UUID uuid){
		Iterator<Player> iterator = whiteListedPlayers.get(tag).iterator();
		
		Player player = null;
		while (iterator.hasNext()){
			player = iterator.next();
			
			if (player.uuid == null){
				if (uuid == null){
					return player;
				}
			}
			else if (player.uuid.equals(uuid)){
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets an array of the white-listed Players from the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner that has the desired white-listed players.
	 * @return Array containing all the white-listed Players.
	 */
	public static Player[] getWhiteListedPlayers(String tag){
		Player[] players = new Player[0];
		
		return whiteListedPlayers.get(tag).toArray(players);
	}
	
	
	/**
	 * Gets the userName of the Player as a String object.
	 * <p>
	 * Equivalent to toString().
	 * 
	 * @return String object containing the userName of the Player.
	 */
	public String getUserName(){
		return userName;
	}
	
	
	/**
	 * Gets the uuid of the Player as a UUID object.
	 * 
	 * @return UUID object containing the uuid of the Player.
	 */
	public UUID getUUID(){
		return uuid;
	}
	
	
	/**
	 * Gets whether or not this Player is opped in the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to check.
	 * @return Whether or not this Player is opped.
	 */
	public boolean isOpped(String tag){
		return is_Opped.get(tag);
	}
	
	
	/**
	 * Gets whether or not this Player is banned in the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to check.
	 * @return Whether or not this Player is banned.
	 */
	public boolean isBanned(String tag){
		return is_Banned.get(tag);
	}
	
	
	/**
	 * Gets whether or not this Player is white-listed in the Runner with the given <em>tag</em>.
	 * 
	 * @param tag String object containing the tag of the Runner to check.
	 * @return Whether or not this Player is white-listed.
	 */
	public boolean isWhiteListed(String tag){
		return is_WhiteListed.get(tag);
	}
	
	
	/**
	 * Sets the op status of this Player to the given value in the Runner of the given <em>tag</em>. 
	 * <em>level</em> and <em>bypass</em> can't be specified in-game because of command restrictions. They will 
	 * take effect after the Minecraft server is restarted.
	 * <p>
	 * If the in-game equivalent of these commands take-place, the ServerOutputLine parser will handle it.
	 * 
	 * @param tag String object containing the tag of the Runner to change the op status of.
	 * @param value Boolean representing what value to change the Player's op status to.
	 * @param level Integer representing the op level of the Player. Ignored if <em>value</em> is false.
	 * @param bypass Boolean representing whether or not the Player can bypass the Minecraft server's player 
	 * limit. Ignored if <em>value</em> is false.
	 * @return Whether or not the set was successful.
	 */
	public boolean setOp(String tag, boolean value, int level, boolean bypass){
		Runner temp_runner = RunnerManager.getRunner(tag);
		
		if (temp_runner == null){
			return false;
		}
		else if (!temp_runner.checkStatus()){
			return false;
		}
		
		
		if (value){
			if (is_Opped.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("op " + userName);
			server.flush();
			
			is_Opped.put(tag, true);
			oppedPlayers.get(tag).add(this);
			
			
			if (restart_data.containsKey(tag)){
				restart_data.get(tag).add(new op_data(this, level, bypass));
			}
			else{
				LinkedList<op_data> temp = new LinkedList<>();
				temp.add(new op_data(this, level, bypass));
				
				restart_data.put(tag, temp);
			}
		}
		else{
			if (!is_Opped.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("deop " + userName);
			server.flush();
			
			is_Opped.put(tag, false);
			oppedPlayers.get(tag).remove(this);
			
			
			LinkedList<op_data> temp = restart_data.get(tag);
			
			for (op_data data : temp){
				if (data.player.equals(this)){
					temp.remove(data);
					
					break;
				}
			}
			
			if (temp.isEmpty()){
				restart_data.remove(tag);
			}
		}
		
		return true;
	}
	
	
	/**
	 * Sets the op status of this Player to the given value in the Runner of the given <em>tag</em>. 
	 * <em>level</em> and <em>bypass</em> will be the default values, as specified in the properties file.
	 * <p>
	 * If the in-game equivalent of these commands take-place, the ServerOutputLine parser will handle it.
	 * 
	 * @param tag String object containing the tag of the Runner to change the op status of.
	 * @param value Boolean representing what value to change the Player's op status to.
	 * @return Whether or not the set was successful.
	 */
	public boolean setOp(String tag, boolean value){
		Runner temp_runner = RunnerManager.getRunner(tag);
		
		if (temp_runner == null){
			return false;
		}
		else if (!temp_runner.checkStatus()){
			return false;
		}
		
		
		if (value){
			if (is_Opped.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("op " + userName);
			server.flush();
			
			is_Opped.put(tag, true);
			oppedPlayers.get(tag).add(this);
		}
		else{
			if (!is_Opped.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("deop " + userName);
			server.flush();
			
			is_Opped.put(tag, false);
			oppedPlayers.get(tag).remove(this);
			
			
			LinkedList<op_data> temp = restart_data.get(tag);
			
			for (op_data data : temp){
				if (data.player.equals(this)){
					temp.remove(data);
					
					break;
				}
			}
			
			if (temp.isEmpty()){
				restart_data.remove(tag);
			}
		}
		
		return true;
	}
	
	
	static class op_data{
		public Player player;
		public int level;
		public boolean bypass;
		
		public op_data(Player player, int level, boolean bypass){
			this.player = player;
			this.level = level;
			this.bypass = bypass;
		}
		
		
		@Override
		public boolean equals(Object other){
			op_data other_data = (op_data)other;
			
			if (player.equals(other_data.player) && level == other_data.level && bypass == other_data.bypass){
				return true;
			}
			
			
			return false;
		}
	}
	
	
	/**
	 * Sets the ban status of this Player to the given value in the Runner of the given <em>tag</em>. Uses the 
	 * given ban message.
	 * <p>
	 * If the in-game equivalent of these commands take-place, the ServerOutputLine parser will handle it.
	 * 
	 * @param tag String object containing the tag of the Runner to change the ban status of.
	 * @param value Boolean representing what value to change the Player's ban status to.
	 * @param banMessage String object containing the ban message (reason the Player was banned). If null or 
	 * empty, the default ban message will be used. If <em>value</em> is false, this field is ignored.
	 * @return Whether or not the set was successful.
	 */
	public boolean setBan(String tag, boolean value, String banMessage){
		Runner temp_runner = RunnerManager.getRunner(tag);
		
		if (temp_runner == null){
			return false;
		}
		else if (!temp_runner.checkStatus()){
			return false;
		}
		
		
		if (value){
			if (is_Banned.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			if (banMessage == null){
				server.println("ban " + userName);
				server.flush();
			}
			else if (banMessage.isEmpty()){
				server.println("ban " + userName);
				server.flush();
			}
			else{
				server.println("ban " + userName + " " + banMessage);
				server.flush();
			}
			
			is_Banned.put(tag, true);
			bannedPlayers.get(tag).add(this);
		}
		else{
			if (!is_Banned.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("pardon " + userName);
			server.flush();
			
			is_Banned.put(tag, false);
			bannedPlayers.get(tag).remove(this);
		}
		
		return true;
	}
	
	
	/**
	 * Sets the ban status of this Player to the given value in the Runner of the given <em>tag</em>. Uses the 
	 * default ban message.
	 * <p>
	 * If the in-game equivalent of these commands take-place, the ServerOutputLine parser will handle it.
	 * 
	 * @param tag String object containing the tag of the Runner to change the ban status of.
	 * @param value Boolean representing what value to change the Player's ban status to.
	 * @return Whether or not the set was successful.
	 */
	public boolean setBan(String tag, boolean value){
		return setBan(tag, value, null);
	}
	
	
	/**
	 * Sets the white-list status of this Player to the given value in the Runner of the given <em>tag</em>.
	 * <p>
	 * If the in-game equivalent of these commands take-place, the ServerOutputLine parser will handle it.
	 * 
	 * @param tag String object containing the tag of the Runner to change the white-list status of.
	 * @param value Boolean representing what value to change the Player's white-list status to.
	 * @return Whether or not the set was successful.
	 */
	public boolean setWhiteList(String tag, boolean value){
		Runner temp_runner = RunnerManager.getRunner(tag);
		
		if (temp_runner == null){
			return false;
		}
		else if (!temp_runner.checkStatus()){
			return false;
		}
		
		
		if (value){
			if (is_WhiteListed.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("whitelist add " + userName);
			server.flush();
			
			is_WhiteListed.put(tag, true);
			whiteListedPlayers.get(tag).add(this);
		}
		else{
			if (!is_WhiteListed.get(tag)){
				return false;
			}
			
			PrintWriter server = RunnerManager.getRunner(tag).getInputPrintWriter();
			
			server.println("whitelist remove " + userName);
			server.flush();
			
			is_WhiteListed.put(tag, false);
			whiteListedPlayers.get(tag).remove(this);
		}
		
		return true;
	}
	
	
	/**
	 * Tells whether or not the given Player object is equal to this Player object.
	 * <p>
	 * They will only be equal if the userName and UUID of each object is equal. All command block "Players" 
	 * are considered equal.
	 * 
	 * @param o The other Player to check for equality.
	 * @return Whether or not the given Player object is equal to this Player object.
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Player)){
			return false;
		}
		
		Player other = (Player)o;
		
		if (userName.equals(other.userName) && uuid.equals(other.uuid) && is_Opped.equals(other.is_Opped) && 
				is_Banned.equals(other.is_Banned) && is_WhiteListed.equals(other.is_WhiteListed)){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Gets the userName of the Player as a String object.
	 * <p>
	 * Equivalent to getUserName().
	 * 
	 * @return String object containing the userName of the Player.
	 */
	@Override
	public String toString(){
		return userName;
	}
	
	
//	public static void main(String[] args) throws Exception{
////		Player me = new Player("polic72");
////		
////		System.out.println(me.getUUID().toString());
//		
//		
//		UUID oh = null;
//		Player me = new Player(oh);
//		
//		System.out.println(me.getUserName());
//		
//		
//		Player me1 = new Player(UUID.fromString("045a3536-aa18-4a35-a1cc-c20032fcb70b"));
//		
//		System.out.println(me1.getUserName());
//		System.out.println(me1.is_Opped);
//	}
}

















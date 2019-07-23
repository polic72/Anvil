package anvil.api.Internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import anvil.api.Config.ConfigFile;


/**
 * Represents a manager/database for Runner objects.
 * <p>
 * Runner objects are the objects that contain and run the Minecraft servers. The RunnerManager is a way for 
 * Anvil to recognize and use each Runner object. If a Runner object is created by a PlugIn object and not 
 * added to the RunnerManager, it will not be recognized by the rest of the wrapper.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see Runner
 * @see anvil.api.PlugIn
 */
public abstract class RunnerManager{
	private static ArrayList<Runner> runnerList = new ArrayList<>(5);	//Any more than 5 runners can afford it.
	
	
	private static ConfigFile main_configFile;
	
	
	/**
	 * Adds a Runner object to the manager. This operation is necessary for Anvil to recognize Runner objects. 
	 * Anvil adds its own Runners (specified by the user in the GUI/CLI) before PlugIns add theirs.
	 * <p>
	 * Once added, a Runner object cannot be removed from the manager. Shutting down the wrapper is the only way 
	 * to clear the manager.
	 * <p>
	 * All new Runner objects should be created and added inside of the {@link anvil.api.PlugIn#onLoad()} 
	 * method.
	 * 
	 * @param runner Runner object to add to the manager.
	 * @throws IllegalArgumentException When the given <em>runner</em>'s tag is already found in the manager.
	 */
	public static void addRunner(Runner runner) throws IllegalArgumentException{
		for (int i = 0; i < runnerList.size(); ++i){
			if (runner.getTag().equals(runnerList.get(i).getTag())){
				throw new IllegalArgumentException("The given Runner has a tag that is already taken. (\""
						+ runner.getTag() + "\")");
			}
		}
		
		runnerList.add(runner);
	}
	
	
	/**
	 * Gets the Runner object from the manager with the given tag.
	 * <p>
	 * All of the Runner objects retrieved this way are mutable, so take care not to mess up the Runners when 
	 * altering them, it can throw a wrench in many PlugIns and Anvil itself.
	 * 
	 * @param tag String object containing the tag of the desired Runner.
	 * @return Runner object in the manager containing the given tag. Null if no such object exists.
	 */
	public static Runner getRunner(String tag){
		for (int i = 0; i < runnerList.size(); ++i){
			if (runnerList.get(i).getTag().equals(tag)){
				return runnerList.get(i);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets an array containing all of the Runner objects stored in the manager.
	 * <p>
	 * All of the Runner objects retrieved this way are mutable, so take care not to mess up the Runners when 
	 * altering them, it can throw a wrench in many PlugIns and Anvil itself.
	 * 
	 * @return Runner array containing all of the Runner objects in the manager.
	 */
	static Runner[] getRunnerArray(){
		Runner[] returnArray = new Runner[0];
		
		returnArray = runnerList.toArray(returnArray);
		
		return returnArray;
	}
	
	
	/**
	 * Gets the main config file of the wrapper.
	 * <p>
	 * This contains useful information for Anvil to work properly, but also acts as a centralized config file 
	 * if there exists a desire to add to it.
	 * 
	 * @return ConfigFile object containing the main config file.
	 */
	public static ConfigFile getMainConfigFile(){
		return main_configFile;
	}
	
	
	/**
	 * Sets the main config file of the wrapper.
	 * <p>
	 * This only does anything after being run for the first time, which Anvil does. 
	 * 
	 * @param config ConfigFile object to set the main config file to.
	 */
	public static void setMainConfigFile(ConfigFile config){
		if (main_configFile == null){
			main_configFile = config;
		}
	}
	
	
	/**
	 * Updates the op files of every Runner that needs it.
	 */
	static void update_ops(){
		for (String tag : Player.restart_data.keySet()){
			Runner runner = getRunner(tag);
			
			//If it's still running, ignore it.
			if (runner.checkStatus()){
				continue;
			}
			
			String op_filePath = runner.getRunFile().getParent() + "/ops.json";
			
			LinkedList<Player.op_data> data_list = Player.restart_data.get(tag);
			
			
			for (Player.op_data data : data_list){
				Scanner reader = null;
				try{
					reader = new Scanner(new File(op_filePath));
				}
				catch (FileNotFoundException e){
					//This won't ever happen.
				}
				String new_file = "";
				
				while (reader.hasNextLine()){
					String line = reader.nextLine();
					
					int pos = line.indexOf("\"name\": \"");
					
					if (pos == -1){
						new_file += line + "\n";
						
						continue;
					}
					
					
					pos += 9;
					String name = line.substring(pos, line.indexOf('\"', pos));
					
					new_file += line + "\n";
					
					
					if (!name.equals(data.player.getUserName())){
						continue;
					}
					
					
					//Level:
					line = reader.nextLine();
					pos = line.indexOf(": ") + 2;
					
					new_file += line.substring(0, pos) + String.valueOf(data.level) + ",\n";
					
					
					//Bypass:
					line = reader.nextLine();
					pos = line.indexOf(": ") + 2;
					
					new_file += line.substring(0, pos) + String.valueOf(data.bypass) + "\n";
				}
				
				reader.close();
				
				
				PrintWriter writer = null;
				try{
					writer = new PrintWriter(new File(op_filePath));
				}
				catch (FileNotFoundException e){
					//This won't ever happen. 
				}
				
				writer.write(new_file);
				
				writer.close();
			}
			
			
			Player.restart_data.remove(tag);
		}
	}
	
	
//	public static void main(String[] args) throws IOException{
////		Scanner input = new Scanner(System.in);
////		
////		
////		Runner test = new Runner("C:/Users/stonisg/Desktop/API Environment/Run.bat", false, "1");
////		Runner otherTest = new Runner("C:/Users/stonisg/Desktop/API Environment/Run.bat", false, "2");
////		
////		RunnerManager.addRunner(test);
////		RunnerManager.addRunner(otherTest);
////		
////		input.nextLine();
////		
////		test.start();
//////		otherTest.start();
////		
////		new Thread(){
////			
////			@Override
////			public void run(){
////				
////				Scanner hmm = test.getOutputScanner();
////				
////				while(hmm.hasNextLine()){
////					System.out.println(hmm.nextLine());
////				}
////			}
////		}.start();
////		
////		input.nextLine();
////		
////		Player hold = new Player("Cutebot");
////		
////		input.nextLine();
////		
////		hold.setBan("1", false);
////		
////		input.nextLine();
////		
////		test.stop();
//////		otherTest.stop();
////		
////		input.nextLine();
////		input.close();
//		
//		
//		
//		
//		
//		
//		
//		Scanner input = new Scanner(System.in);
//		
//		Runner test = new Runner("C:/Users/stonisg/Desktop/API Environment/Run.bat", false, "1");
//		RunnerManager.addRunner(test);
//		
//		test.start();
//		
//		
//		input.nextLine();
//		
//		
//		Player player = new Player("SkaianReckoning");
//		
//		player.setOp("1", false);
//		
//		
//		input.nextLine();
//		
//		
//		player.setOp("1", true, 2, true);
//		
//		
//		input.nextLine();
//		
//		
//		test.stop();
//		
//		
//		input.close();
//	}
}





















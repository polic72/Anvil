package anvil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import anvil.api.Command.Command;
import anvil.api.Command.CommandPermissions;
import anvil.api.Command.CommandResult.*;
import anvil.api.Config.ConfigFile;
import anvil.api.Config.ConfigOption;
import anvil.api.Internal.Player;
import anvil.api.Internal.Runner;
import anvil.api.Internal.RunnerManager;
import anvil.api.ServerOutputLine.Defaults;
import anvil.api.ServerOutputLine.ServerOutputLine;


public abstract class Main{
	private static String current_path;
	
	private static File plugIn_directory;
	
	
	public static void main(String[] args){
		current_path = System.getProperty("user.dir");
		
		
		File cfg = new File(current_path + "/anvil.config");
		RunnerManager.setMainConfigFile(new ConfigFile(cfg));
		
		ConfigFile main_config = RunnerManager.getMainConfigFile();
		
		// {{ Build the Main ConfigFile:
		
		ConfigOption mainRunner = new ConfigOption("runFile", null, null, true, null, "The location of the run "
				+ "file (put the path to the Run.sh here).");
		
		main_config.addOption(mainRunner);
		
		
		// }} Build the Main ConfigFile:
		
		
		System.out.println("Starting Anvil...");
		
		
		LinkedList<Thread> created_threads = null;
		
		// {{ Load PlugIns:
		
		plugIn_directory = new File(current_path + "/PlugIns");
		
		if (!plugIn_directory.exists()){
			plugIn_directory.mkdir();	//Definitely no PlugIns to load.
		}
		else{
			HashMap<String, String> load_errors = PlugIn_Loader.loadPlugIns(plugIn_directory);
			
			for (String file : load_errors.keySet()){
				String error = load_errors.get(file);
				
				System.err.println("\"" + file + "\" failed to load: " + error);
			}
			
			
			created_threads = PlugIn_Loader.applyMethod("onLoad");
		}
		
		// }} Load PlugIns:
		
		
		if (created_threads != null){
			waitForThreads(created_threads);
		}
		
		
		String runFile_path = null;
		if (cfg.exists()){
			try{
				System.out.println("Loading config file.");
				
				main_config.readFromFile();
			}
			catch (FileNotFoundException e){
				//This can't happen.
			}
			
			
			runFile_path = main_config.getOption("runFile").getCurrentValue();
		}
		else{
			System.out.println("Created config file.\nExiting..."); 
			
			main_config.saveToFile();
			
			
			created_threads = PlugIn_Loader.applyMethod("onShutDown");
			waitForThreads(created_threads);
			
			
			System.exit(0);
		}
		
		
		RunnerManager.addRunner(new Runner(runFile_path, false, "main"));
		
		Runner main_runner = RunnerManager.getRunner("main");
		
		
		ServerOutputLine.addKnownType(Defaults.class);	//The onLoad() method was already run, so this is fine.
		
		
		System.out.println("Starting \"" +  runFile_path + "\".");
		
		try{
			main_runner.start();
		}
		catch (IOException e){
			//This shouldn't happen.
		}
		
		
		
		
		
		Thread outputLine_interpreter = new Thread(){
			
			@Override
			public void run(){
				Class<?>[] param_types_start = new Class<?>[1];
				param_types_start[0] = String.class;
				
				Object[] params_start = new Object[1];
				params_start[0] = "main";
				
				
				Class<?>[] param_types = new Class<?>[2];
				param_types[0] = String.class;
				param_types[1] = ServerOutputLine.class;
				
				Object[] params = new Object[2];
				params[0] = "main";
				
				
				ServerOutputLine line;
				while ((line = main_runner.getNextOutputLine()) != null){
					
					//Should run all PlugIn interprets here. Also print to the screen if the Runner is active.
					
					params[1] = line;
					
					System.out.println(line.getEntireLine());
					
					
					if (line.getType() == Defaults.DONE_LOADING){
						PlugIn_Loader.applyMethod("onServerStart", param_types_start, params_start);
					}
					
					
					// {{ In-game Command Handler:
					
					else if (line.getType() == Defaults.PLAYER_SPOKEN_MESSAGE || 
							line.getType() == Defaults.PLAYER_SAY_MESSAGE){
						
						if (line.getSubContents().startsWith("!")){
							Command command = null;
							try{
								command = new Command(line.getContents());
								
								if (command.getType().getPermissions() == CommandPermissions.SERVER){
									runCommand(command, null, main_runner);	//TODO This might be illegal.
								}
								else if (command.getType().getPermissions() == CommandPermissions.ADMIN){
									Player culprit = line.getCauseUser();
									
									if (culprit.isOpped("main")){
										runCommand(command, culprit, main_runner);
									}
									else{
										//TODO Tell the player that they don't have access to the command.
									}
								}
								else if (command.getType().getPermissions() == CommandPermissions.PLAYER){
									runCommand(command, line.getCauseUser(), main_runner);
								}
							}
							catch (IllegalArgumentException e){
								//Do nothing, it's just not a known command.
							}
						}
					}
					
					// }} In-game Command Handler:
					
					
					// {{ Op/Ban/White-list Handler:
					
					// {{ Op:
					
					else if (line.getType() == Defaults.OPPED_PLAYER ||
							line.getType() == Defaults.OPPED_SERVER){
						
						Player recipient = line.getRecipient();
						
						recipient.setOp("main", true);
					}
					else if (line.getType() == Defaults.DEOPPED_PLAYER ||
							line.getType() == Defaults.DEOPPED_SERVER){
						
						Player recipient = line.getRecipient();
						
						recipient.setOp("main", false);
					}
					
					// }} Op:
					
					
					// {{ Ban:
					
					else if (line.getType() == Defaults.BAN_PLAYER ||
							line.getType() == Defaults.BAN_SERVER){
						
						Player recipient = line.getRecipient();
						
						if (line.getSubContents() == null){
							recipient.setBan("main", true);
						}
						else{
							recipient.setBan("main", true, line.getContents());
						}
					}
					else if (line.getType() == Defaults.PARDON_PLAYER ||
							line.getType() == Defaults.PARDON_SERVER){
						
						Player recipient = line.getRecipient();
						
						recipient.setBan("main", false);
					}
					
					// }} Ban:
					
					
					// {{ White-list:
					
					else if (line.getType() == Defaults.WHITELIST_ADD_PLAYER ||
							line.getType() == Defaults.WHITELIST_ADD_SERVER){
						
						Player recipient = line.getRecipient();
						
						recipient.setWhiteList("main", true);
					}
					else if (line.getType() == Defaults.WHITELIST_REMOVE_PLAYER ||
							line.getType() == Defaults.WHITELIST_REMOVE_SERVER){
						
						Player recipient = line.getRecipient();
						
						recipient.setWhiteList("main", false);
					}
					
					// }} White-list:
					
					// }} Op/Ban/White-list Handler:

					
					PlugIn_Loader.applyMethod("interpretServerOutputLine", param_types, params);
				}
				
				//Dies when this thread is interrupted, which should be done after the server is stopped.
			}
			
		};
		outputLine_interpreter.start();
		
		
		Thread CLI_interpreter = new Thread(){
			
			@Override
			public void run(){
				Scanner reader = new Scanner(System.in);
				
				while (main_runner.checkStatus()){
					String line = reader.nextLine();
					
					if (line.startsWith("!")){
						Command command = null;
						try{
							command = new Command(line);
							
							runCommand(command, null, main_runner);
						}
						catch (IllegalArgumentException e){
							//Do nothing, it's just not a known command.
							//TODO say it's unknown
						}
					}
					else{
						main_runner.writeToServer(line);
					}
				}
				
				reader.close();
			}
			
		};
		CLI_interpreter.start();
		
		
		try {
			main_runner.waitForServer();
		}
		catch (InterruptedException e){
			//This shouldn't happen.
		}
		
		
		//onServerStop:
		Class<?>[] param_types = new Class<?>[1];
		param_types[0] = String.class;
		
		Object[] params = new Object[1];
		params[0] = "main";
		
		created_threads = PlugIn_Loader.applyMethod("onServerStop", param_types, params);
		waitForThreads(created_threads);
		
		
		//onShutDown:
		created_threads = PlugIn_Loader.applyMethod("onShutDown");
		waitForThreads(created_threads);	//TODO Say that all threads end up waiting for every other thread here.
		
		
		outputLine_interpreter.interrupt();
		
		System.out.println("\n\nPlease hit \"Enter\" to exit.");
	}
	
	
	/**
	 * Runs a command.
	 * 
	 * @param command Command object containing the command to interpret.
	 * @param player Player object containing the player that ran the command. Null if the server did it.
	 * @param runner Runner object containing the runner that the command was run in.
	 */
	public static void runCommand(Command command, Player player, Runner runner){
		//Start this interpreter after every Command object is made.
		Thread command_interpreter = new Thread(){
			CommandResult result = null;
			
			@Override
			public void run(){
				Thread executioner = new Thread(){
					
					@Override
					public void run(){
						try{
							result = command.getType().getExecutionData().execute(command.getArguments());	
						}
						catch (Throwable e){
							result = new Failed("The Command threw a(n) " + e.getClass().getSimpleName() + "!");
						}
					}
					
				};
				
				executioner.start();
				try{
					executioner.join();
				}
				catch (InterruptedException e){
					//This won't happen.
				}
				
				
				//Do the sending to the monitor and the Player here.
				System.out.println(result.getMessage());
				
				if (player != null){
					if (!player.getUserName().equals("@")){
						runner.sendToPlayer(player, result.getMessage());
					}
				}
			}
			
		};
		
		
		command_interpreter.start();
	}
	
	
	/**
	 * Makes the current thread wait for all the given threads to join.
	 * 
	 * @param threads Thread array containing all the threads to wait for.
	 */
	public static void waitForThreads(LinkedList<Thread> threads){
		for (Thread thread : threads){
			try{
				thread.join();
			}
			catch (InterruptedException e){
				//This should never happen.
			}
		}
	}
}




















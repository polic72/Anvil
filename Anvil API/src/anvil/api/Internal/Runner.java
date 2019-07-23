package anvil.api.Internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import anvil.api.PlugIn;
import anvil.api.ServerOutputLine.ServerOutputLine;


/**
 * Represents the data structure that contains the actual server process itself.
 * <p>
 * Runner objects run a separate process to run the server. They also use a separate thread to run the 
 * ServerOutputLine interpreter cleanly. Minecraft servers, themselves, tend to use only 1 thread at a time, but 
 * can use more occasionally.
 * <p>
 * Each Runner object uses the directory of the passed Run file as a working directory. This means that Anvil 
 * can be placed anywhere you'd like, and still run each server in a separate directory seamlessly.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public class Runner{
	private File runFile;
	private boolean isJar;
	
	private Process process;
	private Scanner in;
	private PrintWriter out;
	
	private Thread outputLine_interpreter;
	private BlockingQueue<ServerOutputLine> outputLines = new LinkedBlockingQueue<>();
	
	private boolean started = false;
	
	private String tag;
	
	
	/**
	 * Constructs a Runner instance with the given Run file location.
	 * 
	 * @param runLocation String object containing the location of the Run file for the server.
	 * @param isJar Boolean representing whether or not the given Run file is the server.jar file itself. If 
	 * false, the Run file should contain 1 line with everything needed to start up the server, including all 
	 * arguments.
	 * @param tag String object containing the tag of the Runner object. This is just an identifier for the 
	 * Runner. Each one should be unique.
	 */
	public Runner(String runLocation, boolean isJar, String tag){
		if (runLocation == null){
			throw new IllegalArgumentException("The given String object is null.");
		}
		else if (runLocation.isEmpty()){
			throw new IllegalArgumentException("The given String object is empty.");
		}
		else{
			runFile = new File(runLocation);
			
			if (!runFile.exists()){
				throw new IllegalArgumentException("The given Run file doesn't exist.");
			}
			else if (runFile.isDirectory()){
				throw new IllegalArgumentException("The given Run file is a directory.");
			}
			
			this.isJar = isJar;
		}
		
		
		this.tag = tag;
	}
	
	
	/**
	 * Constructs a Runner instance with the given Run file location.
	 * 
	 * @param runFile File object containing the Run file for the server.
	 * @param isJar Boolean representing whether or not the given Run file is the server.jar file itself. If 
	 * false, the Run file should contain 1 line with everything needed to start up the server, including all 
	 * arguments.
	 * @param tag String object containing the tag of the Runner object. This is just an identifier for the 
	 * Runner. Each one should be unique.
	 */
	public Runner(File runFile, boolean isJar, String tag){
		if (runFile == null){
			throw new IllegalArgumentException("The given File object is null.");
		}
		else{
			this.runFile = runFile;
			
			if (!runFile.exists()){
				throw new IllegalArgumentException("The given Run file doesn't exist.");
			}
			else if (runFile.isDirectory()){
				throw new IllegalArgumentException("The given Run file is a directory.");
			}
			
			this.isJar = isJar;
		}
		
		
		this.tag = tag;
	}
	
	
	/**
	 * Starts this Runner object.
	 * <p>
	 * This Runner's input and output are now active and can be used fully.
	 * <p>
	 * Most server.jar files take up to 30 seconds to start up, sometimes longer when using a lot of mods. This 
	 * needs to be taken into account when using custom Runners. As for all recognized Runners (Runners in the 
	 * {@link RunnerManager}), the {@link PlugIn#onServerStart(String)} is run after any given Runner is done 
	 * starting.
	 * <p>
	 * Starts the ServerOutputLine interpreter for this Runner.
	 * 
	 * @return Whether or not this Runner was successfully started.
	 * @throws IOException When process creation fails.
	 */
	public boolean start() throws IOException{
		if (started){
			return false;
		}
		
		
		ProcessBuilder pb = null;
		if (!isJar){
			try{
				Scanner reader = new Scanner(runFile);
				
				
				String command = reader.nextLine();
				
				String[] commandSplit = command.split(" ");
				
				
				pb = new ProcessBuilder(commandSplit);//pb = new ProcessBuilder(runFile.getAbsolutePath());
				pb = pb.directory(runFile.getParentFile());
				
				process = pb.start();
				
				//oof = process.getInputStream();
				in = new Scanner(process.getInputStream());
				out = new PrintWriter(process.getOutputStream());
				
				
				reader.close();
			}
			catch (FileNotFoundException e){
				//Nothing, this should never happen.
			}
		}
		else{
			String command = "java -jar \"" + runFile.getName() + "\"";
			
			String[] commandSplit = command.split(" ");
			
			
			pb = new ProcessBuilder(commandSplit);//pb = new ProcessBuilder(runFile.getAbsolutePath());
			pb.directory(runFile.getParentFile());
			
			process = pb.start();
			
			in = new Scanner(process.getInputStream());
			out = new PrintWriter(process.getOutputStream());
		}
		
		
		outputLine_interpreter = new Thread(){	//This will kill itself cleanly when the server is dead.
			
			@Override
			public void run(){
				while (in.hasNext()){
					String line = in.nextLine();
					outputLines.add(new ServerOutputLine(line));
				}
			}
		};
		
		outputLine_interpreter.start();
		
		
		started = true;
		
		return true;
	}
	
	
	/**
	 * Stops this Runner object gracefully (allows the server thread to end itself).
	 * <p>
	 * Is the same as {@link #stop(boolean)}, just passes true for <em>wait</em> and catches any possible
	 * InterruptedExceptions.
	 * 
	 * @return Whether or not this Runner was successfully stopped.
	 * 
	 * @see Runner#stop(boolean)
	 */
	public boolean stop(){
		boolean booleanToReturn;
		try{
			booleanToReturn = stop(true);
		}
		catch (InterruptedException e){
			booleanToReturn = true;	//?
		}
		
		
		started = false;
		
		return booleanToReturn;
	}
	
	
	/**
	 * Stops this Runner object gracefully (allows the server thread to end itself).
	 * 
	 * @param wait Boolean value representing whether or not this thread should wait for the server thread to 
	 * die. May cause Anvil to momentarily stop responding if true and the server is taking a while to stop.
	 * @return Whether or not this Runner was successfully stopped.
	 * @throws InterruptedException When waiting for the server thread to die is interrupted. Will not be 
	 * thrown if <em>wait</em> is false.
	 * 
	 * @see #stop()
	 * @see #stopForcibly()
	 */
	public boolean stop(boolean wait) throws InterruptedException{
		if (!started){
			return false;
		}
		
		
		writeToServer("stop");
		
		
		if (wait){
			process.waitFor();
		}
		
//		outputLine_interpreter.stop();
		
		
		in = null;
		out = null;
		
		process = null;
		
		
		started = false;
		
		
		RunnerManager.update_ops();
		
		
		return true;
	}
	
	
	/**
	 * Forcibly stops this Runner object.
	 * <p>
	 * WARNING! Can cause corruption in the world. Only use when necessary.
	 * 
	 * @return Whether or not this Runner was successfully stopped.
	 * 
	 * @see #stop(boolean)
	 * @see #stop()
	 */
	public boolean stopForcibly(){
		if (!started){
			return false;
		}
		
		
		process.destroy();
		
//		outputLine_interpreter.stop();
		
		in = null;
		out = null;
		
		process = null;
		
		
		started = false;
		
		
		RunnerManager.update_ops();
		
		
		return true;
	}
	
	
	/**
	 * Checks whether or not the server is still started or stopped. This is to stop any discrepancies from the 
	 * server being shutdown from in-game rather than through the API.
	 * 
	 * @return Whether or not the server is still running. True if running, false otherwise.
	 */
	public boolean checkStatus(){
		if (process == null){
			started = false;
			
			in = null;
			out = null;
			
			process = null;
			
			return false;
		}
		else if (process.isAlive()){
			started = true;
			
			return true;
		}
		else{
			started = false;
			
			in = null;
			out = null;
			
			process = null;
			
			return false;
		}
	}
	
	
	/**
	 * Causes the current thread to wait for the server to stop.
	 * <p>
	 * If the server's process dies in any way, this thread will continue.
	 * 
	 * @throws InterruptedException When waiting for the server thread to die is interrupted.
	 */
	public void waitForServer() throws InterruptedException{
		process.waitFor();
	}
	
	
	/**
	 * Gets the next output line from the server's queue.
	 * <p>
	 * This method is a blocking method, meaning that if there are no output lines in the queue, this method 
	 * makes the current thread wait indefinitely until it has a value, which it will return. If the current 
	 * thread is interrupted, it'll return null.
	 * 
	 * @return ServerOutputLine object containing the next output line in the queue. Null if the current thread 
	 * is interrupted.
	 * 
	 * @see ServerOutputLine
	 */
	public ServerOutputLine getNextOutputLine(){
		try{
			return outputLines.poll(Long.MAX_VALUE, TimeUnit.DAYS);	//This will take over 25 million billion
		}															//years to timeout. No accidents here B)
		catch (InterruptedException e){
			return null;
		}
	}
	
	
	/**
	 * Writes a message to the server. The given String is written as-is to the server.
	 * 
	 * @param message String object containing the desired message to send.
	 * @return Whether or not the write was successful.
	 */
	public boolean writeToServer(String message){
		if (out != null){
			out.println(message);
			out.flush();
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Gets the output of the inner server as a Scanner object.
	 * 
	 * @return Scanner object containing the output of the server.
	 */
	Scanner getOutputScanner(){
		return in;
	}
	
	
	/**
	 * Gets the input of the inner server as a PrintWriter object.
	 * 
	 * @return PrintWriter object containing the input of the server.
	 */
	PrintWriter getInputPrintWriter(){
		return out;
	}
	
	
	/**
	 * Gets the File object this Runner is using to represent the server.jar/Run_script file.
	 * 
	 * @return File object containing this Runner's server.jar/Run_script file.
	 */
	public File getRunFile(){
		return runFile;
	}
	
	
	/**
	 * Gets the tag as a String object.
	 * 
	 * @return String object containing the tag of the Runner.
	 */
	public String getTag(){
		return tag;
	}
	
	
	/**
	 * Writes a message to the ops of the server. The given String is written as-is to those Players.
	 * 
	 * TODO Make this way more comprehensive, look here: 
	 * https://www.digminecraft.com/game_commands/tellraw_command.php
	 * to do so.
	 * 
	 * TODO Add another that does this with all Players.
	 * 
	 * 
	 * Actually show the throws parts of every method, or at least document it.
	 * 
	 * 
	 * 
	 * @param message String object containing the desired message to send.
	 * @return Whether or not the write was successful.
	 */
	public boolean sendToOps(String message){
		if (out != null){
			
			Player[] operators = Player.getOppedPlayers(tag);
			
			for (int i = 0; i < operators.length; ++i){
				out.println("tellraw " + operators[i].getUserName() + " {\"text\":\"" + message + "\"}");
				out.flush();	
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Writes a message to the ops of the server. The given String is written as-is to those Players.
	 * 
	 * TODO Make this way more comprehensive, look here: 
	 * https://www.digminecraft.com/game_commands/tellraw_command.php
	 * to do so.
	 * 
	 * TODO Fix this
	 * 
	 * 
	 * 
	 * @param player Player object containing the Player to send the message to.
	 * @param message String object containing the desired message to send.
	 * @return Whether or not the write was successful.
	 */
	public boolean sendToPlayer(Player player, String message){
		if (out != null){
			
			out.println("tellraw " + player.getUserName() + " {\"text\":\"" + message + "\"}");
			out.flush();
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
//	public static void main(String[] args) throws IOException{
//		Runner testRunner = new Runner("C:\\Users\\stonisg\\Desktop\\API Environment\\Run.bat", false, "tag");
//		Scanner input = new Scanner(System.in);
//		
//		ServerOutputLine.addKnownType(Defaults.class);
//		
//		input.nextLine();
//		
//		testRunner.start();
//		
//		input.nextLine();
//		
//		
////		input.nextLine();
////		
////		//new Player("polic72").setOp(true, 4, false);
////		//new Player("polic72").setBan(true, "fuck you");
////		
////		input.nextLine();
//		
////		new Player("polic72").setOp(false, 4, false);
//		//new Player("polic72").setBan(false, "ok sorry");
//		
//		
//		Thread yeet = new Thread(){
//			
//			@Override
//			public void run(){
//				ServerOutputLine line;
//				while ((line = testRunner.getNextOutputLine()) != null){
//					System.out.println(line.getEntireLine());
//					
//					//Should run all PlugIn interprets here. Also print to the screen if the Runner is active.
//					System.out.println(line.getType());
//				}
//				
//				//Dies when this thread is interrupted, which should be done after the server is stopped.
//				System.out.println("--------------------------------------------------------------------------");
//			}
//			
//		};
//		yeet.start();
//		
//		
//		input.nextLine();
//		
//		input.close();
//		
//		testRunner.stop();
//		yeet.interrupt();
//	}
}




















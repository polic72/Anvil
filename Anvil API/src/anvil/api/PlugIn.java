package anvil.api;

import anvil.api.Internal.Runner;
import anvil.api.ServerOutputLine.Defaults;
import anvil.api.ServerOutputLine.ServerOutputLine;


/**
 * Represents an interface for a PlugIn.
 * <p>
 * PlugIns are .jar files that Users can create with the API and add-on to Anvil. Each class implementing the 
 * PlugIn interface should get its own separate .jar file, as to not confuse the interpreter, and to keep 
 * consistency. A class that implements this interface is literally the entry point for the PlugIn.
 * <p>
 * All PlugIns should be mutually exclusive of every other, that meaning that no PlugIn should rely on another 
 * to perform an action before or after this one.
 * <p>
 * When implementing the PlugIn interface, it's very important to not make the class abstract, other-wise Anvil 
 * can't properly load the PlugIn, and it will be ignored.
 * <p>
 * Every method is run on a separate thread. This means that even if a method is already running, another method 
 * could be invoked (including the same method that's already running). This means that all PlugIns should make 
 * their data thread safe.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public interface PlugIn{
	
	
	/**
	 * Runs once every time Anvil is started.
	 */
	public abstract void onLoad();
	
	
	/**
	 * Runs once every time Anvil is shut down.
	 */
	public abstract void onShutDown();
	
	
	/**
	 * If for whatever reason the PlugIn throws an unhandled throwable (Exception or Error) in any other 
	 * {@link PlugIn} method, this method will be called. It will not be called if this method also throws an 
	 * unhandeled throwable, so don't worry about those kinds of loops.
	 * <p>
	 * Once this method is called on a separate thread, Anvil will no longer interact with the PlugIn until it 
	 * [Anvil] is restarted.
	 * <p>
	 * Any other currently running methods of this PlugIn will continue to run until they exit.
	 * 
	 * @param e Throwable object containing the Throwable that was thrown by the PlugIn.
	 */
	public abstract void onThrowable(Throwable e);
	
	
	/**
	 * Runs once every time the wrapped Minecraft server is started. This is executed when the server outputs 
	 * the "Done!" output line.
	 * 
	 * @param tag The Runner tag of the Runner that was started.
	 * 
	 * @see Runner
	 * @see Defaults#DONE_LOADING
	 */
	public abstract void onServerStart(String tag);
	
	
	/**
	 * Runs once every time the wrapped Minecraft server is shut down. This is executed when the server's 
	 * process dies.
	 * 
	 * @param tag The Runner tag of the Runner that was stopped.
	 * 
	 * @see Runner#stop()
	 * @see Runner#stop(boolean)
	 * @see Runner#stopForcibly()
	 */
	public abstract void onServerStop(String tag);
	
	
	/**
	 * Runs once every time Anvil receives an output-line from the server.
	 * <p>
	 * Every output-line is interpreted by the wrapper first, then to the list of loaded PlugIns. Anvil will 
	 * call each PlugIn's interpretServerOutputLine() method in an indiscriminate order, this means that no 2 
	 * PlugIns should try to interact with each other unless done so intelligently.
	 * <p>
	 * Will interpret lines even if the Runner from the tag isn't completely started. It might be wise to check if 
	 * the server is fully started before interpreting output lines. Use {@link PlugIn#onServerStart(String)} to 
	 * do this.
	 * 
	 * @param tag The tag of the Runner the output line came from.
	 * @param outputLine The ServerOutputLine to interpret.
	 * 
	 * @see Runner
	 * @see ServerOutputLine
	 */
	public abstract void interpretServerOutputLine(String tag, ServerOutputLine outputLine);
}





















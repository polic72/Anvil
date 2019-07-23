package anvil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import anvil.api.PlugIn;


/**
 * Loads, unloads, and runs the methods of all PlugIns handled by Anvil.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public abstract class PlugIn_Loader{
	private static HashMap<Class<?>, PlugIn_Node> loaded_plugins = new HashMap<>();
//	private static LinkedList<Object> loaded_plugins = new LinkedList<>();
	
	private static URLClassLoader loader;
	
	
	/**
	 * Attempts to load PlugIns from the given jar files with the given class names.
	 * <p>
	 * If the PlugIn fails to load for any reason, it will be added to the failure HashMap. This will detail 
	 * which PlugIn failed to load and why.
	 * 
	 * @param plugIn_directory File object that contains the PlugIn directory to load.
	 * @return HashMap containing the PlugIns that failed to load.
	 */
	public static HashMap<String, String> loadPlugIns(File plugIn_directory){
		if (loader == null){
			File[] jars = plugIn_directory.listFiles();
			
			
			HashMap<String, String> load_errors = new HashMap<>(jars.length);
			
			
			LinkedList<URL> URLs_list = new LinkedList<>();
			HashMap<String, String> class_names = new HashMap<>(jars.length);
			
			// {{ Load Class Names:
			
			
			for (int i = 0; i < jars.length; ++i){
				ZipFile zip = null;
				try{
					zip = new ZipFile(jars[i]);
					
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry zipEntry = (ZipEntry) entries.nextElement();
						
						if (zipEntry.getName().endsWith(".class")){
							String fix = zipEntry.getName().replace('/', '.');
							fix = fix.substring(0, fix.lastIndexOf('.'));
							
							class_names.put(jars[i].getAbsolutePath(), fix);
							
							
							try{
								URLs_list.add(jars[i].toURI().toURL());
							}
							catch (MalformedURLException e){
								//This won't happen.
							}
						}
					}
					
					
					zip.close();
				}
				catch (ZipException e){
					String error_text = "\"" + jars[i].getName() + "\" is corrupted.";
							
					load_errors.put(jars[i].getAbsolutePath(), error_text);
					
					continue;
				}
				catch (IOException e){
					String error_text = "\"" + jars[i].getName() + "\" couldn't be opened.";
					
					load_errors.put(jars[i].getAbsolutePath(), error_text);
					
					continue;
				}
			}
			
			// }} Load Class Names:
			
			
			loader = new URLClassLoader(URLs_list.toArray(new URL[0]), PlugIn_Loader.class.getClassLoader());
			
			
			for (String jar : class_names.keySet()){
				String class_name = class_names.get(jar);
				
				
				Class<?> plugIn_class = null;
				try{
					plugIn_class = loader.loadClass(class_name);//.asSubclass(PlugIn.class);
					//plugIn_class = Class.forName("testing.Test", true, loader);//.asSubclass(PlugIn.class);
				}
				catch (ClassNotFoundException e){
					//This won't happen.
				}
				
				
				//Check if it's a PlugIn:
				Class<?>[] implementations = plugIn_class.getInterfaces();
				
				for (int q = 0; q < implementations.length; ++q){
					if (!implementations[q].getName().equals(PlugIn.class.getName())){
						String error_text = "\"" + class_name + "\" does not implement the PlugIn interface."; 
						
						load_errors.put(class_name, error_text);
						
						continue;
					}	
				}
				
				
				Object instance = null;
				
				// {{ Instantiate the PlugIn:
				
				try {
					instance = plugIn_class.getConstructor().newInstance();
				}
				catch (InstantiationException e){
					String error_text = "\"" + class_name + "\" is an abstract class."; 
					
					load_errors.put(jar, error_text);
					
					continue;
				}
				catch (IllegalAccessException e){
					String error_text = "\"" + class_name + "\" is hiding its default constructor.";
					
					load_errors.put(jar, error_text);
					
					continue;
				}
				catch (InvocationTargetException e) {
					String error_text = "\"" + class_name + "\"'s default constructor threw an exception.";
					
					load_errors.put(jar, error_text);
					
					continue;
				}
				catch (NoSuchMethodException e) {
					String error_text = "\"" + class_name + "\" is hiding its default constructor.";
					
					load_errors.put(jar, error_text);
					
					continue;
				}
				catch (IllegalArgumentException | SecurityException e) {
					String error_text = "\"" + class_name + "\" threw this:" + System.lineSeparator()
						+ System.lineSeparator() + e.getMessage();
					
					load_errors.put(jar, error_text);
					
					continue;
				}
				
				// }} Instantiate the PlugIn:
				
				
				loaded_plugins.put(plugIn_class, new PlugIn_Node(jar, instance));
//				loaded_plugins.add(instance);
			}
			
			
			//Never unload the ClassLoader, unless the wrapper is shutting down, which it'll do itself.
			//This, however, does mean that you can no longer alter the PlugIns at runtime.
			//TODO Mark that somewhere.
//			try {
//				loader.close();
//			}
//			catch (IOException e){
//				//Do nothing.
//			}
			
			
			return load_errors;
		}
		
		
		return null;
	}
	
	
	/**
	 * Acts as a node for the loaded_plugins HashMap. Just stores the useful information.
	 */
	private static class PlugIn_Node{
		public String file;
		public Object instance;
		
		public PlugIn_Node(String file, Object instance){
			this.file = file;
			this.instance = instance;
		}
	}
	
	
//	/**
//	 * Applies the method to all the loaded PlugIns. This will run the method in the PlugIn with the given name.
//	 * <p>
//	 * If, for whatever reason, any of these invoked method throw an unhandeled throwable, the 
//	 * {@link PlugIn#onThrowable(Throwable)} method is invoked, passing the thrown throwable. The PlugIn is then 
//	 * unloaded entirely from the wrapper, until it is restarted.
//	 * 
//	 * @param method_name The name of the methods to invoke.
//	 * @return HashMap object containing error information from attempting to invoke a the method.
//	 */
//	public static HashMap<String, String> applyMethod(String method_name){
//		HashMap<String, String> run_errors = null;
//		
//		LinkedList<Class<?>> remove_me = new LinkedList<>();
//		
//		for (Class<?> clazz : loaded_plugins.keySet()){
//			PlugIn_Node node = loaded_plugins.get(clazz);
//			
//			Method meth = null;
//			try{
//				meth = clazz.getMethod(method_name);
//			}
//			catch (NoSuchMethodException | SecurityException | NullPointerException e){
//				throw new IllegalArgumentException("Method doesn't exist.");	//GG Garrett, ya fucked up.
//			}
//			
//			
//			try{
//				meth.invoke(node.instance);
//			}
//			catch (IllegalAccessException e){
//				throw new IllegalArgumentException("Can't access method.");	//GG Garrett, ya fucked up.
//			}
//			catch (IllegalArgumentException e){
//				throw new IllegalArgumentException("Bad arguments.");	//GG Garrett, ya fucked up.
//			}
//			catch (InvocationTargetException e){
//				
//				// {{ Handle Throwable in PlugIn Method:
//				
//				if (run_errors == null){
//					run_errors = new HashMap<>(loaded_plugins.size());
//				}
//				
//				
//				Throwable thrown = e.getTargetException();
//				
//				run_errors.put(node.file, thrown.getMessage());
//				
//				
//				Class<?>[] param_types = new Class<?>[1];
//				param_types[0] = Throwable.class;
//				
//				try{
//					meth = clazz.getMethod("onThrowable", param_types);
//				}
//				catch (NoSuchMethodException | SecurityException | NullPointerException e1) {
//					//This won't happen.
//				}
//				
//				Object[] params = new Object[] {thrown};
//				
//				try{
//					meth.invoke(clazz, params);
//				}
//				catch (Throwable e1) {
//					//They fucked up twice, just ignore it.
//				}
//				
//				// }} Handle Throwable in PlugIn Method:
//				
//				
//				remove_me.add(clazz);
//			}
//		}
//		
//		for (Class<?> rem : remove_me){
//			loaded_plugins.remove(rem);
//		}
//		
//		
//		return run_errors;
//	}
	
	
	/**
	 * Applies the method to all the loaded PlugIns. This will run the method in the PlugIn with the given name 
	 * and arguments.
	 * <p>
	 * If, for whatever reason, any of these invoked method throw an unhandeled throwable, the 
	 * {@link PlugIn#onThrowable(Throwable)} method is invoked, passing the thrown throwable. The PlugIn is then 
	 * unloaded entirely from the wrapper, until it is restarted.
	 * 
	 * @param method_name The name of the methods to invoke.
	 * @return Thread array containing all the threads created to run the methods.
	 */
	public static LinkedList<Thread> applyMethod(String method_name){
		LinkedList<Thread> return_threads = new LinkedList<>();
		
		
		Set<Class<?>> keySet = null;
		
		synchronized (loaded_plugins){
			keySet = new HashSet<>(loaded_plugins.keySet());	
		}
		
		
		for (Class<?> clazz : keySet){
			
			Thread created = new Thread(){
				Class<?> stored_clazz = clazz;
				PlugIn_Node node = null;
				
				String _method_name = method_name;
				
				
				@Override
				public void run(){
					
					synchronized (loaded_plugins){
						node = loaded_plugins.get(clazz);	
					}
							
					
					if (node != null){
						Method meth = null;
						try{
							meth = stored_clazz.getMethod(method_name);
						}
						catch (NoSuchMethodException | SecurityException | NullPointerException e){
							throw new IllegalArgumentException("Method doesn't exist.");//GG Garrett, ya fucked up.
						}
						
						
						try{
							meth.invoke(node.instance);
						}
						catch (IllegalAccessException e){
							throw new IllegalArgumentException("Can't access method.");	//GG Garrett, ya fucked up.
						}
						catch (IllegalArgumentException e){
							throw new IllegalArgumentException("Bad arguments.");	//GG Garrett, ya fucked up.
						}
						catch (InvocationTargetException e){
							
							// {{ Handle Throwable in PlugIn Method:
							
							Throwable thrown = e.getTargetException();
							
							
							// {{ Invoke "onThrowable"
							
							Class<?>[] param_types = new Class<?>[1];
							param_types[0] = Throwable.class;
							
							new Thread(){
								
								@Override
								public void run(){
									
									Method meth = null;
									try{
										meth = stored_clazz.getMethod("onThrowable", param_types);
									}
									catch (NoSuchMethodException | SecurityException | NullPointerException e1){
										//This won't happen.
									}
									
									Object[] params = new Object[] {thrown};
									
									try{
										meth.invoke(node.instance, params);
									}
									catch (Throwable e1) {
										//They fucked up twice, just ignore it.
									}
								}
								
							}.start();
							
							// }} Invoke "onThrowable"
							
							announceUnload(node.file, _method_name, thrown);
							
							// }} Handle Throwable in PlugIn Method:
							
							
							synchronized (loaded_plugins){
								loaded_plugins.remove(stored_clazz);	
							}
						}
					}
					
				}
				
			};
			
			
			created.start();
			
			return_threads.add(created);
		}
		
		
		return return_threads;
	}
	
	
	/**
	 * Applies the method to all the loaded PlugIns. This will run the method in the PlugIn with the given name 
	 * and arguments.
	 * <p>
	 * If, for whatever reason, any of these invoked method throw an unhandeled throwable, the 
	 * {@link PlugIn#onThrowable(Throwable)} method is invoked, passing the thrown throwable. The PlugIn is then 
	 * unloaded entirely from the wrapper, until it is restarted.
	 * 
	 * @param method_name The name of the methods to invoke.
	 * @param arg_types Class<?> array containing the types of the arguments passed to the method.
	 * @param args Object array containing the arguments to pass to the method.
	 * @return Thread array containing all the threads created to run the methods.
	 */
	public static LinkedList<Thread> applyMethod(String method_name, Class<?>[] arg_types, Object[] args){
		if (arg_types.length != args.length){
			throw new IllegalArgumentException("The argument types and arguments array aren't the same length.");
		}
		
		
		LinkedList<Thread> return_threads = new LinkedList<>();
		
		
		Set<Class<?>> keySet = null;
		
		synchronized (loaded_plugins){
			keySet = new HashSet<>(loaded_plugins.keySet());	
		}
		
		
		for (Class<?> clazz : keySet){
			
			Thread created = new Thread(){
				Class<?> stored_clazz = clazz;
				PlugIn_Node node = null;
				
				String _method_name = method_name;
				
				
				@Override
				public void run(){
					
					synchronized (loaded_plugins) {
						node = loaded_plugins.get(clazz);	
					}
							
					
					if (node != null){
						Method meth = null;
						try{
							meth = stored_clazz.getMethod(method_name, arg_types);
						}
						catch (NoSuchMethodException | SecurityException | NullPointerException e){
							throw new IllegalArgumentException("Method doesn't exist.");//GG Garrett, ya fucked up.
						}
						
						
						try{
							meth.invoke(node.instance, args);
						}
						catch (IllegalAccessException e){
							throw new IllegalArgumentException("Can't access method.");	//GG Garrett, ya fucked up.
						}
						catch (IllegalArgumentException e){
							throw new IllegalArgumentException("Bad arguments.");	//GG Garrett, ya fucked up.
						}
						catch (InvocationTargetException e){
							
							// {{ Handle Throwable in PlugIn Method:
							
							Throwable thrown = e.getTargetException();
							
							
							// {{ Invoke "onThrowable"
							
							Class<?>[] param_types = new Class<?>[1];
							param_types[0] = Throwable.class;
							
							new Thread(){
								
								@Override
								public void run(){
									
									Method meth = null;
									try{
										meth = stored_clazz.getMethod("onThrowable", param_types);
									}
									catch (NoSuchMethodException | SecurityException | NullPointerException e1){
										//This won't happen.
									}
									
									Object[] params = new Object[] {thrown};
									
									try{
										meth.invoke(node.instance, params);
									}
									catch (Throwable e1) {
										//They fucked up twice, just ignore it.
									}
								}
								
							}.start();
							
							// }} Invoke "onThrowable"
							
							announceUnload(node.file, _method_name, thrown);
							
							// }} Handle Throwable in PlugIn Method:
							
							
							synchronized (loaded_plugins){
								loaded_plugins.remove(stored_clazz);	
							}
						}
					}
					
				}
				
			};
			
			
			created.start();
			
			return_threads.add(created);
		}
		
		
		return return_threads;
	}
	
	
	/**
	 * Announces that the PlugIn has been unloaded because it threw something in a method.
	 * 
	 * @param file String object containing the file of the PlugIn being unloaded.
	 * @param method_name String object containing name of the method that cause this.
	 * @param thrown Throwable object containing the throwable being thrown in the method.
	 */
	private static void announceUnload(String file, String method_name, Throwable thrown){
		String temp_err = "\"" + file + "\" is being unloaded because inside of \"" + method_name + "\", it threw "
				+ "a \"" + thrown.getClass().getName() + "\" with the message: \"" + thrown.getMessage() + "\".";
		
		String error_text = "";
		
		for (int i = 0; i < temp_err.length(); ++i){
			error_text += "-";
		}
		
		error_text += "\n" + temp_err + "\n";
		
		for (int i = 0; i < temp_err.length(); ++i){
			error_text += "-";
		}
		
		error_text += "\n";
		
		System.err.println(error_text);
		
		
		//TODO This needs to wait until the server is started.
//		RunnerManager.getRunner("main").sendToOps(error_text);
	}
}





















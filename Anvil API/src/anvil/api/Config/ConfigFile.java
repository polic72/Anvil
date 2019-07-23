package anvil.api.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import anvil.api.PlugIn;


/**
 * Represents a file where configurable options are available.
 * <p>
 * All ConfigFile objects should be loaded while all PlugIns are loaded, so they should be built in the 
 * {@link PlugIn#onLoad()} method. For more information on building a ConfigFile object, see 
 * {@link #addOption(ConfigOption)}.
 * <p>
 * For organization, it is a good idea to name the config file itself along with the name of the PlugIn.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see ConfigOption
 */
public class ConfigFile{
	public static char COMMENT_CHARACTER = '#';
	
	
	private File file;
	
	private LinkedList<ConfigOption> options;
	
	
	/**
	 * Constructs a ConfigFile object.
	 * 
	 * @param filePath String object containing the path to the config file.
	 */
	public ConfigFile(String filePath){
		this(new File(filePath));
	}
	
	
	/**
	 * Constructs a ConfigFile object.
	 * 
	 * @param file File object containing the config file.
	 */
	public ConfigFile(File file){
		this.file = file;
		
		options = new LinkedList<>();
	}
	
	
	/**
	 * Adds a ConfigOption to the list of stored ConfigOptions. Each one will appear in the file and be 
	 * configurable.
	 * <p>
	 * This method is a crucial part of "building" the ConfigFile. Building the ConfigFile means adding the 
	 * all necessary ConfigOption objects to represent the file. This is best to be done in the {@link 
	 * anvil.api.PlugIn#onLoad()} method of the PlugIn. This is also a good time to add ConfigOptions to the 
	 * main ConfigFile.
	 * <p>
	 * Once added, these options cannot be removed.
	 * 
	 * @param option ConfigOption object containing the option to add.
	 * @return Whether or not the add was successful. False if an option with the same title already exists.
	 * 
	 * @see ConfigOption
	 */
	public boolean addOption(ConfigOption option){
		Iterator<ConfigOption> iterator = options.iterator();
		
		ConfigOption current;
		while (iterator.hasNext()){
			current = iterator.next();
			
			if (current.getTitle().equals(option.getTitle())){
				return false;
			}
		}
		
		
		options.add(option);
		
		return true;
	}
	
	
	/**
	 * Gets the ConfigOption object associated with the given title.
	 * <p>
	 * Note: This method returns the actual ConfigOption stored by this ConfigFile. Any changes made to the 
	 * option will affect the object for the rest of Anvil's run-time.
	 * 
	 * @param title String object containing the title of the ConfigOption to get.
	 * @return ConfigOption object containing the desired option. Null if the title doesn't exist in this 
	 * ConfigFile object.
	 */
	public ConfigOption getOption(String title){
		Iterator<ConfigOption> iterator = options.iterator();
		
		ConfigOption current;
		while (iterator.hasNext()){
			current = iterator.next();
			
			if (current.getTitle().equals(title)){
				return current;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Gets the ConfigOptions stored in the ConfigFile.
	 * <p>
	 * Note: This method returns the actual ConfigOptions stored by this ConfigFile. Any changes made to the 
	 * options will affect them for the rest of Anvil's run-time.
	 * 
	 * @return ConfigOption array containing every option.
	 */
	public ConfigOption[] getOptions(){
		ConfigOption[] temp = new ConfigOption[0];
		
		return options.toArray(temp);
	}
	
	
	/**
	 * Reads the file given in the constructor and updates the ConfigOption list with the stored values. This 
	 * only updates the currentValue property of the related ConfigOption.
	 * <p>
	 * This method is designed to be run after the user has already built the ConfigFile. See 
	 * {@link #addOption(ConfigOption)} for more information on building a ConfigFile. The main config file is 
	 * automatically read after all {@link PlugIn#onLoad()} methods have run; any other config files need to be 
	 * read manually.
	 * 
	 * @throws IllegalStateException When a required field is empty, or the field isn't built into the ConfigFile.
	 * @throws FileNotFoundException When the given file in the constructor doesn't exist.
	 */
	public void readFromFile() throws IllegalStateException, FileNotFoundException{
		Scanner reader = null;
		reader = new Scanner(file);
		
		@SuppressWarnings("unchecked")
		LinkedList<ConfigOption> options_copy = (LinkedList<ConfigOption>) options.clone();
		Iterator<ConfigOption> iterator;
		
		LinkedList<ConfigOption> finisher = new LinkedList<>();
		
		
		String line;
		
		String readTitle;
		int pos;
		
		boolean legal;
		ConfigOption current;
		String value;
		
		while (reader.hasNextLine()){
			legal = false;
			
			line = reader.nextLine();
			
			if (line.startsWith(String.valueOf(COMMENT_CHARACTER)) || line.isEmpty()){
				continue;
			}
			
			pos = line.indexOf(ConfigOption.getDelimiter());
			if (pos == -1)
			{
				reader.close();
				
				throw new IllegalStateException("The line \"" + line + "\" is not properly formatted.");
			}
			
			readTitle = line.substring(0, pos);
			
			
			iterator = options_copy.iterator();
			while (iterator.hasNext()){
				current = iterator.next();
				
				if (current.getTitle().equals(readTitle)){
					value = line.substring(pos + 1);
					
					if (value.isEmpty() && current.isRequired()){
						reader.close();
						
						throw new IllegalStateException("The \"" + current.getTitle() + "\" option is "
								+ "required, and therefore cannot be empty.");
					}
					else if (!current.setCurrentValue(value)){
						reader.close();
						
						throw new IllegalStateException("The \"" + current.getTitle() + "\" option contains "
								+ "an illegal value.");
					}
					
					finisher.add(current);
					iterator.remove();
					
					legal = true;
					break;
				}
			}
			
			if (!legal){
				reader.close();
				
				throw new IllegalStateException("The \"" + readTitle + "\" option is not built into this "
						+ "ConfigFile object.");
			}
		}
		
		reader.close();
		
		
		if (!options_copy.isEmpty()){
			iterator = options_copy.iterator();
			
			String error = "";
			
			if (options_copy.size() == 1){
				error = "\"" + options_copy.get(0).getTitle() + "\"";
			}
			else{
				while(iterator.hasNext()){
					current = iterator.next();
					
					if (iterator.hasNext()){
						error += "\"" + current.getTitle() + "\", ";
					}
					else{
						error += "and \"" + current.getTitle() + "\"";
					}
				}
			}
			
			
			reader.close();
			throw new IllegalArgumentException("The options with the following title(s): (" + error + ") "
					+ "are missing from the given file.");
		}
		
		
		options = finisher;
	}
	
	
	/**
	 * Saves the stored values of the ConfigOptions list to the file given in the constructor. If a 
	 * ConfigOption's currentValue is empty/null, the defaultValue will be written instead.
	 * <p>
	 * If the file already exists, every option's value will be overwritten, but comments and positioning will 
	 * be left untouched. If the file doesn't exist yet, all titles, values, and comments will be written.
	 * <p>
	 * This method is designed to be run after the user has already built the ConfigFile. See 
	 * {@link #addOption(ConfigOption)} for more information on building a ConfigFile.
	 */
	public void saveToFile(){
		if (file.isDirectory()){
			throw new IllegalArgumentException("The given file is a directory.");
		}
		
		
		if (file.exists()){
			
			//Read file and load into memory:
			Scanner reader = null;
			try{
				reader = new Scanner(file);
			}
			catch (FileNotFoundException e1){
				//Literally impossible.
			}
			
			
			String entireFile = "";
			while (reader.hasNextLine()){
				entireFile += reader.nextLine() + "\r\n";
			}
			reader.close();
			
			
			//Write lines that need to be written:
			@SuppressWarnings("unchecked")
			LinkedList<ConfigOption> options_copy = (LinkedList<ConfigOption>) options.clone();
			Iterator<ConfigOption> iterator;
			
			
			String newEntireFile = "";
			
			
			reader = new Scanner(entireFile);
			
			//These are for later:
			ConfigOption current;
			boolean legal;
			
			
			String readTitle;
			int pos;
			
			String line;
			while (reader.hasNextLine()){
				line = reader.nextLine();
				
				if (line.startsWith(String.valueOf(COMMENT_CHARACTER)) || line.isEmpty()){
					newEntireFile += line + System.lineSeparator();
				}
				else{
					pos = line.indexOf(ConfigOption.getDelimiter());
					
					if (pos == -1){
						reader.close();
						
						throw new IllegalArgumentException("The line \"" + line + "\" is not properly "
								+ "formatted.");
					}
					
					readTitle = line.substring(0, pos);
					
					
					legal = false;
					
					iterator = options_copy.iterator();
					while (iterator.hasNext()){
						current = iterator.next();
						
						if (current.getTitle().equals(readTitle)){
							if (current.getCurrentValue().isEmpty()){
								newEntireFile += current.getTitle() + ConfigOption.getDelimiter()
								+ current.getDefaultValue() + System.lineSeparator();
								
								iterator.remove();
							}
							else{
								newEntireFile += current.getTitle() + ConfigOption.getDelimiter()
								+ current.getCurrentValue() + System.lineSeparator();
								
								iterator.remove();
							}
							
							legal = true;
							break;
						}
					}
					
					if (!legal){
						reader.close();
						
						throw new IllegalArgumentException("The \"" + readTitle + "\" option is not built into "
								+ "this ConfigFile object.");
					}
				}
			}
			
			
			if (!options_copy.isEmpty()){
				iterator = options_copy.iterator();
				
				String error = "";
				
				if (options_copy.size() == 1){
					error = "\"" + options_copy.get(0).getTitle() + "\"";
				}
				else{
					while(iterator.hasNext()){
						current = iterator.next();
						
						if (iterator.hasNext()){
							error += "\"" + current.getTitle() + "\", ";
						}
						else{
							error += "and \"" + current.getTitle() + "\"";
						}
					}
				}
				
				
				reader.close();
				throw new IllegalArgumentException("The options with the following title(s): (" + error + ") "
						+ "are missing from the given file.");
			}
			
			
			PrintWriter writer = null;
			try{
				writer = new  PrintWriter(file);
			}
			catch (FileNotFoundException e){
				//This can't really happen.
			}
			
			writer.print(newEntireFile);
			
			
			reader.close();
			writer.close();
		}
		else{	//File doesn't exist yet:
			
			Iterator<ConfigOption> iterator = options.iterator();
			
			PrintWriter writer = null;
			try{
				writer = new  PrintWriter(file);
			}
			catch (FileNotFoundException e){
				//This can't really happen.
			}
			
			
			Scanner comment_reader;
			String commentLine;
			boolean hadComment;
			
			ConfigOption current;
			while (iterator.hasNext()){
				current = iterator.next();
				comment_reader = new Scanner(current.getComment());
				
				//Writing comment:
				hadComment = false;
				
				while (comment_reader.hasNext()){
					hadComment = true;
					
					commentLine = comment_reader.nextLine();
					
					writer.println(COMMENT_CHARACTER + commentLine);
				}
				
				if (hadComment){
					writer.println();
				}
				
				
				//Writing option itself:
				if (current.getCurrentValue().isEmpty()){
					writer.println(current.getTitle() + ConfigOption.getDelimiter()
					+ current.getDefaultValue());
				}
				else{
					writer.println(current.getTitle() + ConfigOption.getDelimiter()
					+ current.getCurrentValue());
				}
				
				writer.println(System.lineSeparator());
			}
			
			
			writer.close();
		}
	}
	
	
//	public static void main(String[] args) throws FileNotFoundException{
//		ConfigFile test = new ConfigFile("C:\\Users\\stonisg\\Desktop\\API Environment\\zzz.txt");
//		
//		ConfigOption _1 = new ConfigOption("test1", "", null, false, "yeetin", "This is a test comment.\r\noh");
//		
//		ConfigOption _2 = new ConfigOption("number", "5", "[0-5]", true, "0", "Use this for that.");
//		
//		test.addOption(_1);
//		test.addOption(_2);
//		
//		test.readFromFile();
//		
//		System.out.println(test.options.get(0).getCurrentValue());
//		
//		test.getOption("number").setCurrentValue("3");
//		System.out.println(test.options.get(1).getCurrentValue());
//		
////		test.getOption("number") = null;
//	}
}





















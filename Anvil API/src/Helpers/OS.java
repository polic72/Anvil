package Helpers;


/**
 * Marks the different recognized operating systems for a program to differentiate.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public enum OS{
	WIN_10("Windows 10"),
	WIN_8_1("Windows 8.1"),
	WIN_8("Windows 8"),
	WIN_7("Windows 7"),
	WIN_VISTA("Windows Vista"),
	WIN_XP("Windows XP"),
	LINUX("Linux"),
	MAC("MacOSX");
	
	
	private String name;
	
	
	/**
	 * Constructs an OS enumeration.
	 * 
	 * @param name Name of the OS.
	 */
	private OS(String name){
		this.name = name;
	}
	
	
	/**
	 * Get the name of the OS enumeration as a String object.
	 * 
	 * @return String object containing the name of the OS enumeration.
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * Converts a given String to its respective OS enumeration. The String needs to be character-perfect.
	 * 
	 * @param name String object containing the name of the operating system.
	 * @return The OS enumeration associated with the given String object.
	 */
	public static OS stringToOS(String name){
		OS osToReturn = null;
		
		for (OS os : OS.values()){
			if (name.equals(os.name)){
				osToReturn = os;
				break;
			}
		}
		
		return osToReturn;
	}
	
	
	/**
	 * Determine whether the given, recognized OS is a version of Windows.
	 * 
	 * @return Whether or not the OS enumeration is a version of Windows.
	 */
	public boolean isWindows(){
		return name.startsWith("Windows");
	}
	
	
	/**
	 * Determine whether the given, recognized OS is a version of Unix/Linux.
	 * 
	 * @return Whether or not the OS enumeration is a version of Unix/Linux.
	 */
	public boolean isUnix(){
		return (name.startsWith("Linux") || name.startsWith("Mac"));
	}
	
	
	/**
	 * Gets the OS enumeration of this machine.
	 * 
	 * @return OS enumeration that best represents this machine.
	 */
	public static OS getOS(){
		return stringToOS(System.getProperty("os.name"));
	}
}















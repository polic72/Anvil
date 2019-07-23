package anvil.api.Config;


/**
 * Represents an option inside a ConfigFile.
 * <p>
 * Each ConfigFile object should contain multiple ConfigOption objects, each of which need to be constructed. 
 * Constructing a ConfigOption object only needs a title and a currentValue, but every other value should be 
 * specified to fully take advantage of the class.
 * 
 * @author Garrett Stonis
 * @version 1.0
 * 
 * @see ConfigFile
 */
public class ConfigOption implements Cloneable{
	private static final char DELIMITER = '=';
	
	
	private String title;
	private String currentValue;
	
	private String acceptedResponse;
	private boolean required;
	private String defaultValue;
	private String comment;
	
	
	/**
	 * Constructs a ConfigOption object with the given Strings.
	 * <p>
	 * By default, this option accepts any response, is not required, has no default value, and has no comment.
	 * 
	 * @param title String object containing the title of the option. This is checked using String.equals() at 
	 * runtime. Cannot be null or empty.
	 * @param currentValue String object containing the currentValue of the option. Null or empty if it is 
	 * empty.
	 * 
	 * @see #ConfigOption(String, String, String, boolean, String, String)
	 */
	public ConfigOption(String title, String currentValue){
		this(title, currentValue, null, false, null, null);
	}
	
	
	/**
	 * Constructs a ConfigOption object with the given Strings and boolean.
	 * 
	 * @param title String object containing the title of the option. This is checked using String.equals() at 
	 * runtime. Cannot be null or empty.
	 * @param currentValue String object containing the currentValue of the option. Null or empty if it is 
	 * empty.
	 * @param acceptedResponse String object containing the accepted responses for the option. This is checked 
	 * using regex patterns at runtime. Null or empty for any value allowed.
	 * @param required Boolean representing whether or not this option is required to be filled. If true, the 
	 * default value can still be null or empty.
	 * @param defaultValue String object containing the defaultValue of the option. Null or empty for no 
	 * default value.
	 * @param comment String object containing the comment to put in front of the option in the file. Null or 
	 * empty for no comment. Can fully handle white-space characters.
	 * 
	 * @see #ConfigOption(String, String)
	 */
	public ConfigOption(String title, String currentValue, String acceptedResponse, boolean required, String 
			defaultValue, String comment){
		
		if (!setTitle(title)){
			throw new IllegalArgumentException("An illegal title was given.");
		}
		
		setAcceptedResponse(acceptedResponse);
		
		
		if (!setCurrentValue(currentValue)){
			throw new IllegalArgumentException("An illegal currentValue was given.");
		}
		
		setRequired(required);
		
		if (!setDefaultValue(defaultValue)){
			throw new IllegalArgumentException("An illegal defaultValue was given.");
		}
		
		setComment(comment);
	}
	
	
	/**
	 * Tells whether or not the given response is acceptable.
	 * 
	 * @param response String object containing the response to check.
	 * @return Whether or not the given response is acceptable.
	 */
	public boolean isAcceptable(String response){
		return response.matches(acceptedResponse);
	}
	
	
	/**
	 * Gets the title of this option.
	 * 
	 * @return String object containing the title of this option.
	 */
	public String getTitle(){
		return title;
	}
	
	
	/**
	 * Gets the currentValue of this option.
	 * 
	 * @return String object containing the currentValue of this option.
	 */
	public String getCurrentValue(){
		return currentValue;
	}
	
	
	/**
	 * Gets the acceptedResponse of this option.
	 * 
	 * @return String object containing the acceptedResponse of this option.
	 */
	public String getAcceptedResponse(){
		return acceptedResponse;
	}
	
	
	/**
	 * Tells whether or not this option is required.
	 * 
	 * @return Boolean representing whether or not this option is required.
	 */
	public boolean isRequired(){
		return required;
	}
	
	
	/**
	 * Gets the defaultValue of this option.
	 * 
	 * @return String object containing the defaultValue of this option.
	 */
	public String getDefaultValue(){
		return defaultValue;
	}
	
	
	/**
	 * Gets the comment of this option.
	 * 
	 * @return String object containing the comment of this option.
	 */
	public String getComment(){
		return comment;
	}
	
	
	/**
	 * Sets the title to the given new title.
	 * 
	 * @param title String object containing the new title.
	 * @return Whether or not the set was successful. False if null, empty, or contains the DELIMITER.
	 */
	private boolean setTitle(String title){
		if (title == null){
			return false;
		}
		else if (title.isEmpty()){
			return false;
		}
		else if (title.contains(String.valueOf(DELIMITER))){
			return false;
		}
		
		
		this.title = title;
		
		return true;
	}
	
	
	/**
	 * Sets the currentValue of the option to the given new currentValue. This must match the regex pattern 
	 * stored in the acceptResponse field, or it will fail.
	 * <p>
	 * NOTE: This will change the value the ConfigFile saves, so be wary.
	 * 
	 * @param currentValue String object containing the value of the new currentValue.
	 * @return Whether or not the set was successful. False if containing the DELIMITER, empty/null and is 
	 * required, or if it doesn't match the acceptedResponse.
	 */
	public boolean setCurrentValue(String currentValue){
		if (currentValue == null){
			if (required){
				return false;
			}
			
			this.currentValue = new String();
			
			return true;
		}
		else if (currentValue.isEmpty()){
			if (required){
				return false;
			}
			
			this.currentValue = new String();
			
			return true;
		}
		else if (currentValue.contains(String.valueOf(DELIMITER))){
			return false;
		}
		
		
		if (acceptedResponse.isEmpty() || currentValue.matches(acceptedResponse)){
			this.currentValue = currentValue;
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Sets the acceptedResponse to the given new acceptedResponse.
	 * <p>
	 * When changing the acceptedResponse, it is assumed that the currentValue and defaultValue already align 
	 * with the restrictions applied by the new acceptedResponse; they will not be checked. This means that 
	 * not also adjusting the currentValue and defaultValue could result in unexpected exceptions when reading 
	 * from ConfigFiles and working in your PlugIn.
	 * 
	 * @param acceptedResponse String object containing the new acceptedResponse.
	 */
	private void setAcceptedResponse(String acceptedResponse){
		if (acceptedResponse == null){
			this.acceptedResponse = new String();
		}
		else{
			this.acceptedResponse = acceptedResponse;
		}
	}
	
	
	/**
	 * Sets whether or not this option is required.
	 * 
	 * @param required Boolean representing whether or not this option is required.
	 */
	private void setRequired(boolean required){
		this.required = required;
	}
	
	
	/**
	 * Sets the defaultValue to the given new defaultValue. This value needs to match the acceptedResponse if 
	 * it isn't empty/null.
	 * 
	 * @param acceptedResponse String object containing the new defaultValue.
	 * @return Whether or not the set was successful. False if containing the DELIMITER, or if it is non-empty 
	 * and doesn't match the acceptedResponse.
	 */
	private boolean setDefaultValue(String defaultValue){
		if (defaultValue == null){
			this.defaultValue = new String();
			
			return true;
		}
		else if (defaultValue.isEmpty()){
			this.defaultValue = new String();
			
			return true;
		}
		else if (defaultValue.contains(String.valueOf(DELIMITER))){
			return false;
		}
		
		
		if (acceptedResponse.isEmpty() || defaultValue.matches(acceptedResponse)){
			this.defaultValue = defaultValue;
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * Sets the comment to the given new comment.
	 * 
	 * @param comment String object containing the new comment.
	 */
	private void setComment(String comment){
		if (comment == null){
			this.comment = new String();
		}
		else{
			this.comment = comment;
		}
	}
	
	
	/**
	 * Gets the delimiter of all options.
	 * 
	 * @return Character representing the delimiter of all options.
	 */
	public static char getDelimiter(){
		return DELIMITER;
	}
	
	
	@Override
	public Object clone(){
		ConfigOption optionToReturn = new ConfigOption(title, currentValue, acceptedResponse, required, 
				defaultValue, comment);
		
		return optionToReturn;
	}
}





















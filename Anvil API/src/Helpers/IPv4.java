package Helpers;


/**
 * Represents an IPv4 address.
 * <p>
 * Each number of the address is delimited by the '.' (period) character.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public class IPv4{
	private static final String pattern = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}"
			+ "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
	
	
	private String address;
	
	
	/**
	 * Constructs an IPv4 object.
	 * 
	 * @param address The IPv4 address as a String object.
	 */
	public IPv4(String address){
		if (!address.matches(pattern)){
			throw new IllegalArgumentException("The given String is not an IPv4 address.");
		}
		
		this.address = address;
	}
	
	
	/**
	 * Gets the address of the IPv4 object.
	 * 
	 * @return String object containing the address of the IPv4 object.
	 */
	@Override
	public String toString(){
		return address;
	}
	
	
//	public static void main(String[] args){
//		IPv4 ip = new IPv4("10.0.0.1");
//		
//		String s = "";
//		for (int i = 0; i < 4; ++i){
//			s += "([0-9]|";
//			
//			for (int q = 1; q < 26; ++q){
//				if (q != 25){
//					s += (q + "[0-9]|");
//				}
//				else{
//					s += q + "[0-5]";
//				}
//			}
//			
//			s += ").";
//		}
//		
//		System.out.println(s);
//	}
}

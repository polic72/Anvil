package Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Adds functionality to the File class by adding a few helpful methods. All methods are accessed 
 * statically, and therefore independently from any one File object.
 * 
 * @author Garrett Stonis
 * @version 1.0
 */
public abstract class FileHelper{
	private static OS os = OS.stringToOS(System.getProperty("os.name"));
	
	/**
	 * Copies a given directory and all of its contents to another directory. If the target directory does 
	 * not exist, it will be created, along with any directories needed for it to be created.
	 * <p>
	 * If the name of the target directory is not the same as the source directory, the source is 
	 * essentially copied into a new name.
	 * <p>
	 * Maximum individual file-size to copy, in the directory's contents, is 2,147,483,647 bytes (roughly 
	 * 2Gb).
	 * 
	 * @param sourceString The String object containing the path of the source directory.
	 * @param targetString The String object containing the path of the target directory.
	 * @return Whether or not the copy was successful.
	 * @throws Exception Exception Thrown if copying files has failed.
	 */
	public static boolean copyDirectory(String sourceString, String targetString) throws Exception{
		return copyDirectory(new File(sourceString), new File(targetString));
	}
	
	
	/**
	 * Copies a given directory and all of its contents to another directory. If the target directory does 
	 * not exist, it will be created, along with any directories needed for it to be created.
	 * <p>
	 * If the name of the target directory is not the same as the source directory, the source is 
	 * essentially copied into a new name.
	 * <p>
	 * Maximum individual file-size to copy, in the directory's contents, is 2,147,483,647 bytes (roughly 
	 * 2Gb).
	 * 
	 * @param source The File object containing the path of the source directory.
	 * @param target The File object containing the path of the target directory.
	 * @return Whether or not the copy was successful.
	 * @throws Exception Thrown if copying files has failed.
	 */
	public static boolean copyDirectory(File source, File target) throws Exception{
		if (!source.exists() || source.isFile()){	//Not for files/non-existent directories.
			return false;
		}
		
		if (target.exists() && target.isFile()){	//Destination is not a directory.
			return false;
		}
		else if (!target.exists()){
			forceCreateDirectory(target);	//Forces the creation of the destination directory.
		}
		
		
		//Search through source directory:
		String[] sourceContents = source.list();
		File[] sourceContentsFiles = new File[sourceContents.length];
		
		for (int i = 0; i < sourceContents.length; ++i){
			sourceContents[i] = source.getPath() + '/' + sourceContents[i];
			
			sourceContentsFiles[i] = new File(sourceContents[i]);
		}
		
		
		//Work time:
		for (int i = 0; i < sourceContentsFiles.length; ++i){
			if (sourceContentsFiles[i].isDirectory()){
				copyDirectory(sourceContents[i], target + "/" + sourceContentsFiles[i].getName());
			}
			else{	//"sourceContentsFiles[i]" is a file.
				copyFile(sourceContents[i], target.getPath() + "/" + sourceContentsFiles[i].getName());
			}
		}
		
		
		return true;
	}
	
	
	/**
	 * Copies a given file to a given target file. If the target file does not exist, it will be 
	 * created, along with any directories needed for it to be created. Overwrites any files with same name 
	 * in the target file's directory.
	 * <p>
	 * Maximum file-size to copy is 2,147,483,647 bytes (roughly 2Gb).
	 * 
	 * @param fileString The String object containing the path of the desired file to copy.
	 * @param targetString The String object containing the path of the target file.
	 * @return Whether or not the copy was successful.
	 * @throws Exception Thrown in the case that the "file" file isn't found or target file couldn't be 
	 * created.
	 */
	public static boolean copyFile(String fileString, String targetString) throws Exception{
		return copyFile(new File(fileString), new File(targetString));
	}
	
	
	/**
	 * Copies a given file to a given target file. If the target file does not exist, it will be 
	 * created, along with any directories needed for it to be created. Overwrites any files with same name 
	 * in the target file's directory.
	 * <p>
	 * Maximum file-size to copy is 2,147,483,647 bytes (roughly 2Gb).
	 * 
	 * @param file The File object containing the path of the desired file to copy.
	 * @param target The File object containing the path of the target file.
	 * @return Whether or not the copy was successful.
	 * @throws Exception Thrown in the case that the "file" file isn't found or target file couldn't be 
	 * created.
	 */
	public static boolean copyFile(File file, File target) throws Exception{
		FileInputStream in = new FileInputStream(file);
		
		if (!file.isFile()){	//This is after previous line so the FileNotFoundException can be thrown.
			in.close();
			
			return false;
		}
		
		forceCreateDirectory(getFileLocation(target));
		target.createNewFile();
		
		FileOutputStream out = new FileOutputStream(target);
		
		byte[] entireFile = new byte[(int)file.length()];	//Cuts max file-size down to 2Gb.  :<
		
		in.read(entireFile);
		out.write(entireFile);
		
		in.close();
		out.close();
		
		return true;
	}
	
	
	/**
	 * Gets the path of a file/directory WITHOUT the file/directory included. The final '/' or '\', 
	 * however, will be kept.
	 * 
	 * @param file File object containing the desired path.
	 * @return String object that contains the path of a given file/directory WITHOUT the file/directory.
	 */
	public static String getFileLocation(File file){
		return getFileLocation(file.getPath());
	}
	
	
	/**
	 * Gets the path of a file/directory WITHOUT the file/directory included. The final '/' or '\', 
	 * however, will be kept.
	 * 
	 * @param fileString String object containing the desired path.
	 * @return String object that contains the path of a given file/directory WITHOUT the file/directory.
	 */
	public static String getFileLocation(String fileString){
		StringBuilder stringToReturn = new StringBuilder(fileString);
		
		if (os.isWindows()){
			stringToReturn.replace(stringToReturn.lastIndexOf("\\") + 1, stringToReturn.length(), "");
		}
		else if (os.isUnix()){
			stringToReturn.replace(stringToReturn.lastIndexOf("/") + 1, stringToReturn.length(), "");
		}
		
		return stringToReturn.toString();
	}
	
	
	/**
	 * Forcibly creates a directory, as well as any directories needed for the directory to be created. 
	 * Will only fail if the path contains an illegal character, or the path already exists (including it 
	 * leading to a file).
	 * 
	 * @param path String object containing the path of the directory desired to be created.
	 * @return Whether or not the directory was successfully created.
	 */
	public static boolean forceCreateDirectory(String path){
		return forceCreateDirectory(new File(path));
	}
	
	
	/**
	 * Forcibly creates a directory, as well as any directories needed for the directory to be created. 
	 * Will only fail if the path contains an illegal character, or the path already exists (including it 
	 * leading to a file).
	 * 
	 * @param pathFile File object containing the path of the directory desired to be created.
	 * @return Whether or not the directory was successfully created.
	 */
	public static boolean forceCreateDirectory(File pathFile){
		if (!pathFile.exists()){
			final String ILLEGAL_CHARACTERS = ":*?\"<>|";
			
			String path = pathFile.getPath();
			
			for (int i = 0; i < path.length(); ++i){	//Illegal character check.
				for (int q = 0; q < ILLEGAL_CHARACTERS.length(); ++q){
					if (path.charAt(i) == ILLEGAL_CHARACTERS.charAt(q)){
						if ((path.charAt(i) == ':' && i == 1) && os.isWindows()){
							//You're good, that's just the drive in Windows systems.
						}
						else{
							return false;	//Illegal character found.
						}
					}
				}
			}
			
			
			//Create Directories:
			String builder = new String();
			File temp = null;
			
			for (int i = 0; i < path.length(); ++i){
				if (path.charAt(i) == '\\' || path.charAt(i) == '/'){
					temp = new File(builder);
					
					if (temp.exists() && !temp.isDirectory()){	//Only directories should be in the path.
						return false;
					}
					else if (temp.exists()){	//No need to make the directory if it already exists.
						builder += path.charAt(i);
					}
					else{
						temp.mkdir();
						
						builder += path.charAt(i);
					}
				}
				else{
					builder += path.charAt(i);	//The character is added to the builder String when not a 
												//type of slash.
				}
			}
			
				
			pathFile.mkdir();	//Gets the last directory in the sequence.
			
			
			return true;
		}
		else{	//The directory already exists.
			return false;
		}
	}
	
	
	/**
	 * Forcibly deletes a directory (or a file), despite any sub-directories or sub-files.
	 * <p>
	 * Cannot be undone.
	 * 
	 * @param path String object containing the path to delete.
	 * @return Whether or not the delete was successful.
	 */
	public static boolean forceDeleteDirectory(String path){
		return forceDeleteDirectory(new File(path));
	}
	
	
	/**
	 * Forcibly deletes a directory (or a file), despite any sub-directories or sub-files.
	 * <p>
	 * Cannot be undone.
	 * 
	 * @param pathFile File object containing the path to delete.
	 * @return Whether or not the delete was successful.
	 */
	public static boolean forceDeleteDirectory(File pathFile){
		if (pathFile.isDirectory()){
			String[] items = pathFile.list();
			
			for (String item : items){
				File temp = new File(pathFile.getPath() + File.separator + item);
				
				forceDeleteDirectory(temp);
			}
			
			
			return pathFile.delete();
		}
		else if (pathFile.isFile()){
			return pathFile.delete();
		}
		else{
			return false;
		}
	}
	
	
	private static LinkedList<String> filesToAdd = null;
	private static LinkedList<ZipEntry> entries = null;
	
	
	/**
	 * Zips the given file/directory into a .zip file.
	 * <p>
	 * Note that if the zipping fails, there will likely still be a .zip file created, and it will be 
	 * corrupted.
	 * <p>
	 * File-reading is handled with a by-file-size buffer, meaning that the zipping process is optimized to 
	 * be as fast as possible, but suffers from high memory usage if any files in the target are large.
	 * 
	 * @param fileToZip String object containing the path of the file/directory to zip.
	 * @param zipLocation String object containing the desired path for the .zip file.
	 * @return Whether or not the zip was successful.
	 */
	public static boolean zip(String fileToZip, String zipLocation){
		return zip(new File(fileToZip), new File(zipLocation));
	}
	
	
	/**
	 * Zips the given file/directory into a .zip file.
	 * <p>
	 * Note that if the zipping fails, there will likely still be a .zip file created, and it will be 
	 * corrupted.
	 * <p>
	 * File-reading is handled with a by-file-size buffer, meaning that the zipping process is optimized to 
	 * be as fast as possible, but suffers from high memory usage if any files in the target are large.
	 * 
	 * @param fileToZip File object containing the path of the file/directory to zip.
	 * @param zipLocation File object containing the desired path for the .zip file.
	 * @return Whether or not the zip was successful.
	 */
	public static boolean zip(File fileToZip, File zipLocation){
		filesToAdd = new LinkedList<>();
		entries = new LinkedList<ZipEntry>();
		
		addFileList(fileToZip, fileToZip.getName());
		
		
		//Convert lists to arrays for more efficient access:
		String[] filesToAdd_Array = new String[0];
		filesToAdd_Array = filesToAdd.toArray(filesToAdd_Array);
		
		ZipEntry[] entries_Array = new ZipEntry[0];
		entries_Array = entries.toArray(entries_Array);
		
		
		FileOutputStream zipFileStream;
		try{
			zipFileStream = new FileOutputStream(zipLocation);
		}
		catch (FileNotFoundException e){
			return false;
		}
		ZipOutputStream zipFileStream_Zip = new ZipOutputStream(zipFileStream);
		
		FileInputStream in = null;
		ZipEntry entry = null;
		
		try{
			byte[] buffer = new byte[0];
			for (int i = 0; i < entries_Array.length; ++i){
				entry = new ZipEntry(entries_Array[i]);
				zipFileStream_Zip.putNextEntry(entry);
				
				
				File temp_file = new File(filesToAdd_Array[i]);
				in = new FileInputStream(temp_file);
				buffer = new byte[(int)temp_file.length()];
				
				while (in.read(buffer) > 0){
					zipFileStream_Zip.write(buffer);
				}
				
				in.close();
				zipFileStream_Zip.closeEntry();
			}
			
			
			zipFileStream_Zip.close();
		}
		catch (IOException e){
			return false;
		}
		
		filesToAdd_Array = null;
		return true;
	}
	
	
	/**
	 * Zips the given file/directory into a .zip file.
	 * <p>
	 * Note that if the zipping fails, there will likely still be a .zip file created, and it will be 
	 * corrupted.
	 * 
	 * @param fileToZip String object containing the path of the file/directory to zip.
	 * @param zipLocation String object containing the desired path for the .zip file.
	 * @param bufferSize The size of the buffer for file-reading. Larger values make the zipping faster, but 
	 * take more RAM to complete. 1024 is a great intermediate value.
	 * @return Whether or not the zip was successful.
	 */
	public static boolean zip(String fileToZip, String zipLocation, int bufferSize){
		return zip(new File(fileToZip), new File(zipLocation), bufferSize);
	}
	
	
	/**
	 * Zips the given file/directory into a .zip file.
	 * <p>
	 * Note that if the zipping fails, there will likely still be a .zip file created, and it will be 
	 * corrupted.
	 * 
	 * @param fileToZip File object containing the path of the file/directory to zip.
	 * @param zipLocation File object containing the desired path for the .zip file.
	 * @param bufferSize The size of the buffer for file-reading. Larger values make the zipping faster, but 
	 * take more RAM to complete. 1024 is a great intermediate value.
	 * @return Whether or not the zip was successful.
	 */
	public static boolean zip(File fileToZip, File zipLocation, int bufferSize){
		filesToAdd = new LinkedList<>();
		entries = new LinkedList<ZipEntry>();
		
		addFileList(fileToZip, fileToZip.getName());
		
		
		//Convert lists to arrays for more efficient access:
		String[] filesToAdd_Array = new String[0];
		filesToAdd_Array = filesToAdd.toArray(filesToAdd_Array);
		
		ZipEntry[] entries_Array = new ZipEntry[0];
		entries_Array = entries.toArray(entries_Array);
		
		
		FileOutputStream zipFileStream;
		try{
			zipFileStream = new FileOutputStream(zipLocation);
		}
		catch (FileNotFoundException e){
			return false;
		}
		ZipOutputStream zipFileStream_Zip = new ZipOutputStream(zipFileStream);
		
		FileInputStream in = null;
		ZipEntry entry = null;
		
		try{
			byte[] buffer = new byte[1024];
			for (int i = 0; i < entries_Array.length; ++i){
				entry = new ZipEntry(entries_Array[i]);
				zipFileStream_Zip.putNextEntry(entry);
				
				
				in = new FileInputStream(new File(filesToAdd_Array[i]));
				while (in.read(buffer) > 0){
					zipFileStream_Zip.write(buffer);
				}
				
				in.close();
				zipFileStream_Zip.closeEntry();
			}
			
			
			zipFileStream_Zip.close();
		}
		catch (IOException e){
			return false;
		}
		
		filesToAdd_Array = null;
		return true;
	}
	
	
	/**
	 * Adds a given file's list of items and nested items into the filesToAdd LinkedList.
	 * 
	 * @param file File object containing the item to add.
	 * @param source String object containing the name of the source directory.
	 */
	private static void addFileList(File file, String source){
		if (file.isDirectory()){
			for (String item : file.list()){
				File temp = new File(file.getAbsolutePath() + File.separator + item);
				
				if (temp.isDirectory()){
					addFileList(temp, source + File.separator + temp.getName());
				}
				else{
					addFileList(temp, source);
				}
			}
		}
		else{
			String path = file.getAbsolutePath();
			String name = file.getName();
			
			filesToAdd.add(path);
			entries.add(new ZipEntry(source + File.separator + name));
		}
		
	}
	
	
//	public static void main(String[] args) throws IOException{
//		//zip("C:\\Users\\stonisg\\Desktop\\Kid Pix", "C:\\Users\\stonisg\\Desktop\\it.zip");
//		
//		System.out.println(forceDeleteDirectory("C:\\Users\\stonisg\\Desktop\\Test\\yo - Copy"));
//		
//		//System.out.println(new File("C:\\Users\\stonisg\\Desktop\\Test\\yo - Copy").isDirectory());
//		
//		System.out.println(new File(
//				"C:\\Users\\stonisg\\Desktop\\Test\\yo - Copy\\wefewfewfwef.txt").isFile());
//	}
}



















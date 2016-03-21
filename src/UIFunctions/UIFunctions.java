package UIFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

import UI.UI;

public class UIFunctions {
	
static boolean uploaded = false;
static boolean downloaded=false;
public static DbxClient client;
public static boolean auth = false;
	
	/** authenticates and connects to the drop box account.
	 * @return
	 * @throws DbxException
	 */
	public static boolean authenticate() throws DbxException
	{
	final String APP_KEY = "mcekt24kv5cnvg3"; // change with yours
    final String APP_SECRET = "yi87iajlggkwroe"; // change with yours
    System.out.println("Entered authentication");
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

    DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
    // Have the user sign in and authorize your app.
    String authorizeUrl = webAuth.start();
    String myToken = "raj4TOJ9dYAAAAAAAAAALvYJeV14ZgPhjoORf1B3HpGBMcG_yh9pkeIGq0k54PoJ"; 
    
    client = new DbxClient(config, myToken);
    auth=true;
    System.out.println("Linked account: " + client.getAccountInfo().displayName);
    return auth;

	}
	
	
	/** lists the users Dropbox files.
	 * @param folderPath
	 * @throws DbxException
	 */
	public static void listDropboxFolders(String folderPath) throws DbxException {
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(folderPath);
		UI.txtrListOfFiles.setText("");
		System.out.println("Entered ListOfDropBoxFolders");
		
		for(DbxEntry child: listing.children)
		{
			
			if(child.isFolder())
			{	
				
				UI.txtrListOfFiles.append("Folder :" +child.name +"\n");
				System.out.println("Folder: "+child.path);
				listDropboxFolders("/"+child.name);
			}
			else
			{
				
				UI.txtrListOfFiles.append("	"+child.path+"\n");
				System.out.println("	" + child.path);
			}
		}	
		
	}
	
	
	/**Uploads files to Dropbox.
	 * @param fileName
	 * @param file
	 * @return
	 * @throws DbxException
	 * @throws IOException
	 */
	public static boolean uploadToDropbox(String fileName,String file) throws DbxException,
	IOException {
		File inputFile = new File(fileName);
		FileInputStream finput = new FileInputStream(inputFile);
		
		try {
						
			DbxEntry.File uploadedFile = client.uploadFile("/" +file,
					DbxWriteMode.add(), inputFile.length(), finput);
			uploaded = true;
			String sharedUrl = client.createShareableUrl("/" +file);
			System.out.println("Uploaded: " + uploadedFile.toString() + " URL "
			+ sharedUrl);
			//Append it in the textarea
			String file_name=uploadedFile.path;
			
			UI.txtrListOfFiles.append("	"+file_name+"\n");
			
		} finally {
			finput.close();
		}
		return uploaded;
	}
	
	
	/**Downloads files from the Dropbox.
	 * @param fileName
	 * @return
	 * @throws DbxException
	 * @throws IOException
	 */
	public static boolean downloadFromDropbox(String fileName) throws DbxException,IOException {
		String dfile ="temp/";
		FileOutputStream outputStream = new FileOutputStream(dfile+fileName);
		try {
			DbxEntry.File downloadedFile = client.getFile("/" + fileName,null,outputStream);
			System.out.println("Metadata: " + downloadedFile.toString());
			downloaded=true;
		} finally {
	outputStream.close();
		}
		return downloaded;
	}
	
	
	
	/** generates list of files for drop down list.
	 * @param folderPath
	 * @return
	 * @throws DbxException
	 */
	public static ArrayList<String> listDropDownFiles(String folderPath) throws DbxException {
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(folderPath);
		System.out.println("Entered List Of DropDown files ");
		
		ArrayList<String> dropdownArray= new ArrayList<String>();
		
		
		for(DbxEntry child: listing.children)
		{
			
			if(child.isFolder())
			{	
				
				//UI.txtrListOfFiles.append("Folder :" +child.name +"\n");
				//System.out.println("Folder: "+child.path);
				listDropboxFolders("/"+child.name);
			}
			else
			{
				dropdownArray.add(child.path);
				
				//UI.txtrListOfFiles.append("	"+child.path+"\n");
				//System.out.println("	" + child.path);
			}
		}	
		
		return dropdownArray;
	}
	
	
	
}

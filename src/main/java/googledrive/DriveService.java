package googledrive;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;

import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
/**
 * A sample application that runs multiple requests against the Drive API. The requests this sample
 * makes are:
 * <ul>
 * <li>Does a resumable media upload</li>
 * <li>Updates the uploaded file by renaming it</li>
 * <li>Does a resumable media download</li>
 * <li>Does a direct media upload</li>
 * <li>Does a direct media download</li>
 * </ul>
 *
 * @author rmistry@google.com (Ravi Mistry)
 */
public class DriveService {

	/**
	   * Be sure to specify the name of your application. If the application name is {@code null} or
	   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	   */
	  private static final String APPLICATION_NAME = "";

	  /** Directory to store user credentials. */
	  private static final java.io.File DATA_STORE_DIR =
	      new java.io.File(System.getProperty("user.home"), ".store/drive_sample");

	  /**
	   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	   * globally shared instance across your application.
	   */
	  private static FileDataStoreFactory dataStoreFactory;

	  /** Global instance of the HTTP transport. */
	  private static HttpTransport httpTransport;

	  /** Global instance of the JSON factory. */
	  private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	  /** Global Drive API client. */
	  private static Drive drive;

	  /** Authorizes the installed application to access user's protected data. */
	  private static Credential authorize() throws Exception {
	    // load client secrets
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	        new InputStreamReader(DriveService.class.getResourceAsStream("/client_secrets.json")));
	    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
	        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
	      System.out.println(
	          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
	              + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
	      System.exit(1);
	    }
	    // set up authorization code flow
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
	        JSON_FACTORY, clientSecrets, DriveScopes.all())
	            .setDataStoreFactory(dataStoreFactory).build();
	    // authorize
	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	  }
	  public static Drive getDriveService() throws Exception {	        
	        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	        Credential credential = authorize();
	        return new Drive.Builder(
	        		httpTransport, JSON_FACTORY, credential)
	                .setApplicationName(APPLICATION_NAME)
	                .build();
	    }
	  public static void doUploadFile(String UPLOAD_FILE_PATH, String ContentType) {
	    Preconditions.checkArgument(
	        !UPLOAD_FILE_PATH.startsWith("Enter "),
	        "Please enter the upload file path and download directory in %s", DriveService.class);

	    try {
	    	java.io.File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);
	      // set up the global Drive instance
	      drive = getDriveService();
	      // run commands
	      
	      View.header1("Starting Resumable Media Upload");
	      File uploadedFile = uploadFile(true, UPLOAD_FILE, ContentType);
	      
	      View.header1("Success!");
	      return;
	    } catch (IOException e) {
	      System.err.println(e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	    System.exit(1);
	  }
	  public static List<String> doSearch(String name) {
			// TODO Auto-generated method stub
		  List<String> links = new ArrayList<String>();
		  try {
			  dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		      // set up the global Drive instance
		      drive = getDriveService();
		      // run
		      
		      View.header1("Starting Resumable Media Upload");
		      String pageToken = null;
		      do {
		          FileList result = drive.files().list()
		                  .setQ("name = '"+name+"'")
		                  .setSpaces("drive")
		                  .setFields("nextPageToken, files")
		                  .setPageToken(pageToken)
		                  .execute();
		          for(File file: result.getFiles()) {
		              links.add(file.getWebViewLink());
		          }
		          pageToken = result.getNextPageToken();
		      } while (pageToken != null);
		      
		      View.header1("Success!");
		      return links;
		    } catch (IOException e) {
		      System.err.println(e.getMessage());
		    } catch (Throwable t) {
		      t.printStackTrace();
		    }
		  	return links;
		}
	  /** Uploads a file using either resumable or direct media upload. */
	  private static File uploadFile(boolean useDirectUpload, java.io.File UPLOAD_FILE, String ContentType) throws IOException {
	    File fileMetadata = new File();
	    fileMetadata.setName(UPLOAD_FILE.getName());

	    FileContent mediaContent = new FileContent(ContentType, UPLOAD_FILE);

	    Drive.Files.Create insert =  drive.files().create(fileMetadata, mediaContent);
	    MediaHttpUploader uploader = insert.getMediaHttpUploader();
	    uploader.setDirectUploadEnabled(useDirectUpload);
	    uploader.setProgressListener(new FileUploadProgressListener());
	    return insert.execute();
	  }
	  

}

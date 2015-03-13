/**
 * This class contains all Network-related things that must run in the background of the UI. 
 * 
 * @author hackjunky, jacrin
 *
 */
public class ClientMain {
	AppletUI ui;
	
	public ClientMain(AppletUI instance) {
		ui = instance;
	}
	
	/**
	 * This method should only be called by the LoginDialog. A result of True will init the main user interface.
	 * @param username The username of the client.
	 * @param password The password of the client.
	 * @return Returns true if the username and the password in the database exist.
	 */
	public boolean Authenticate(String username, String password) {
		boolean isValid = true;
		
		if (isValid) {
			ui.showControls();
			ui.username = username;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * This class is the Thread that will run inside the client to establish the connection to the server, and download all relevant
	 * data. This includes the Strings containing airplane information, and the user's permissions level in the database.
	 * @author hackjunky, jacrin
	 *
	 */
	public class NetworkLayer implements Runnable{
		
		@Override
		public void run() {
			
		}
	}
}

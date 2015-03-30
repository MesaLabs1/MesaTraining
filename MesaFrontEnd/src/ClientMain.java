import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains all Network-related things that must run in the background of the UI. 
 * 
 * @author hackjunky, jacrin
 *
 */
public class ClientMain {
	static final int FRONTEND_VERSION = 1;
	AppletUI ui;

	Thread networkClient;
	NetworkLayer client;

	public ClientMain(AppletUI instance) {
		ui = instance;
	}

	/**
	 * Log is a method that posts the sender name, the time, and the message. 
	 * @param message The message to display in the system console.
	 * 
	 */
	private static SimpleDateFormat timeFormatter= new SimpleDateFormat("hh:mm:ss a");

	void Log(String message) {
		Date date = new Date();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = timeFormatter.format(date);

		String log = "[" + sender + "@" + time +"]: " + message;

		//Print it
		System.out.println(log);
	}

	/**
	 * The following block of code allows this class to fetch the calling class' data
	 * without placing any additional resource load on Thread than is necessary.
	 */
	private static final int CLIENT_CODE_STACK_INDEX;
	static {
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			i++;
			if (ste.getClassName().equals(ClientMain.class.getName())) {
				break;
			}
		}
		CLIENT_CODE_STACK_INDEX = i;
	}

	/**
	 * This method should only be called by the LoginDialog. A result of True will init the main user interface.
	 * @param username The username of the client.
	 * @param password The password of the client.
	 * @return Returns true if the username and the password in the database exist.
	 */
	public boolean Authenticate(String username, String password) {
		boolean isValid = true;

		if (networkClient != null) {
			networkClient.interrupt();
		}

		networkClient = new Thread(client = new NetworkLayer(username, password));

		if (isValid) {
			ui.showControls();
			ui.username = username;
			return true;
		}else {
			return false;
		}
	}

	/**
	 * This class is the Thread that will run inside the client to establish the connection to the client, and download all relevant
	 * data. This includes the Strings containing airplane information, and the user's permissions level in the database.
	 * @author hackjunky, jacrin
	 *
	 */
	public class NetworkLayer implements Runnable {
		private Socket client;
		private String username;
		private String password;

		boolean active = true;
		boolean validated = false;
		
		DataInputStream in;
		DataOutputStream out;
		
		/**
		 * Initialize this class with the attempted authentication information.
		 * @param user The client's username.
		 * @param pass The client's password.
		 */
		public NetworkLayer(String user, String pass) {
			username = user;
			password = pass;
		}

		@Override
		public void run() {
			while(active) {
				try {
					in = new DataInputStream(client.getInputStream());
					out = new DataOutputStream(client.getOutputStream());

					Log("Client initializing on " + client.getLocalAddress() + "@" + client.getLocalPort() + ".");
					Log("Preparing to handshake the client at " + client.getRemoteSocketAddress() + ". I hope I know the secret handshake.");

					/*
					 * Client Sends: Version, Computer Name, IP-Address
					 * client Checks: Version MATCH, IP-Address MATCH, Computer Name store.
					 * client Sends: Random sequence.
					 * Client Sends: Specific characters from the sequence. 
					 * client Checks: Sequence MATCH
					 * client Sends: Welcome!
					 * 
					 * -> Repeat
					 * 
					 * client Waits for Client Request
					 * Client: Request
					 * 
					 * -> Repeat
					 * 
					 * If the target fails out, we will close this connection and interpret the connection as malicious.
					 */

					/*
					 * We do not provide feedback to the user for the first two transmissions since we do not want them to know why they
					 * got rejected. These kinds of errors would only be solicited by attackers. Our software should always be compliant 
					 * with its own protocols.
					 */

					//Should be of format: %VERSION%NAME%IP%
					InetAddress addr;
					addr = InetAddress.getLocalHost();
					out.writeUTF("%" + FRONTEND_VERSION + "%" + addr.getHostName() + "%" + client.getLocalAddress() + "%");

					/*
					 * The server will terminate the connection here if we don't sign correctly. However, it will return a command string letting us
					 * know why we hurt it's feelings. 
					 */

					String callsign = in.readUTF();

					if (callsign.startsWith("$ERROR")) {		//Is the return not a callsign, but actually, an error report?
						Log("Server returned the network message " + callsign + ".");
					}else {
						//We need to return characters 57, 72, 15, 66, and 49
						String response = "" + callsign.charAt(57) + callsign.charAt(72) + callsign.charAt(15) + callsign.charAt(66) + callsign.charAt(49);

						//Send the code.
						out.writeUTF(response);

						//Again, the server will terminate the connection here if we don't respond correctly above.

						String ident = in.readUTF();

						if (ident.equals("$IDENTIFY")) {	//The server wants us to send our login information
							out.writeUTF("$IDENTIFY " + username + " " + password);

							String ret = in.readUTF();
							if (ret.equals("$VALID")) {
								/*
								 * The connection is validated. In this case, the server always waits for us to ask, and then
								 * responds. As long as a user is connected, we can send a network message at any time to request new data from the
								 * server.
								 */
								validated = true;		//We set this flag so the client doesn't terminate automatically.
							}else if (ret.equals("$INVALID")) {
								/* The server is written to handle repeated login attempts. However, we can also just tell the server
								 * to forget about it, so if a user stays on the log-in page, they don't hold a slot forever.
								 */
								out.writeUTF("$ABORT");
							}
						}
					}

					if (!validated) {
						client.close();
						active = false;
					}
				}catch(SocketTimeoutException s) {
					System.out.println("The socket has timed out and been reset.");
					break;
				}catch(IOException e) {
					System.out.println("IOException!");
					e.printStackTrace();
					break;
				}
			}
		}
		
		public String RemoteRequest(String request) {
			Log("Requesting data from the server...");
			if (validated) {
				String response = "$NODATA";
				try {
					out.writeUTF(request);
					Log("Decoding...");
					response = in.readUTF();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (response.equals("$INVALID")) {
					//This isn't a valid request.
					Log("Invalid Request.");
					return null;
				}else if (response.equals("$NODATA")) {
					//There was no data returned by the server.
					Log("No data was returned by the server.");
					return null;
				}else {
					return response;
				}
			}else {
				Log("Please wait until the server is fully initialized before requesting data!");
			}
			return null;
		}
	}
}

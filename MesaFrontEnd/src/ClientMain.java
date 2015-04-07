import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;

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
	
	boolean authenticated = false;

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
	public void Authenticate(String username, String password, JLoginDialog callback) {
		if (networkClient != null) {
			networkClient.interrupt();
		}

		/*
		 *	What is a callback? We can pass a superclass as a variable into a method knowing we will need to 
		 *	"call-back" the super class and let it know when we're done, since our operation is a BDO...
		 *	(background data operation), otherwise known as an ADO (asynchronous data operation).
		 */
		client = new NetworkLayer(username, password, callback);
		networkClient = new Thread(client);
		Log("Authenticating '" + username + "' with the remote server... (this process may hang)");
		networkClient.start();
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
		
		JLoginDialog callback;

		/**
		 * Initialize this class with the attempted authentication information.
		 * @param user The client's username.
		 * @param pass The client's password.
		 */
		public NetworkLayer(String user, String pass, JLoginDialog cback) {
			username = user;
			password = pass;
			callback = cback;
		}
		
		@Override
		public void run() {
			try {
				//TODO: We need the remote address for this connection.
				client = new Socket("127.0.0.1", 1337);

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

				//Should be of format: VERSION%NAME%IP%
				InetAddress addr;
				addr = InetAddress.getLocalHost();
				out.writeUTF(FRONTEND_VERSION + "%" + addr.getHostName() + "%" + client.getLocalAddress() + ":" + client.getLocalPort() + "%");

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

					Log("Sending authorization code " + response + ".");

					//Again, the server will terminate the connection here if we don't respond correctly above.

					String ident = in.readUTF();
					
					if (ident.equals("$IDENTIFY")) {	//The server wants us to send our login information
						String identification = "$IDENTIFY " + username + " " + password;
						out.writeUTF(identification);

						String ret = in.readUTF();
						if (ret.equals("$VALID")) {
							Log("Server says we're good to go. Awaiting serialization index list...");
							/*
							 * The connection is validated. In this case, the server always waits for us to ask, and then
							 * responds. As long as a user is connected, we can send a network message at any time to request new data from the
							 * server.
							 */
							validated = true;		//We set this flag so the client doesn't terminate automatically.
							authenticated = true;
							
							//Let's show the controls on the applet.
							ui.showControls();
							ui.username = username;
							
							//Tell the dialog we're good, so it can hide itself.
							callback.AuthSucess();
						}else if (ret.equals("$INVALID")) {
							/* The server is written to handle repeated login attempts. However, we can also just tell the server
							 * to forget about it, so if a user stays on the log-in page, they don't hold a slot forever.
							 */
							out.writeUTF("$ABORT");
							callback.AuthFailure();
						}
					}
				}

				if (!validated) {
					client.close();
					active = false;
				}
			}catch(SocketTimeoutException s) {
				Log("The socket has timed out and been reset.");
				active = false;
				s.printStackTrace();
			}catch(ConnectException c) {
				Log("Connection Refused.. is the server running?");
				active = false;
			}catch(IOException e) {
				Log("IOException!");
				active = false;
				e.printStackTrace();
			}
		}

		public String RemoteRequest(String request) {
			Log("Requesting data from the server...");
			if (validated) {
				String response = "$NODATA";
				try {
					out.writeUTF(request);
					Log("Requesting...");
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

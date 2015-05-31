import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * This class contains all Network-related things that must run in the background of the UI. 
 * 
 * @author hackjunky, jacrin
 *
 */
public class ClientMain {
	static final int FRONTEND_VERSION = 1;
	boolean authenticated = false;
	
	AppletUI ui;
	Thread networkClient;
	NetworkLayer client;

	ArrayList<String[]> data;
	
	ArrayList<StringPoint> refCodes;		//refCodes are used to help us store specific data points in the array that contain networked strings
											//We made a custom class called StringPoint that stores a String and an index, like: Names, 0
											//This tells the program that position 0 in the data array contains the names string
	NetworkTimer netTicker;
	Timer ticker;
	
	public ClientMain(AppletUI instance) {
		ui = instance;
		refCodes = new ArrayList<StringPoint>();
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
	 * This class simply re-requests data from the server every time it ticks.
	 * @author Hack
	 *
	 */
	public class NetworkTimer implements ActionListener {

		public NetworkTimer() {
			RequestList();
			Log("Receiving serialized objects...");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			UpdateLists();
			RequestList();
		}
		
		/**
		 * Requests the master lists from the server. You can easily add more requests here, for additional serializable data fields.
		 */
		public void RequestList() {
			//We need to serialize all data over to us, so let's add these requests to the master query...
			//Log("Querying server for new data...");
			client.SetRefCode("Dates", client.RemoteRequest("$GET DATES"));
			client.SetRefCode("Pilots", client.RemoteRequest("$GET PILOTS"));
			client.SetRefCode("Aircrafts", client.RemoteRequest("$GET AIRCRAFTS"));
			client.SetRefCode("Training", client.RemoteRequest("$GET TRAINING"));
			client.SetRefCode("Maintinence", client.RemoteRequest("$GET MAINTINENCE"));
			client.SetRefCode("Flight", client.RemoteRequest("$GET FLIGHT"));
			client.SetRefCode("Ranklist", client.RemoteRequest("$GET RANKLIST"));
		}
		
		/**
		 * Updates the UI from here, by feeding the UpdateListsWithArray method.
		 */
		public void UpdateLists() {
			//Read over each reference code, and the values, and prepare to parse.
			for (int i = 0; i < refCodes.size(); i++) {
				StringPoint value = refCodes.get(i);
				
				//Since each data type has different rules, we must differentiate each type.
				int index = value.GetIndex();
				
				if (value.GetValue().equals("Dates")) {
					//Date, so the format is: 05192015;182436
					//Example Date: 05/19/2015, at 18:24:36 (Global Time, so 6:24:36 PM). Indicates the last use date.
					
					String[] unformatted = data.get(index);
					String[] dates = new String[unformatted.length];
					
					for (int j = 0; j < unformatted.length; j++) {
						String[] split = unformatted[j].split(";");
						
						String month = split[0].substring(0, 2);
						String day = split[0].substring(2, 4);
						String year = split[0].substring(4, 8);
						
						String hour = split[1].substring(0, 2);
						String minute = split[1].substring(2, 4);
						String seconds = split[1].substring(4, 6);
						String suffix = "XX";
						if (Integer.parseInt(hour) > 12) {
							suffix = "PM";
						}else {
							suffix = "AM";
						}
						
						String out = month + "/" + day + "/" + year + " at " + hour + ":" + minute + ":" + seconds + " " + suffix;
						dates[j] = out;
					}
					//We've converted them all to coherent values, now apply them through the UpdateListsWithArray method
					ui.eventHandler.UpdateListWithArray(ui.dateModel, dates);
				}else if (value.GetValue().equals("Pilots")) {
					//Pilots, so format is: Jad Aboulhosn;Andreas Anderson
					//Supports multiple pilots, in this case both Jad and Andy.
					String[] unformatted = data.get(index);
					String[] names = new String[unformatted.length];
					
					for (int j = 0; j < unformatted.length; j++) {
						String[] split = unformatted[j].split(";");
						String out = "";
						for (int k = 0; k < split.length; k++) {
							out += split[k] + ",";
						}
						if (out.length() > 0) {
							out = out.substring(0, out.length() - 1);
						}else {
							out = "No Recorded Pilots";
						}
						names[j] = out;
					}
					ui.eventHandler.UpdateListWithArray(ui.pilotModel, names);
				}else if (value.GetValue().equals("Aircrafts")) {
					//Aircrafts, which is technically a single field. So let's literally copy, paste.
					String[] unformatted = data.get(index);
					ui.eventHandler.UpdateListWithArray(ui.nameModel, unformatted);
				}else if (value.GetValue().equals("Training")) {
					
				}else if (value.GetValue().equals("Maintinence")) {
					
				}else if (value.GetValue().equals("Flight")) {
					
				}else if (value.GetValue().equals("Ranklist")) {
					ui.rankModel.clear();
					ui.userModel.clear();
					//This array contains a string system where each entry is as follows FLASTNAME;RANK, so lets split, and add them to the userlist.
					for (String s : data.get(index)) {
						String[] split = s.split(";");
						String user = split[0];
						String rank = split[1];
						
						//Lets add them to the user list.
						ui.rankModel.addElement(rank);
						ui.userModel.addElement(user);
					}
				}			
			}
			refCodes = new ArrayList<StringPoint>();	//Clear the refCodes, when we're done.
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

		ArrayList<String> query;
		
		int QUERY_ID = 0;

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

			query = new ArrayList<String>();
			data = new ArrayList<String[]>();
		}
		
		/**
		 * Sets a reference code for the stored data type
		 * @param value The type of the data: Name, Airplane, etc
		 * @param index The location in the data superarray.
		 */
		public void SetRefCode(String value, int index) {
			for (int i = 0; i < refCodes.size(); i++) {
				StringPoint v = refCodes.get(i);
				if (v.GetValue().equals(value)) {
					v.SetIndex(index);
					//System.out.println("Overwriting " + value + " in " + index);
					return;
				}
			}
			//System.out.println("Storing " + value + " in " + index);
			//Data point doesnt exist, lets create it
			refCodes.add(new StringPoint(index, value));
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
							
							netTicker = new NetworkTimer();
							ticker = new Timer(2000, netTicker);
							ticker.start();
							
							//This means that our data array will look like this in a bit:
							//ID	DATA
							// 0	List of Dates
							// 1 	List of Pilots
							// 2 	List of Aircrafts
							// 3 	List of Training Logs
							// 4	List of Maintinence Logs
							// 5 	List of Flight Logs
							
							while (true) {
								if (query.size() > 0) {
									String request = query.get(0);
									String[] split = request.split(";");
									int id = Integer.parseInt(split[0]);
									request = split[1];
									query.remove(0);
									try {
										//Log("Requesting Query " + QUERY_ID + "...");
										out.writeUTF(request);
										response = in.readUTF();
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (response.equals("$INVALID")) {
										//This isn't a valid request.
										//Log("Invalid Request.");
									}else if (response.equals("$NODATA")) {
										//There was no data returned by the server.
										//Log("No data was returned by the server.");
									}else {
										String[] recv = null;
										if (response.startsWith("$LIST")) {
											recv = response.substring("$LIST ".length(), response.length()).split("$");
											//Log("Receiving data (" + recv.length + " bytes).");
											//Log("Writing to " + id);
											data.add(id, recv);		//We RECV the list, and put it at the Query ID so we can fetch it by ID later.
										}
										if (response.startsWith("$RANK ")) {
											recv = response.substring("$RANK ".length(), response.length()).split("$");
											//This is a single rank, so $RANK JABOULHOSN SUPERCOOL, is an example
											//Log("Receiving data (" + recv.length + " bytes).");
											//Log("Writing to " + id);
											data.add(id, recv);		//We RECV the list, and put it at the Query ID so we can fetch it by ID later.
										}
										if (response.startsWith("$RANKLIST ")) {
											recv = response.substring("$RANKLIST ".length(), response.length()).split("$");
											//This is all ranks, so $RANKLIST JABOULHOSN;SUPERCOOL$JCLOW;NOTASCOOL is an example
											//Log("Receiving data (" + recv.length + " bytes).");
											//Log("Writing to " + id);
											//The recv list is a list where each entry is FLASTNAME;RANK.
											data.add(id, recv);		//We RECV the list, and put it at the Query ID so we can fetch it by ID later.
										}
									}
								}else {
									out.writeUTF("$NOREQUEST");
								}
								
							}
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
				callback.ShowHelpText("Call the Pope, all hell just broke loose.");
				active = false;
				s.printStackTrace();
			}catch(ConnectException c) {
				Log("Connection Refused.. is the server running?");
				callback.ShowHelpText("Connection refused. Is the server running?");
				active = false;
			}catch(IOException e) {
				Log("IOException!");
				active = false;
				e.printStackTrace();
			}
		}

		public int RemoteRequest(String request) {
			//Log("Sending '" + request + "' to the server...");
			if (validated) {
				query.add(QUERY_ID + ";" + request);
				
				int num = QUERY_ID; 
				QUERY_ID++;
				if (QUERY_ID > 1000) {	//We store our IDs recursively, but at 1000 entries, i think it's safe to reset and delete old data
					QUERY_ID = 0;
				}
				return num;
			}else {
				//Log("Please wait until the server is fully initialized before requesting data!");
			}
			return -1;
		}
	}
	
	public class StringPoint {
		int num;
		String string;
		
		public StringPoint(int index, String val) {
			num = index;
			string = val;
		}
		
		public int GetIndex() {
			return num;
		}
		
		public String GetValue() {
			return string;
		}
		
		public void SetIndex(int i) {
			num = i;
		}
		
		public void SetValue(String s) {
			string = s;
		}
	}
}

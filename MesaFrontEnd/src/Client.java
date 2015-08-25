import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Client {
	static final int FRONTEND_VERSION = 2;
	boolean authenticated = false;

	UI ui;
	Thread networkClient;
	NetworkLayer instance;

	Payload payload;

	public Client(UI instance) {
		ui = instance;
		payload = new Payload();
	}

	
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a");

	void Log(String message) {
		Date date = new Date();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = timeFormatter.format(date);

		String log = "[" + sender + "@" + time +"]: " + message;

		//Print it
		System.out.println(log);
		ui.consoleModel.addElement(log);
	}

	void SilentLog(String mesage) {
		System.out.println(mesage);
		ui.consoleModel.addElement(mesage);
	}

	
	private static final int CLIENT_CODE_STACK_INDEX;
	static {
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			i++;
			if (ste.getClassName().equals(Client.class.getName())) {
				break;
			}
		}
		CLIENT_CODE_STACK_INDEX = i;
	}

	
	public void authenticate(String username, String password, JLoginDialog callback) {
		if (networkClient != null) {
			networkClient.interrupt();
		}

		/*
		 *	What is a callback? We can pass a superclass as a variable into a method knowing we will need to 
		 *	"call-back" the super class and let it know when we're done, since our operation is a BDO...
		 *	(background data operation), otherwise known as an ADO (asynchronous data operation).
		 */
		instance = new NetworkLayer(username, password, callback);
		networkClient = new Thread(instance);
		Log("Authenticating '" + username + "' with the remote server... (this process may hang)");
		networkClient.start();
	}

	public Payload getPayload() {
		return payload;
	}

	
	public class NetworkLayer implements Runnable {
		private Socket client;
		private String username;
		private String password;

		private String rank = "user"; 	//Defaults to no rank.

		boolean active = true;
		boolean validated = false;

		DataInputStream in;
		DataOutputStream out;

		ArrayList<String> query;

		int QUERY_ID = 0;

		JLoginDialog callback;

		
		public NetworkLayer(String user, String pass, JLoginDialog cback) {
			username = user.toLowerCase();
			password = pass;
			callback = cback;

			query = new ArrayList<String>();
		}

		public String getUsername() {
			return username;
		}

		@Override
		public void run() {
			try {
				//TODO: We need the remote address for this connection.
				client = new Socket("127.0.0.1", 1337);

				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				out = new DataOutputStream(client.getOutputStream());
				
				in = new DataInputStream(client.getInputStream());

				Log("Client initializing on " + client.getLocalAddress() + "@" + client.getLocalPort() + ".");

				Log("Preparing to handshake the client at " + client.getRemoteSocketAddress() + ". Transmitting authorization protocol.");

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
				 * Server: Response
				 * Server: Synchronize data class
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
					if (callsign.startsWith("$ERROR, VERSION: ")) {
						callback.AuthFailure();
						callback.ShowHelpText("Downloading update... (this may take a second)");
						String remoteSource = callsign.substring("$ERROR, VERSION: ".length(), callsign.length());
						downloadFile("update.zip", "https://www.dropbox.com/s/ecatxsny3cxvoru/update.zip?dl=1");
					}
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
							ui.setVisible(true);
							ui.username = username;

							//Tell the dialog we're good, so it can hide itself.
							callback.AuthSucess();

							while (true) {
								if (query.size() > 0) {
									String request = query.get(0);
									String[] split = request.split(";");
									int id = Integer.parseInt(split[0]);
									request = split[1];
									if (split.length > 2) {
										for (int i = 2; i < split.length; i++) {
											request += ";" + split[i];
										}
									}
									query.remove(0);
									try {
										//Log("[QUERY] " + request + "; ID:" + id);
										out.writeUTF(request);
										response = in.readUTF();

									} catch (IOException e) {
										e.printStackTrace();
									}
									if (response.equals("$INVALID")) {
										//This isn't a valid request.
										Log("Invalid request.");
									}else if (response.equals("$NODATA")) {
										//There was no data returned by the server.
										//Log("No data was returned by the server.");
									}else if (response.equals("$PERMS")) {
										Log("You do not have the sufficient priveleges for this action.");
									}else if (response.equals("$ERROR")) {
										Log("Invalid request: '" + request + "'.");
									}else if (response.equals("$SUCCESS")) {
										Log("The remote operation was completed successfully.");
									}else if (response.startsWith("$FAILURE")) {
										if (response.length() > "$FAILURE".length()) {
											Log("The remote operation has failed. Reason: " + response.substring("$FAILURE ".length(), response.length()));
										}else {
											Log("The remote operation has failed for an unstated reason.");
										}
									}else if (response.startsWith("$RECOVERY ")) {
										String password = response.substring("RECOVERY ".length(), response.length());
										ui.ShowUIDialog("MesaLabs Password Recovery", "The password for this user was recovered successfully. It is: " + password);
									}else {
										String[] recv = null;
										if (response.startsWith("$RANK ")) {
											recv = response.substring("$RANK ".length(), response.length()).split(";");
											if (recv[0].equals(username)) {
												rank = recv[1];
											}
										}
									}
								}else {
									out.writeUTF("$NOREQUEST");
									try {
										payload = (Payload)ois.readObject();
									} catch (ClassNotFoundException e) {
										Log("The UIDs for the server and client do not match!");
										e.printStackTrace();
									}
									ui.lblUsers.setText("" + payload.getNumUsers());
									ui.lblUsersOnline.setText("" + payload.getNumOnline());
									ui.lblServerUptime.setText(payload.getUptime().substring("Uptime: ".length(), payload.getUptime().length()));
									ui.lblMemoryUsage.setText(payload.getMemUsage());
									ui.lblNetworkOverhead.setText("" + payload.getNumOverhead());
									ui.lblNetworkIP.setText("" + payload.getNetIP());
									ui.lblBufferSize.setText(QUERY_ID + "");

									//As a security measure, we've moved the rank check to a completely separate command. We can add a signature to this to make it even more secure.
									instance.remoteRequest("$GET RANK " + instance.username);
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
				if (ui != null) {
					ui.setVisible(false);
				}
				active = false;
				s.printStackTrace();
			}catch(ConnectException c) {
				Log("Connection Refused.. is the server running?");
				callback.ShowHelpText("Connection refused. Is the server running?");
				if (ui != null) {
					ui.setVisible(false);
				}
				active = false;
			}catch(IOException e) {
				Log("The socket has been reset.");
				active = false;
				if (ui != null) {
					ui.setVisible(false);
				}
				e.printStackTrace();
			}
		}

		public void downloadFile(final String filename, final String urlString)
		        throws MalformedURLException, IOException {
		    BufferedInputStream in = null;
		    FileOutputStream fout = null;
		    try {
		        in = new BufferedInputStream(new URL(urlString).openStream());
		        fout = new FileOutputStream(filename);

		        final byte data[] = new byte[1024];
		        int count;
		        while ((count = in.read(data, 0, 1024)) != -1) {
		            fout.write(data, 0, count);
		        }
		    } finally {
		        if (in != null) {
		            in.close();
		        }
		        if (fout != null) {
		            fout.close();
		        }
		    }
		}
		
		public int remoteRequest(String request) {
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

		public String getRank() {
			return rank;
		}
	}
}

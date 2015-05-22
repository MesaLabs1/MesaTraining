import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import javax.swing.Timer;

/**
 * This is the main network-socketed class for the backend. 
 * 
 * @author hackjunky, jacrin
 *
 */
public class Backend {
	//PropertyMaster reference object from INIT
	Init.PropertyMaster properties;
	Utils util;

	//Creative shit.
	enum ThreadNames {
		Bashful, Doc, Dopey, Grumpy, Happy, Sleepy, Sneezy
	}

	//Network Master -- all hail
	NetworkMaster netMaster;
	Thread netMasterThread;
	
	//DatabaseRunner -- something witty
	DatabaseManager dbMan;

	//Timer
	Timer eventTicker;
	EventHandler eventHandler;

	public Backend(Init.PropertyMaster prop) {
		properties = prop;
		util = properties.util;

		util.Log("Backend main program is initializing...");
		
		//Create a new NetworkMaster
		netMaster = new NetworkMaster();
		
		//Initialize the DBManager and point it to the shared resources
		dbMan = new DatabaseManager(util, netMaster.ui);
		
		//Create a wrapper thread
		netMasterThread = new Thread(netMaster);

		//Populate the Timer
		eventHandler = new EventHandler();
		eventTicker = new Timer(100, eventHandler);

		//Init the thread.
		netMasterThread.start();

		//Init the Timer
		eventTicker.start();
	}


	public class EventHandler implements ActionListener{
		Backend backend;
		UI ui;
		NetworkMaster netMaster;

		public EventHandler() {
			backend = Backend.this;
			netMaster = backend.netMaster;
			ui = netMaster.ui;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			backend = Backend.this;
			netMaster = backend.netMaster;
			ui = netMaster.ui;

			if (ui.activate) {
				util.Log("Activating the Network Listener...");
				ui.activate = false;
				netMasterThread = new Thread(netMaster);
				netMasterThread.start();
			}
			if (ui.deactivate) {
				util.Log("Deactivating the Network Listener...");
				ui.deactivate = false;
				ui.SetStatus(UI.ServerStatus.Inactive);
				netMasterThread.interrupt();
			}

			for (int i = 0; i < netMaster.networkThreads.length; i++) {
				if (netMaster.networkThreads[i] != null) {
					if (!netMaster.networkThreads[i].isAlive()) {
						netMaster.networkThreads[i] = null;
						netMaster.networkSockets[i] = null;
					}
				}
			}
			
			netMaster.UpdateUI();
		}
	}

	/**
	 * This class, the NetworkMaster, is the master class of the NetworkSocket.
	 * @author hackjunky, jacrin
	 */
	/*
	 * Multi-threading Introduction.
	 * 
	 * Runnable, the implementation, allows this while-true loop to run continuously until the connection with the client has been fully
	 * negotiated. Since this would normally make the program hang, this class is its own thread. It runs asynchronously in the background
	 * along side a designated number of threads. Bear in mind that whatever number was designated in PropertyMaster.NETWORK_MAX_CONNECTIONS
	 * is the number of threads to be created. This does not mean, say, that 16 threads are always running, but that they can be, if need be.
	 * 
	 * If a system lags hard, or the program runs out of memory, reducing the number of threads by lowering the NETWORK_MAX_CONNECTIONS number
	 * in the configuration file might be a good idea.
	 * 
	 * Network Introduction
	 * 
	 * Networking at this level is simple by concept. When we create an instance of this class, it is dormant. It's waiting for a client to attempt
	 * a connection to this port. When this connection has been created, the server will then proceed from the accept() method, and then launches a new
	 * instance of the NetworkSocket with an attached Socket object, the object that contains the connection between us and the client, and a name. The 
	 * name is an arbitrary identifier that we can use in debugging to identify misbehaving threads. The enumerator in the Backend.java class for ThreadNames
	 * contains the master list of all names, and then, after attaching 3 arbitrary integers to it for security reasons, we give the thread an init signal
	 * by starting the thread. From here, it's on it's own.
	 * 
	 * We've stored all the threads here in a NetworkThreads variable. At any point, we can interrupt an active connection by accessing it in the Array, and
	 * calling Interrupt(). This will cancel the running job, and set the thread to idle. It is also recommended that a thread make a callback such that when
	 * it terminates, we can delete the object, and mark the slot as available.
	 *
	 */
	public class NetworkMaster implements Runnable {
		//References to various classes for ease
		Backend instance = Backend.this;
		Utils util = instance.properties.util;
		UI ui = instance.properties.ui;

		//The ServerSocket that will spawn all connection Sockets...
		ServerSocket serverSocket;

		//Network Variables
		Thread[] networkThreads;

		NetworkSocket[] networkSockets;

		Socket socket;

		public NetworkMaster() {
			networkThreads = new Thread[properties.NETWORK_MAX_CONNECTIONS];
			networkSockets = new NetworkSocket[properties.NETWORK_MAX_CONNECTIONS];
		}

		/**
		 * We can populate the UI from this method.
		 */
		public void UpdateUI() {
			int threads = 0;
			int clients = 0;
			int handles = 0;
			for (int i = 0; i < networkThreads.length; i++) {
				if (networkThreads[i] != null) {
					threads++;
				}
				if (networkSockets[i] != null) {
					handles++;
					if (networkSockets[i].server.isConnected()) {
						clients++;
					}
				}
			}	
			ui.numThreads = threads;
			ui.numClients = clients;
			ui.numHandles = handles;
		}

		public void run() {
			//Create a master socket at our port, defined in PropertyMaster.NETWORK_PORT
			try {
				try {
					serverSocket = new ServerSocket(instance.properties.NETWORK_PORT);
				}catch (Exception e) {
					util.Log("Server failed to bind to port... is it already running?");
					util.Log("Ignore this message if you've just reactivated the listener.");
				}

				Random rand = new Random();

				//This will execute eternally until the program terminates. It's in a separate thread for this reason.
				while (true) {
					UpdateUI(); 
					ui.SetStatus(UI.ServerStatus.Active);
					ui.progressBar.setValue(100);

					//Wait for the socket to accept a connection. This thread halts here until a new user approaches us.
					util.Log("Awaiting connections on Port " + serverSocket.getLocalPort() + "...");
					socket = serverSocket.accept();

					ui.SetStatus(UI.ServerStatus.Busy);
					UpdateUI(); 

					util.Log("Connection requested from " + socket.getRemoteSocketAddress() + "... Delegating thread.");

					/*
					 * Here, we iterate over the network threads and look for a null slot. This will only be the case the first time
					 * we use a slot, since after it terminates it won't return to null. For this reason, the variable flag "active" in
					 * the child class NetworkSocket will be toggled to false to indicate to us that it's time to kill this one off.
					 */
					boolean success = false;
					for (int i = 0; i < networkThreads.length; i++) {
						boolean isClear = false;

						if (networkThreads[i] != null) {
							//Thread is not null, so lets see if we can clear it.
							if (!networkSockets[i].GetActive()) {
								/*
								 * GC - Garbage Collection
								 * 
								 * Garbage Collection involves Java searching memory spaces for defunct/unused data and destroying it
								 * to as to minimize our memory footprint. While we can call GC via System.gc(), Java also does it 
								 * when it believes a large amount of data is unused.  
								 */

								util.Log("Clearing a previous thread (this might cause us to GC in a bit)...");
								networkThreads[i].interrupt();
								networkThreads[i] = null;
								networkSockets[i] = null;
								isClear = true;

								ui.accessCount++;

								//We'll call GC on our own just in case
								ui.numGCs++;
								System.gc();
							}
						}else {
							//Fantastic, thread is already null!
							isClear = true;
						}

						if (isClear) {
							//Current thread is null, so lets go ahead and use it!
							util.Log("Using thread " + i + ". Instantiating the thread.");
							String randomName = ThreadNames.values()[rand.nextInt(ThreadNames.values().length)].toString() + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9);
							networkThreads[i] = new Thread(networkSockets[i] = new NetworkSocket(socket, randomName));
							networkThreads[i].start();
							success = true;

							ui.accessCount++;

							//We got what we came for, lets break.
							break;
						}
					}
					if (!success) {
						util.Log("CRITICAL ERROR. I DON'T HAVE ANYWHERE TO PUT MY NEXT CLIENT.");
					}
					ui.accessCount++;
					UpdateUI();

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					serverSocket.close();
				} catch (Exception e) {

				}
			}
		}

		/**
		 * Fetches the current client from the thread.
		 * @return The client as a Socket.
		 */
		public Socket GetCurrentSocket() {
			return socket;
		}
	}

	public class NetworkSocket implements Runnable {
		//Reference to Superclass for ease-of-access
		Backend instance = Backend.this;
		Utils util = instance.properties.util;
		Init.PropertyMaster propMaster = instance.properties;
		UI ui;

		//Data
		private String name;
		private Socket server;

		private boolean active;

		private String remoteName;

		//Client
		private String username;
		private String password;

		public NetworkSocket(Socket socket, String threadName) {
			ui = instance.properties.ui;
			active = true;

			name = threadName;
			server = socket;
		}

		public boolean GetActive() {
			return active;
		}

		@Override
		public void run() {
			while(active) {
				try {
					ui.accessCount++;
					ui.progressBar.setValue(10);

					DataInputStream in = new DataInputStream(server.getInputStream());
					DataOutputStream out = new DataOutputStream(server.getOutputStream());

					util.Log("You've awoken " + name + " on port " + server.getLocalPort() + ".");
					util.Log("[" + name + "] Authorizing " + server.getRemoteSocketAddress() + "...");

					/*
					 * What is a Handshake? A handshake is a sequence exchanged by Server-Client systems to prove to each other first that
					 * the connection is being made by our program (not some other program), and also that the client is both legitimate
					 * (not security compromised), and also up-to-date. Review the details of our secret handshake below.
					 * 
					 * Client Sends: Version, Computer Name, IP-Address
					 * Server Checks: Version MATCH, IP-Address MATCH, Computer Name store.
					 * Server Sends: Random sequence.
					 * Client Sends: Specific characters from the sequence. 
					 * Server Checks: Sequence MATCH
					 * Server Sends: Welcome!
					 * 
					 * -> Repeat
					 * 
					 * Server Waits for Client Request
					 * Client: Request
					 * 
					 * -> Repeat
					 * 
					 * If the target fails out, we will close this connection and interpret the connection as malicious.
					 */

					//Should be of format: %VERSION%NAME%IP%
					String opening = in.readUTF();
					String[] entries = opening.split("%");

					//Decode of Message 1
					String remoteVersion = entries[0];
					String remoteName = entries[1];
					String remoteIP = entries[2];

					ui.progressBar.setValue(20);

					if (remoteVersion.equals(String.valueOf(propMaster.BACKEND_VERSION))) {
						ui.progressBar.setValue(25);
						if (remoteIP.equals(server.getRemoteSocketAddress().toString())) {
							ui.progressBar.setValue(30);
							this.remoteName = remoteName;

							/*
							 * We are expecting the characters at the indices of 57,72,15,66,49 to be returned to us.
							 * This is Euler's number (6.5772156649). We can, of course, change this to something else if we feel
							 * the security has been compromised. It is important that clients of this version
							 * and servers of this version both expect and send the same data.
							 * 
							 * Any amateur can crack this code with a few minutes and a proper technique. We can
							 * introduce multiple-stage versions, but complexity here is not necessary since Mesa
							 * is not the CIA.
							 */

							Random rand = new Random();

							String message = "CALLSIGN ";
							for (int i = 0; i < 128; i++) {
								message += rand.nextInt(10);
							}
							out.writeUTF(message);

							ui.progressBar.setValue(45);

							/*
							 * Now, once the next line passes, this means the client has responded to us. We then
							 * need to decode the text to make sure it's compliant.
							 */

							String expectedResponse = "" + message.charAt(57) + message.charAt(72) + message.charAt(15) + message.charAt(66) + message.charAt(49);
							String actualResponse = in.readUTF();

							if (actualResponse.equals(expectedResponse)) {
								ui.progressBar.setValue(65);
								/*
								 * Okay, we got it. The client is legitimate and up-to-date with this server version. Let's go ahead and let them know.
								 */
								out.writeUTF("$IDENTIFY");	//This command specifically forces the client to respond with the User's information

								/*
								 * We expect to read something back like this
								 * $IDENTIFY HACKJUNKY PASSWORD
								 * 
								 * So, let's break it down.
								 */

								boolean verified = false;

								while (!verified) {		//We want to repeat this loop as many times as is necessary
									String identification = in.readUTF();
									if (identification.equals("$ABORT")) {
										break;
									}
									String[] split = GetParameters(identification);
									if (identification.startsWith("$IDENTIFY") && split.length == 2) {
										username = split[0];
										password = split[1];
									}

									boolean isValid = dbMan.ParseUser(username.toLowerCase(), password);
									//We have the user's information, let's check with the database...
									if (isValid) {
										verified = true;
										out.writeUTF("$VALID");
										util.Log("Remote Client (" + username + ") has verified their identity. Welcome, " + username + ".");
									}else {
										util.Log("Remote Client failed to propery verify themselves. Retrying query...");
										out.writeUTF("$INVALID");
									}
								}

								if (verified) {			//Given that the loop above terminated, and the user info was valid, we are now in the main loop.
									boolean done = false;

									while (!done) {
										ui.progressBar.setValue(99);
										String request = in.readUTF();
										if (request.equals("$NOREQUEST")) {
											//No request at this time, reparse.
										}else {
											if (request.startsWith("$MSG")) {					//We can use MSG to send information from the client directly to the console, here. This is for debugging.
												String msg = request.substring("$MSG ".length(), request.length());
												util.Log("[MSG] " + msg);
											}else {
												if (request.startsWith("$GET")) {
													//$GET PILOTS,AIRCRAFTS,DATES,TRAINING,FLIGHT,MAINTINENCE,
													//$GET RANK JABOULHOSN
													
													
													String cmd = request.substring("$GET ".length(), request.length());
													if (cmd.startsWith("RANK")) {
														String user = cmd.substring("RANK ".length(), cmd.length());
														//util.Log("RECV remote request for " + user + "'s rank...");
														String rank = dbMan.RequestRank(user.toLowerCase());
														if (rank != null) {
															//util.Log("Transmitting rank...");
															out.writeUTF(rank);
														}else {
															//util.Log("Could not locate user by ID, sending error...");
															out.writeUTF("$ERROR");
														}
													}else {
														//util.Log("RECV remote request for datalist " + cmd + "...");
														String[] respArr = dbMan.RequestField(cmd);	//We have an array from the DB, let's stringify this.
														String resp = "";
														for (int i = 0; i < respArr.length; i++) {
															resp += respArr[i] + "$";
														}
														if (resp.length() > 0) {
															resp = resp.substring(0, resp.length() - 1);
														}
														//We now have a string separated like this Jad;Andy;Jacqueline;Tebiao, so lets send it.
														//util.Log("Transmitting " + respArr.length + " bytes to client...");
														out.writeUTF("$LIST " + resp);
													}
												}else if (request.startsWith("$SET")) {
													
												}else if (request.equals("$ABORT")) {
													done = true;
												}
											}
										}
										ui.progressBar.setValue(100);
									}
								}else {
									util.Log("Remote Client failed to identify themselves. This is not malicious, they simply failed to log in.");
								}
							}else {
								out.writeUTF("$ERROR, HANDSHAKE");
								util.Log("MISMATCH! Client failed to provide proper handshake! Closing the Server!");
							}
						}else {
							out.writeUTF("$ERROR, IP");
							util.Log("MISMATCH! Client says their IP is " + remoteIP + " but we see " + server.getRemoteSocketAddress() + "! Closing the Server!");
						}
					}else {
						out.writeUTF("$ERROR, VERSION");
						util.Log("MISMATCH! Client is version " + remoteVersion + " but we are version " + propMaster.BACKEND_VERSION + "! Closing the Server!");
						server.close();
						active = false;
					}

					server.close();
					active = false;
				}catch(SocketTimeoutException s) {
					System.out.println("[" + name + "] The socket has timed out and been reset.");
					break;
				}catch(IOException e) {
					util.Log("[" + name + "] has been terminated by the remote client.");
					e.printStackTrace();
					break;
				}
			}
		}

		/**
		 * This takes a network message and returns the parameters. Excludes the command
		 * itself, it is recommended that you assess the actual command with String.startsWith().
		 * 
		 * Example:
		 * 		input = "$COMMAND CAT DOG MOUSE
		 * 		return = Array: "CAT, DOG, MOUSE";
		 * @param input	The input string.
		 * @return An array of the parameters.
		 */
		public String[] GetParameters(String input) {
			String[] split = input.split(" ");
			String[] output = new String[split.length - 1];
			for (int i = 1; i < split.length; i++) {
				//We dont want the item at index 0, since its a $COMMAND
				output[i - 1] = split[i];
			}
			return output;
		}
	}
}

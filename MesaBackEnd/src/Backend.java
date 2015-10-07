import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.Timer;

/**
 * The Backend provides multi-threaded networking with support for complex object serialization. The UI runs on a separate
 * thread, and will occasionally access flags in this class to check it's status for visual reporting.
 * @author hackjunky
 *
 */
public class Backend {
	//We can pass all user configuration parameters around in a PropertyMaster, which is a nested class in Init.
	Init.PropertyMaster properties;
	
	//Utility class is passed around by pointer to the entire program, for the purpose of central logging.
	Utils util;
	
	//The NetworkMaster is a storage system for the active network connections. SEE CLASS.
	NetworkMaster netMaster;
	//The master thread runs the network master, and keeps it asynchronous from the main execution.
	Thread netMasterThread;
	
	//DatabaseManager reference, we will use this class to interpret and render serializable data. SEE CLASS.
	DatabaseManager dbMan;
	//Same as above, but for the curriculum.
	CurriculumDatabase cMan;
	
	//The serialized object, passed between the server and all clients. SEE CLASS.
	Payload payload;
	
	//Main timer keeps track of the server's hearbeat, memory usage, and updates the user interface via flags.
	Timer eventTicker;
	//SEE CLASS.
	EventHandler eventHandler;

	//We assign a thread an arbitrary name. I got clever here.
	enum ThreadNames {
		Bashful, Doc, Dopey, Grumpy, Happy, Sleepy, Sneezy
	}
	
	//Constructor
	public Backend(Init.PropertyMaster prop) {
		//Read in properties pointer from Init.
		properties = prop;
		//Read in utilities pointer from Init.
		util = properties.util;

		util.Log("Backend main program is initializing...");

		//Create a new NetworkMaster
		netMaster = new NetworkMaster();

		//Generate the Payload stub
		payload = new Payload();

		//Initialize the DBManager and point it to the shared resources
		dbMan = new DatabaseManager(util, netMaster.ui, payload);
		
		//Initialize the Curriculum Database, and point it to the shared resources
		cMan = new CurriculumDatabase(util, netMaster.ui, payload);

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

	/**
	 * EventHandler simple controls the UI <-> Async Thread situation. Since, in ADO, we cannot reference other-thread items, 
	 * we can overcome this hurdle by simply setting and reading integer flags in this thread, and having a separate timer on the 
	 * UI side to superimpose the flags onto labels, etc.
	 * @author hackjunky
	 *
	 */
	public class EventHandler implements ActionListener{
		//Let's pass these in, for good measure.
		Backend backend;
		UI ui;
		NetworkMaster netMaster;

		//Timer runs at interval of 100, so we want 1000/100 = 10 tick intervals to maintain per-second operation.
		int second = 10;

		//Constructor
		public EventHandler() {
			backend = Backend.this;
			netMaster = backend.netMaster;
			ui = netMaster.ui;
		}

		//ActionPerformed is called by the Swing Timer class.
		@Override
		public void actionPerformed(ActionEvent e) {
			backend = Backend.this;
			netMaster = backend.netMaster;
			ui = netMaster.ui;

			/*The UI sets a boolean flag, which we read here, and set back to false. This keeps ADO seamless, and avoids
			 * per-thread erroring and error-passing errors. You will see this in many other methods throughout the program.
			 */
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

			/*Here, we check to see if a thread has died, and recycle it's address in the array, to keep the garbage collection
			 * seamless. If we don't do this, we can have up to MAX_USERS threads, all containing junk/useless data, held onto
			 * by a null network class.
			 */	
			for (int i = 0; i < netMaster.networkThreads.length; i++) {
				if (netMaster.networkThreads[i] != null) {
					if (!netMaster.networkThreads[i].isAlive()) {
						netMaster.networkThreads[i] = null;
						netMaster.networkSockets[i] = null;
					}
				}
			}

			//We've done all our UI flagging, tell the UI it's time to update.
			netMaster.UpdateUI();

			//We call this to reload the payload class data for its sync.
			netMaster.UpdatePayload();

			//Remember, we onlu want to tick every second, so since we're at 100 ticks, then 1000/100 = 10 ops/tick.
			if (second == 0) {
				second = 10;

				ui.opsCount = ui.accessCount;
				ui.accessCount = 0;
			}else {
				second--;
			}
		}
	}

	
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
			//We can use this variable read in directly by INIT from the XML configuration file.
			networkThreads = new Thread[properties.NETWORK_MAX_CONNECTIONS];
			networkSockets = new NetworkSocket[properties.NETWORK_MAX_CONNECTIONS];
		}

		
		public void UpdateUI() {
			/*Update the UI. Let's read the thread count, client count, and handle count. Remember that since the other 
			 * ADO thread is continuously accessing this data at approx. 100 times/second, we cannot clear the variable 
			 * until we're ready to write to it. Therefore, we write to these temporary integers, and then update the UI.
			 */
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
					/*BUG: Sometimes if we re-activate the listener and the port is already in use by the program, the system
					 * will reject the assignment. I've added this message because I'm too lazy to fix it.
					 */
					util.Log("Ignore this message if you've just reactivated the listener.");
				}

				Random rand = new Random();

				//This will execute eternally until the program terminates. It's in a separate thread for this reason.
				while (true) {
					//We will repeatedly update the UI while this loop is running indefinitely.
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
								 * so as to minimize our memory footprint. While we can call GC via System.gc(), Java also does it 
								 * when it believes a large amount of data is unused.  
								 * 
								 * Expected behavior: The program will start at 1MB and gradually climb to ~25MB before returning to 0MB.
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
						//We should never ever see this message on the UI.
					}
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
		 * Updating the payload involves converting the database XML output into a usable serializable class called
		 * the Payload. It is not uncommon to reference to the networked class as a Payload.
		 */
		public void UpdatePayload() {
			/*
			 * First, set the models in the payload using the DBMan GetAllOfType request.
			 */
			payload.setDateModel(ConvertArrayToModel(dbMan.GetAllOfType(DatabaseManager.FieldType.DATES)));
			payload.setPilotModel(ConvertArrayToModel(dbMan.GetAllOfType(DatabaseManager.FieldType.PILOTS)));
			payload.setNameModel(ConvertArrayToModel(dbMan.GetAllOfType(DatabaseManager.FieldType.AIRCRAFTS)));

			//Next, populate the ranklist.
			String[] ranklist = dbMan.RequestRankList();
			DefaultListModel<String> userModel = new DefaultListModel<String>();
			DefaultListModel<String> rankModel = new DefaultListModel<String>();
			if (ranklist != null) {
				for (String s : ranklist) {
					String username = s.split(";")[0];
					String rank = s.split(";")[1];
					userModel.addElement(username);
					rankModel.addElement(rank);
				}
				payload.setUserModel(userModel);
				payload.setRankModel(rankModel);
			}

			//User count.
			int count = dbMan.GetUserCount();
			if (count > 0) {
				payload.setNumUsers(count);
			}

			//Arbitrary statistics we serialize because we can.
			payload.setNumOnline(ui.numClients);
			if (netMaster != null && netMaster.socket != null) {
				payload.setNetIP(netMaster.socket.getInetAddress().toString());
			}
			payload.setNumOverhead(ui.opsCount);
			payload.setUptime(ui.lblUptime.getText());
			payload.setMemUsage(ui.lblMemUsage.getText());

			//We inform DBMan that the payload is ready to be sent.
			dbMan.SendPayloadStacks();
		}

		/**
		 * Converts an Array into a DefaultListModel (the format used by JLists)
		 * @param in The array to convert.
		 * @return A DefaultListModel with in[] in it.
		 */
		public DefaultListModel<String> ConvertArrayToModel(String[] in) {
			if (in != null) {
				DefaultListModel<String> model = new DefaultListModel<String>();
				for (String s : in) {
					boolean found = false;
					for (int i = 0; i < model.size(); i++) {
						if (s.equals(model.elementAt(i))) {
							found = true;
						}
					}
					if (!found) {
						model.addElement(s);
					}
				}
				return model;
			}
			return new DefaultListModel<String>();
		}

		/**
		 * Returns the socket that the server wants to use next, so we can assign to it.
		 * @return A socket that wants a user and is next in line.
		 */
		public Socket GetCurrentSocket() {
			return socket;
		}
	}

	/**
	 * NetworkSocket provides socket support for each individual client. In example, creating 12 of these creates 12 possible 
	 * client sockets to write to. The clients will connect to each one individually, but launching them all together would 
	 * cause a port binding error. That's why we store them in the master class, and keep one open, just in case someone connects.
	 * 
	 * When someone disconnects, the loop in this class terminates, and the class marks itself as waiting for Garbage Collection.
	 * The system should collect it soon.
	 * @author hackjunky
	 *
	 */
	public class NetworkSocket implements Runnable {
		//Reference to Superclass for ease-of-access
		Backend instance = Backend.this;
		Utils util = instance.properties.util;
		Init.PropertyMaster propMaster = instance.properties;
		UI ui;

		//Server Data
		private String name;
		private Socket server;
		private boolean active;
		private String remoteName;

		//Client
		private String username;
		private String password;

		//Rank
		private String rank = "user";

		/**
		 * Constructor for NetworkSocket.
		 * @param socket Socket to bind to.
		 * @param threadName Name for the thread, so we can represent it literally.
		 */
		public NetworkSocket(Socket socket, String threadName) {
			ui = instance.properties.ui;
			active = true;

			name = threadName;
			server = socket;
		}

		/**
		 * Are we still alive and connected to a user?
		 * @return True if we are connected, and false if we're ready for GC.
		 */
		public boolean GetActive() {
			return active;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			/**
			 * This method executes eternally via the Thread that controls it. It is called only once, however, you will notice
			 * I've added a while(true), which would normally hang a program. However, since we have our own thread, and this is
			 * normal practice for network threading, everything is okay. Don't freak out :)
			 */
			DataInputStream in;
			DataOutputStream out;
			ObjectOutputStream  oos;
			while(active) {
				//Start here
				try {
					//Show that we're starting our handshake.
					ui.accessCount++;
					ui.progressBar.setValue(10);

					oos = new ObjectOutputStream(server.getOutputStream());
					out = new DataOutputStream(server.getOutputStream());

					in = new DataInputStream(server.getInputStream());

					//Let the user know we're doing it.
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

					//Check to see if the versions match.
					if (remoteVersion.equals(String.valueOf(propMaster.BACKEND_VERSION))) {
						ui.progressBar.setValue(25);
						//Check to see if the user's IP matches what they registered for the socket. 
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

							//Designate a callsign to each message, so we don't lose them in the network trace.
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

									/*
									 * Main loop, the user is authenticated at this point.
									 */
									while (!done) {
										try {
											ui.numOverhead++;
											dbMan.Refresh();

											ui.progressBar.setValue(99);
											rank = dbMan.RequestRank(username.toLowerCase());

											String request = in.readUTF();
											if (request.equals("$NOREQUEST")) {
												/*
												 * The below code handles primitive commands for data manipulations, so lets serialize the class object on our downtime.
												 * Bear in mind that since our client is read only, and we handle our transforms above, we do not need to read
												 * in from the client. This is a one way conversation.
												 */
												oos.reset();
												oos.writeObject(payload);	
											}else {
												if (request.startsWith("$MSG")) {					//We can use MSG to send information from the client directly to the console, here. This is for debugging.
													String msg = request.substring("$MSG ".length(), request.length());
													util.Log("[MSG] " + msg);
												}else {
													if (request.startsWith("$GET")) {				//We serialize our major data classes, but we can send and recv commands like this.
														String cmd = request.substring("$GET ".length(), request.length());
														if (cmd.startsWith("RANK ")) {
															String user = cmd.substring("RANK ".length(), cmd.length());
															//util.Log("RECV remote request for " + user + "'s rank...");
															String rank = dbMan.RequestRank(user.toLowerCase());
															if (rank != null) {
																//util.Log("Transmitting rank...");
																out.writeUTF("$RANK " + user.toLowerCase() + ";" + rank);
															}else {
																//util.Log("Could not locate user by ID, sending error...");
																out.writeUTF("$ERROR");
															}
														}
													}else if (request.equals("$ABORT")) {
														done = true;
													}

													//In this case, we've restricted certain commands to superadmins
													if (rank != null) {
														if (rank.equals("admin") || rank.equals("superadmin")) {
															if (request.startsWith("$CREATE")) {
																//Create command, allows for anything from a user to a log or entry.
																String cmd = request.substring("$CREATE ".length(), request.length());
																if (cmd.startsWith("USER") && rank.equals("superadmin")) {
																	String start = request.substring("$CREATE USER ".length(), request.length());
																	String user = start.split(" ")[0].toLowerCase();
																	String password = start.split(" ")[1];
																	String localrank = start.split(" ")[2].toLowerCase();

																	if (user.length() > 0) {
																		if (password.length() > 3) {
																			if (localrank.equals("user") || localrank.equals("admin") || localrank.equals("superadmin")) {
																				String resp = dbMan.CreateUser(username, user, password, localrank);
																				if (resp.length() > 0) {
																					out.writeUTF("$FAILURE " + resp);
																				}else {
																					out.writeUTF("$SUCCESS");
																				}
																			}else {
																				out.writeUTF("$FAILURE '" + localrank + "' is not a valid rank. Available ranks: user, admin, superadmin.");
																			}
																		}else {
																			out.writeUTF("$FAILURE Passcode must exceed 3 characters in length.");
																		}
																	}else {
																		out.writeUTF("$FAILURE Username cannot be 0 characters in length.");
																	}
																}else if (cmd.startsWith("ENTRY")) {
																	//$CREATE ENTRY PILOT AIRCRAFT DATE TYPE NOTES
																	try {
																		String split[] = request.substring("$CREATE ENTRY ".length(), request.length()).split(" ");
																		String pilot = split[0].toLowerCase();
																		String aircraft = split[2].toLowerCase();
																		String date = split[1].toLowerCase();
																		String type = split[3].toLowerCase();
																		String notes = "";
																		for (int i = 4; i < split.length; i++) {
																			notes += split[i] + " ";
																		}

																		//util.Log("Pilot: " + pilot + ". Aircraft: " + aircraft + ". Date: " + date + ". Type: " + type + ". Notes: " + notes);

																		Payload.Entry entry = payload.CreateBlankEntry(pilot, aircraft, date);
																		if (type.equals("training")) {
																			entry.setTrainingData(notes);
																		}else if (type.equals("Repair")) {
																			entry.setRepairData(notes);
																		}else if (type.equals("flight")) {
																			entry.setFlightData(notes);
																		}

																		String resp = dbMan.CreateEntry(username, entry);

																		if (resp.length() == 0) {
																			out.writeUTF("$SUCCESS");
																		}else {
																			out.writeUTF("$FAILURE " + resp);
																		}
																	}catch (Exception e) {
																		e.printStackTrace();
																		out.writeUTF("$FAILURE The operation did not complete with the given parameters.");
																	}
																}else if (cmd.startsWith("LOG")) {
																	
																}
															}else if (request.startsWith("$RECOVER") && rank.equals("superadmin")) {
																out.writeUTF("$RECOVERY " + dbMan.GetPassword(request.substring("$RECOVER ".length(), request.length())));
															}else if (request.startsWith("$CHANGE ")) {
																String cmd = request.substring("$CHANGE ".length(), request.length());
																if (cmd.startsWith("PASSWORD")) {
																	String username = cmd.split(" ")[1];
																	String oldpass = cmd.split(" ")[2];
																	String newpass = cmd.split(" ")[3];
																	dbMan.ChangePassword(username, oldpass, newpass);
																}
															}else if (request.startsWith("$REMOVE") && rank.equals("superadmin")) {
																String cmd = request.substring("$REMOVE ".length(), request.length());
																if (cmd.startsWith("USER") && rank.equals("superadmin")) {
																	String user = request.substring("$DELETE USER ".length(), request.length());
																	if (username.equals(user)) {
																		out.writeUTF("$FAILURE You cannot delete the account you're currently using.");
																	}else {
																		boolean pass = dbMan.DeleteUser(user);
																		if (!pass) {
																			out.writeUTF("$FAILURE A user doesn't exist by the username '" + user + "'.");
																		}else {
																			out.writeUTF("$SUCCESS");
																		}
																	}
																}else if (cmd.startsWith("ENTRY")) {
																	//$REMOVE ENTRY TYPE STRING
																	String split[] = request.substring("$REMOVE ENTRY ".length(), request.length()).split(" ");
																	String type = split[0].toLowerCase();
																	String value = split[1].toLowerCase();
																	String resp = dbMan.RemoveEntryByType(username, type, value);
																	
																	if (resp.length() == 0) {
																		out.writeUTF("$SUCCESS");
																	}else {
																		out.writeUTF("$FAILURE " + resp);
																	}
																}else if (cmd.startsWith("LOG")) {
																	
																}
															}else if (request.startsWith("$PROMOTE") && rank.equals("superadmin")) {
																String user = request.substring("$PROMOTE ".length(), request.length());
																dbMan.PromoteUser(user);
																out.writeUTF("$SUCCESS");
															}else if (request.startsWith("$DEMOTE") && rank.equals("superadmin")) {
																String user = request.substring("$DEMOTE ".length(), request.length());
																dbMan.DemoteUser(user);
																out.writeUTF("$SUCCESS");
															}
														}else {
															out.writeUTF("$PERMS");
														}
													}else {
														out.writeUTF("$ERROR");
													}
												}
											}

											ui.progressBar.setValue(100);
											ui.numOverhead--;
										}catch (Exception e) {
											//e.printStackTrace();
											util.Log("Forcing premature termination of the thread.");
											break;
										}
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
						out.writeUTF("$ERROR, VERSION: " + propMaster.BACKEND_UPDATE_TARGET);
						util.Log("MISMATCH! Client is version " + remoteVersion + " but we are version " + propMaster.BACKEND_VERSION + "! Closing the Server!");
					}
				}catch(SocketTimeoutException s) {
					System.out.println("[" + name + "] The socket has timed out and been reset.");
					break;
				}catch(IOException e) {
					util.Log("[" + name + "] has been terminated by the remote client.");
					//e.printStackTrace();
					break;
				}

				try {
					oos.close();
					in.close();
					out.close();
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				//We've died, which means the client is disconnected,closed,whatever. Mark us ready for GC, because we want to clean this mess up.
				ui.numOverhead--;
				active = false;
			}
		}

		/**
		 * Helper method that takes a input string, separated by spaces, and returns an array of commands.
		 * @param input COMMAND PARAM PARAM PARAM
		 * @return [PARAM][PARAM][PARAM]
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

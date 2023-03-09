package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Chat {
	public static void main(String[] arg) {
		//using args as an input to program
		if (arg != null && arg.length > 0) { //if args is something....
			try {
				int listener = Integer.parseInt(arg[0]);
				Chat chatProgram = new Chat(listener); //create a new Chat instance with given port
				chatProgram.startChat(); //start the chat program
			} catch (NumberFormatException e) {
				System.out.println("Not given a port number.");
			}
		} else{
			System.out.println("Args are wrong..'");
		}
	}
	private int myPort;
	private InetAddress myIP;
	private int clientCounter;
	private Server msgReceiver; 
	private Map<Integer, Destination> destinationsHosts = new TreeMap<>();
	
	//required methods
	private Chat(int myPort) {
		this.myPort = myPort;
	}
	
	private String getMyIp() {
		return myIP.getHostAddress();
	}
	
	private int getMyPort() {
		return myPort;
	}
	
    public static void help() {
        System.out.println("""
				help: Display available user command manual
				myip: Display IP address
				myport: Display the port that is listening for incoming connections
				connect <destination> <port no>: Establishes connection to specified client
				list: Display a numbered list of all the connections
				terminate <connection  id.>: Terminate specified connection in list
				send <connection id.> <message>: Send message to specified client
				exit: Close all connections and terminate the chat""");
    }

	private void send(String[] commandArg) {
		if (commandArg.length > 2) { //must be more than 2 commands in sendMessage..send destination message
	        try {
	            int id = Integer.parseInt(commandArg[1]);
	            Destination destinationHost = destinationsHosts.get(id); //get ip and port of destination..
	            if (destinationHost != null) { //destination host must be something to send msg...
	            	StringBuilder message = new StringBuilder();
	            	
	            	//note commandArg[0] is our initial port, commandArg[1] is "send" in this case
	            	for (int i = 2 ; i < commandArg.length ; i++) { //have to go through commandArg array 0 and 1
	            		message.append(commandArg[i]); //then just add message at the end
	            		message.append(" ");
	            	}
	            	
	            	destinationHost.sendMessage(message.toString());
	            	System.out.println("Message send to connection id " + id);
	            } else
	            	System.out.println("Connect failed or does not exist. Check parameters.");

	        } catch(NumberFormatException ne){
	        	System.out.println("Connection id does not exist. Type 'list' to check for valid id");
	        }
		} else
			System.out.println("Invalid command format, Kindly follow: send <connection id> <message>");
	}

    private synchronized void connect(String[] commandArg) {
		if (commandArg != null && commandArg.length == 3) { //inputting ip and port manually...
	        try {
	            /*note: array commandArgs[0] is our initial port*/
	        	InetAddress remoteAddress = InetAddress.getByName(commandArg[1]);
	            int remotePort = Integer.parseInt(commandArg[2]);
				System.out.println("Connecting to " + remoteAddress.getHostAddress() + " on port " + remotePort);
				Destination destinationHost = new Destination(remoteAddress,remotePort);

				if (destinationHost.initConnections()) { //if connection successful....
					// client counter for list. first connection assigned #1, second connection #2...etc
					// if self connection, display warning message
					if (remotePort == myPort)
						System.out.println("Warning: You are connecting yourself, please try again!");
					else {
						clientCounter++;
						destinationsHosts.put(clientCounter, destinationHost);
						System.out.println("Connected successfully!\nConnection id " + clientCounter);
					}
				} else
					System.out.println("Unable to establish connection, try again");

	        } catch (NumberFormatException ne) {
	        	System.out.println("Invalid Remote Host Port, unable to connect");
	        } catch (UnknownHostException e) {
	        	System.out.println("Invalid Remote Host Address, unable to connect");
	        }
		} else
			//trying to connect with no/wrong port
			System.out.println("Invalid command format, Kindly follow: connect <destination> <port no>");
	}

    private synchronized void list() {
		System.out.println("Id:\tIP Address\tPort");
		if (destinationsHosts.isEmpty()) {
			System.out.println("Not connected to anyone");
		} else {
			//using map, where an id # associated with ip and port....
			for (Integer id : destinationsHosts.keySet()) {
				Destination destinationHost = destinationsHosts.get(id);
				System.out.println(id+"\t"+destinationHost.toString());
			}
		}
		System.out.println();
    }

	private synchronized void terminate(String[] commandArg) {
		if (commandArg != null && commandArg.length == 2) {
			System.out.println("Terminating connection id " + commandArg[1]);
			try {
				int id = Integer.parseInt(commandArg[1]);
				//checking if the destination map even contains the given id
				if (!destinationsHosts.containsKey(id)) {
					System.out.println("Invalid connection ID, unable to terminate, try list");
					return;
				}
	
				Destination destinationHost = destinationsHosts.get(id);
				boolean closed = !destinationHost.closeConnection();
				if (closed) {
					System.out.println("Connection id "+ id + " was terminated");
					destinationsHosts.remove(id);
				}
			} catch (NumberFormatException e) {
				System.out.println("Connection id does not exist, try again");
			}
		} else
			System.out.println("Invalid command format, Kindly follow: terminate <connection id>");
	}
    
	private void startChat() {
		//begin program by prompting for commands...
		try (Scanner scanner = new Scanner(System.in)) {
			myIP = InetAddress.getLocalHost();
			msgReceiver = new Server();
			new Thread(msgReceiver).start();
			while (true) {
				System.out.println("Enter the command, type help for guidelines");
				String command = scanner.nextLine();
				//if there is something after args..
				if (command != null && command.trim().length() > 0) {
					command = command.trim();

					if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("/h") || command.equalsIgnoreCase("-h")) {
						help();
					} else if (command.equalsIgnoreCase("myip")) {
						System.out.println(getMyIp());
					} else if (command.equalsIgnoreCase("myport")) {
						System.out.println(getMyPort());
					} else if (command.startsWith("connect")) {
						String[] commandArg = command.split("\\s+");
						connect(commandArg);
					} else if (command.equalsIgnoreCase("list")) {
						list();
					} else if (command.startsWith("terminate")) {
						String[] args = command.split("\\s+");
						terminate(args);
					} else if (command.startsWith("send")) {
						String[] commandArg = command.split("\\s+");
						send(commandArg);
					} else if (command.startsWith("exit")) {
						System.out.println("Closing sockets");
						System.out.println("Chat program closing");
						closeAll();
						System.exit(0);
					} else {
						System.out.println("Invalid parameter. See readme");
						System.out.println();
					}
				} //end if args not empty
				else {
					System.out.println("Invalid parameter. See readme");
					System.out.println();
				}
			} //end while loop
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
	}//end start chat

    private void closeAll() {
        for (Integer id : destinationsHosts.keySet()) {
            Destination destinationHost = destinationsHosts.get(id);
            destinationHost.closeConnection();
        }
        destinationsHosts.clear();
        msgReceiver.stopChat();
    }

	private class Clients implements Runnable {
		private BufferedReader input;
		private Socket clientSocket;
		private boolean checkStop = false;

		//client class constructed with input from reader and IP
		private Clients(BufferedReader input, Socket ipAddress) {
			this.input = input;
			this.clientSocket = ipAddress;
		}

		@Override
		public void run() {
			while (!clientSocket.isClosed() && !this.checkStop) {
				String st;
				try {
					st = input.readLine(); //read input from terminal...
					if (st == null) {
						stop();    //if there is no input, then stop
						System.out.println("Connection was terminated by " + clientSocket.getInetAddress().getHostAddress() + " port " + clientSocket.getPort());
						return;
					}
					System.out.println("Message received from " + clientSocket.getInetAddress().getHostAddress() + " port " + clientSocket.getPort() + "\nMessage: " + st);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//stopping a client instance
		public void stop() {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			checkStop = true;
			Thread.currentThread().interrupt();
		}
	}
                       
	private class Server implements Runnable{
		private boolean isStopped ; //boolean to check if the server is stopped
		private List<Clients> clientList = new ArrayList<>(); //list of Clients in an array

		@Override
		public void run() {
			ServerSocket s;
			try {
				s = new ServerSocket(getMyPort()); //make new socket with given port

				while(!isStopped) { //while the server is running...
					try { //try to connect to the server..
						Socket socket = s.accept();
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						Clients clients = new Clients(in, socket); //also, add this client to the client array
						new Thread(clients).start();
						clientList.add(clients);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		public void stopChat(){ //is stopped set to true, therefore server is stopped
			isStopped = true;
			for (Clients clients : clientList) {
				clients.stop();
			}
			Thread.currentThread().interrupt();
		}
	}
}


//destination class for handling connections
class Destination {
	private InetAddress remoteIP;
	private int remotePort;
	private Socket connection;
	private PrintWriter outgoing;
	private boolean isConnected;
//	private static int port;
	//creating destination object with an IP and a port...
	public Destination(InetAddress remoteIP, int remotePort) {
		this.remoteIP = remoteIP;
        this.remotePort = remotePort;
    }

    //making a connection with ip and port...
    public boolean initConnections() {
        try {
            this.connection = new Socket(remoteIP, remotePort); //making a connection..
            //saving the message we want to send to OUT..
            this.outgoing = new PrintWriter(connection.getOutputStream(), true);
            isConnected = true; //if connection works then return TRUE
        } catch (IOException e) {
			e.printStackTrace();
        }
        return isConnected;
    }

    //will only print a message saved in OUTGOING if successfully connected to a server...
    public void sendMessage(String message) {
        if(isConnected) {
            outgoing.println(message);
        }
    }

	//getters and setters
	public InetAddress getRemoteHost() {
		return remoteIP;
	}
	public void setRemoteHost(InetAddress remoteHost) {
		this.remoteIP = remoteHost;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

  	//close the connection by doing this stuff...
    public boolean closeConnection() {
    	if(outgoing != null)
            outgoing.close();
        if(connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return false;
    }
    @Override
    public String toString() {
        return  remoteIP.getHostAddress() + "\t" + remotePort;
    }
}
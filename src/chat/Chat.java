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
	public static void main(String[] arg){
	        //using args as an input to program
		 	if(arg != null && arg.length > 0){ //if args is something....
	            try{
	                int listener = Integer.parseInt(arg[0]);
	                Chat chatProgram = new Chat(listener); //create a new Chat instance with given port
	                chatProgram.startChat(); //start the chat program
	            }
	            catch(NumberFormatException e){
	                System.out.println("Not given a port number.");
	            }
	        }
		 	else{
	            System.out.println("Args are wrong..'");
	        }
	    }
	private int myPort;
	private InetAddress myIP;
	private int clientCounter = 1;
	private Server msgReceiver; 
	private Map<Integer, Destination> destinationsHosts = new TreeMap<>();
	//
	
	//required methods
	private Chat(int myPort) {
		this.myPort = myPort;
	}
	
	private String getmyIp() {
		return myIP.getHostAddress();
	}
	
	private int getmyPort() {
		return myPort;
	}
	
    public static void help() {
        System.out.println("help: Display available user command manual");
        System.out.println("myip: Display IP address");
        System.out.println("myport: Display the port that is listening for incoming connections");
        System.out.println("connect <destination> <port no>: Establishes connection to specified client");
        System.out.println("list: Display a numbered list of all the connections");
        System.out.println("terminate <connection  id.>: Terminate specified connection in list");
        System.out.println("send <connection id.> <message>: Send message to specified client");
        System.out.println("exit: Close all connections and terminate the chat");
    }

    /*public static void getIP() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("The IP address is: " + ip);
    }

    public static void myPort() {
        System.out.println("The chat listening on port: " + port);
    }*/

	private void sendMessage(String[] commandArg) {
		if(commandArg.length > 2){ //must be more than 2 commands in sendMessage..send destination message
	        try{
	            int id = Integer.parseInt(commandArg[1]);
	            Destination destinationHost = destinationsHosts.get(id); //get ip and port of destination..
	            System.out.println("id===="+destinationsHosts.get(id)); //print it
	            if(destinationHost != null){ //destination host must be something to send msg...
	            	StringBuilder message = new StringBuilder();
	            	
	            	//note commandArg[0] is our initial port, commandArg[1] is "send" in this case
	            	for(int i = 2 ; i < commandArg.length ; i++){ //have to go through commandArg array 0 and 1
	            		message.append(commandArg[i]); //then just add message at the end
	            		message.append(" ");
	            	}
	            	
	            	destinationHost.sendMessage(message.toString());
	            	System.out.println("Mesage send successfully");
	            }//end if
	            else
	            	System.out.println("Connect failed or does not exist. Check parameters.");
	            	
	        }//end try
	
	        catch(NumberFormatException ne){
	        	System.out.println("Connection id does not exist. Type 'list' to check for valid id");
	        }
		}//end if >2 commands..
		else{
			System.out.println("Wrong format. Example send 1 hello");
		}
	
	}

    private void connect(String[] commandArg) {
		if(commandArg != null && commandArg.length == 3){ //inputing ip and port manually...
	        try {
	            /*note: array commandArgs[0] is our initial port*/
	        	InetAddress remoteAddress = InetAddress.getByName(commandArg[1]);
	            int remotePort = Integer.parseInt(commandArg[2]);
				System.out.println("Connecting to " + remoteAddress + " on port: " +remotePort);
				Destination destinationHost = new Destination(remoteAddress,remotePort);
				
				if(destinationHost.initConnections()){ //if connection successful....
					/*client counter for list. first connnection assigned #1, second connection #2...etc
					 */
					destinationsHosts.put(clientCounter, destinationHost);
					System.out.println("Connected successfully, client id: " + clientCounter++);
				}
				else {
					System.out.println("Unable to establish connection, try again");
				}
	        }
	        catch(NumberFormatException ne) {
	        	System.out.println("Invalid Remote Host Port, unable to connect");
	        }
	        catch (UnknownHostException e) {
	        	System.out.println("Invalid Remote Host Address, unable to connect");
	        }
		}
		else {
			//trying to connect  with no/wrong port
			System.out.println("Invalid command format , Kindly follow : connect <destination> <port no>");
		}
	}

    private void listDestinations() {
	
	System.out.println("Id:\tIP Address\tPort");
	if(destinationsHosts.isEmpty()){
		System.out.println("Not connected to anyone");
	}
	else{
		//using map, where an id # associated with ip and port....
		for(Integer id : destinationsHosts.keySet()){
			Destination destinationHost = destinationsHosts.get(id);
	        System.out.println(id+"\t"+destinationHost.toString());
	    }
	}
	System.out.println();
    }

	private void terminate(String[] commandArg) {
		if(commandArg != null){
			System.out.println("Terminating connection #: " + commandArg[1]);
			try {
				int id = Integer.parseInt(commandArg[1]);
				//checking if the desination map even contains the given id
				if(destinationsHosts.containsKey(id) == false) {
					System.out.println("Invalid connection ID, unable to terminate, try list");
					return;
				}
	
				Destination destinationHost = destinationsHosts.get(id);
				boolean closed = !destinationHost.closeConnection();
				if(closed){
					System.out.println("ConnectionID: "+ id + " was terminated, but i'll be back!");
					destinationsHosts.remove(id);
				}
	
			}//end try
			catch(NumberFormatException e){
				System.out.println("Must provide connection number");
						}
		}//end if
		else {
			System.out.println("Wrong format. Example terminate 1");
		}	
	}


    private void exit() {
        System.out.println("Exiting chat");
        System.exit(0);
    }
    
	private void startChat() {
	//begin program by prompting for commands...
	Scanner scanner = new Scanner(System.in);
	try{
		 myIP = InetAddress.getLocalHost();
	     msgReceiver = new Server();
	     new Thread(msgReceiver).start();
	     while(true){
	     System.out.print("Enter the command :");
	     String command = scanner.nextLine();
	     //if there is something after args..
		     if(command != null && command.trim().length() > 0){
		    	 command = command.trim();
								
		    	 if(command.equalsIgnoreCase("help") || command.equalsIgnoreCase("/h") || command.equalsIgnoreCase("-h")){
		    		 help();
		    	 }
		    	 else if(command.equalsIgnoreCase("myip")){
		    		 System.out.println(getmyIp());
		    	 }
		    	 else if(command.equalsIgnoreCase("myport")){
		    		 System.out.println(getmyPort());
		    	 }
		    	 else if(command.startsWith("connect")){
		    		 String[] commandArg = command.split("\\s+");
		    		 connect(commandArg);
		    	 }
		    	 else if(command.equalsIgnoreCase("list")){
		    		 listDestinations();
		    	 }
		    	 else if(command.startsWith("terminate")){
		    		 String[] args = command.split("\\s+");
		    		 terminate(args);
		    	 }
		    	 else if(command.startsWith("send")){
		    		 String[] commandArg = command.split("\\s+");
		    		 sendMessage(commandArg);
		    	 }
		    	 else if(command.startsWith("exit")){
		    		 System.out.println("Closing sockets");
		    		 System.out.println("Chat program closing");
		    		 closeAll();
		    		 System.exit(0);
		    	 }
		    	 else{
		    		 System.out.println("Invalid parameter. See readme");
		    		 System.out.println();
		    	 }
		     } //end if args not empty
     	
		    else{
		    	System.out.println("Invalid parameter. See readme");
		    	System.out.println();
	    	}
	    } //end while loop
	} //end try
	catch (UnknownHostException e) {
	    e.printStackTrace();
	}
	
	finally{
	    if(scanner != null)
	        scanner.close();
	    closeAll();
	}
}//end start chat

    private void closeAll(){
        for(Integer id : destinationsHosts.keySet()){
            Destination destinationHost = destinationsHosts.get(id);
            destinationHost.closeConnection();
        }
        destinationsHosts.clear();
        msgReceiver.stopChat();
    }

private class Clients implements Runnable{

private BufferedReader input = null;
private Socket clientSocket = null;
private boolean checkStop = false;
//client class constructed with input from reader and IP
private Clients(BufferedReader input,Socket ipAddress) {
    this.input = input;
    this.clientSocket = ipAddress;
}

@Override
public void run() {
	while(!clientSocket.isClosed() && !this.checkStop)
    {
		String st;
        try {
        	st = input.readLine(); //read input from terminal...
        	if(st == null){
				stop();	//if there is no input, then stop
				System.out.println("Connection was terminated by: " +clientSocket.getInetAddress().getHostAddress() +":"+clientSocket.getPort()+". ");
				return;
						 }

        	System.out.println("Message from " +clientSocket.getInetAddress().getHostAddress() +":"+clientSocket.getPort()+" : "+st);
        	} 
        catch (IOException e) {
        	e.printStackTrace();
        	}
    }
}
//stopping a client instance
public void stop(){

    if(input != null)
        try {
            input.close();
        } catch (IOException e) {
        }

    if(clientSocket != null)
        try {
            clientSocket.close();
        } catch (IOException e) {
        }
    checkStop = true;
    Thread.currentThread().interrupt();
}

}
                       
private class Server implements Runnable{

	BufferedReader in = null;
	Socket socket = null;
	boolean isStopped ; //boolean to check if the server is stopped
	List<Clients> clientList = new ArrayList<Clients>(); //list of Clients in an array


@Override
	public void run() {
    ServerSocket s;
	    try {
	    	s = new ServerSocket(getmyPort()); //make new socket with given port
	    	System.out.println("Server Waiting For The Client");
	    	
	    	while(!isStopped) { //while the server is running...
	    	try { //try to connect to the server..
	    		socket = s.accept();
	    		in = new BufferedReader(new
	            InputStreamReader(socket.getInputStream()));
	    		System.out.println(socket.getInetAddress().getHostAddress() +":"+socket.getPort()+" : client successfully connected.");
	    		
	    		Clients clients = new Clients(in, socket); //also, add this client to the client array
	    		new Thread(clients).start();
	            clientList.add(clients);
	            } 
	    		catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    } 
	    catch (IOException e1) {
	    }

	}

	public void stopChat(){ //is stopped set to true, therefore server is stopped
		isStopped = true;
        for(Clients clients : clientList){
        	clients.stop();
	        }
        Thread.currentThread().interrupt();
    }

}

}



//destination class for handling connections
class Destination{

	private InetAddress remoteIP;
	private int remotePort;
	private Socket connection;
	private PrintWriter outgoing;
	private boolean isConnected;
	//creating destination object with an IP and a port...
	public Destination(InetAddress remoteIP, int remotePort) {
		this.remoteIP = remoteIP;
        this.remotePort = remotePort;
    }

    //making a connection with ip and port...
    public boolean initConnections(){
        try {
            this.connection = new Socket(remoteIP, remotePort); //making a connection..
            //saving the message we want to send to OUT..
            this.outgoing = new PrintWriter(connection.getOutputStream(), true);
            isConnected = true; //if connection works then return TRUE
        } 
        catch (IOException e) {
        }
        return isConnected;
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

    //will only print a message saved in OUTGOING if sucessfully connected to a server...
    public void sendMessage(String message){
        if(isConnected){
            outgoing.println(message);
        }
    }
  //close the connection by doing this stuff...
    public boolean closeConnection(){
    	if(outgoing != null)
            outgoing.close();
        if(connection != null){
            try {
                connection.close();
            } 
            catch (IOException e) {
            }
        }
        isConnected = false;
        return isConnected;
    }
    @Override
    public String toString() {
        return  remoteIP + "\t" + remotePort;
    }
}
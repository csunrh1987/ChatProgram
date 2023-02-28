package chat;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Chat {
	 public static void main(String[] arg){
	        //using args as an input to program
		 	if(arg != null && arg.length > 0){ //if args is something....
	            try{
	                int listener = Integer.parseInt(arg[0]);
	                Chat chatProgram = new Chat(listener); //create a new Chat instance with given port
	                chatProgram.startChat(); //start the chat program
	            }catch(NumberFormatException e){
	                System.out.println("Not given a port number.");
	            }
	        }else{
	            System.out.println("Args are wrong..'");
	        }
	    }
	private int myPort;
	private InetAddress myIP;
	//
	
	//basic methods
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

    public static void myIP() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("The IP address is: " + ip);
    }

    public static void myPort() {
        System.out.println("The chat listening on port: " + port);
    }

    private static void connect() {

    }

    private static void list() {
    }

    private static void terminate() {
    }

    private static void send() {
    }

    private static void exit() {
        System.out.println("Exiting chat");
        System.exit(0);
    }
    
    private void startChat() {
    	//begin program by prompting for commands...
    	Scanner sc = new Scanner(System.in);
    	System.out.print("Enter command..Type help for list of commands");
    	String str = sc.nextLine();
            while (str != null) {
                switch (str) {
                    case "help" -> help();
                    case "myip" -> myIP();
                    case "myport" -> myPort();
                    case "connect" -> connect();
                    case "list" -> list();
                    case "terminate" -> terminate();
                    case "send" -> send();
                    case "exit" -> exit();
                    default -> System.out.println("Invalid user command");
                }
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
        } catch (IOException e) {

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
            } catch (IOException e) {
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
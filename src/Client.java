import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static final String SERVER_IP = "127.0.0.1"; //test
	public static final int SERVER_PORT = 9090;
	
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket(SERVER_IP, SERVER_PORT);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
		
		while(true) {
		System.out.println(">"); //cursor for input commands
		String command = keyboard.readLine();
		outToServer.println(command);
		
		if(command.equals("quit")) break; 
		
		
		String response = input.readLine();
		System.out.println(response);
		}
		
		socket.close();
		System.exit(0);
	
	
	}
}

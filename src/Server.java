import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	private static final int PORT = 9090;
	
	public static void main(String[] args) throws IOException {
	//port listening on
	ServerSocket listener = new ServerSocket(PORT);
	
	System.out.println("Server running...");
	Socket client = listener.accept();
	System.out.println("Connected");
	
	
	PrintWriter outbound = new PrintWriter(client.getOutputStream(), true);
	BufferedReader inbound = new BufferedReader(new InputStreamReader(client.getInputStream()));
	try {
		while (true) {
			String request = inbound.readLine();
			if(request.contains("hello")) {
			outbound.println(testMethod());
			}
			else {
			outbound.println("wat"); //fill me in later
			}
		}
	}
	finally {
	listener.close();
	outbound.close();
	inbound.close();
	}
	
}//end main
	
	public static String testMethod() {
		String tester = "Hello friend";
		return tester;
		}
}
	

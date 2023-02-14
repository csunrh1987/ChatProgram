import java.io.IOException;
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
	outbound.println(new Date().toString());
	System.out.println("Sent");
	client.close();
	listener.close();
	}
	
}

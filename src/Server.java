import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private static final int PORT = 9090;
	private static ArrayList<ClientHandler> clients = new ArrayList<>();
	private static ExecutorService pool = Executors.newFixedThreadPool(4);
	
	
	public static void main(String[] args) throws IOException {
	ServerSocket listener = new ServerSocket(PORT); //server socket
	
	while(true) {
	System.out.println("Server running..."); //waiting for client to connect
	Socket client = listener.accept(); 
	System.out.println("Connected");
	ClientHandler clientThread = new ClientHandler(client);
	clients.add(clientThread);
	
	pool.execute(clientThread);
	
	}
	
}//end main
	
	public static String testMethod() {
		String tester = "Hello friend";
		return tester;
		}
}
	

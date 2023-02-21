import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader inbound;
	private PrintWriter outbound;
	
	public ClientHandler(Socket clientSocket) throws IOException {
		this.client = clientSocket;
		inbound = new BufferedReader(new InputStreamReader(client.getInputStream()));
		outbound = new PrintWriter(client.getOutputStream(),true);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void run() {
		try {
			while (true) {
				String request = inbound.readLine();
				if(request.contains("hello")) {
				outbound.println(Server.testMethod());
				}
				else {
					break;
					//outbound.println("wat"); //fill me in later
				}
			}
		}
		catch (IOException e)  {
		}
		finally {
		try {
			client.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		outbound.close();
		try {
			inbound.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		}	
}

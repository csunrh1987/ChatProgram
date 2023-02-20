import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Chat {
    private Server server;
    private static ArrayList<Client> clientList;
    private static int port;

    public Chat(int port) {
        this.port = port;
        try {
            this.clientList = new ArrayList<>();
            this.server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Chat chat = new Chat(Integer.parseInt(args[0]));
        try {
            chat.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        Scanner sc = new Scanner(System.in);
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
}

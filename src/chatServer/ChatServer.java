package chatServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class ChatServer {

    private static boolean running = true;
    private static final int PORT = 9090;
    public Map<String, ClientHandler> handlers = new HashMap<>();
    private static final Properties properties = Utils.initProperties("server.properties");
    private static final int port = Integer.parseInt(properties.getProperty("port"));
    private static final String ip = properties.getProperty("serverIp");
    private static final String logFile = properties.getProperty("logFile");

    public ChatServer() throws IOException {

    }

    public static boolean isRunning() {
        return running;
    }

    public void online() {
        Set online = handlers.keySet();
        String msg = "ONLINE#" + online.toString();
        for (Map.Entry<String, ClientHandler> entry : handlers.entrySet()) {

            handlers.get(entry.getKey()).out.println(msg);
        }
//        sendToAll("SERVER", msg);
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("Online: " + online.toString()).toUpperCase());

    }

    public void connect(String userID, ClientHandler handler) {
        handlers.put(userID, handler);
        online();
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("User log on: " + userID).toUpperCase());
    }

    public void disconnect(String userID, ClientHandler clientHandler) throws IOException {
        String closeMsg = "User " + userID + " has left";
        sendToAll(userID, closeMsg);
        String close = "CLOSE#";
        handlers.get(userID).out.println(close);
        clientHandler.socket.close();
        handlers.remove(userID);
        online();
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("User logged off" + userID).toUpperCase());
    }

    public void send(String sender, String message, String target) {
        if (handlers.containsKey(target)) {
            String msg = "MESSAGE#" + sender + "#" + message;
            handlers.get(target).out.println(msg);

            Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S " + msg, msg));
        } else {
            send("SERVER", "USER NOT FOUND", sender);
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, String.format("USER NOT FOUND ").toUpperCase());
        }
    }

    public void sendToAll(String sender, String message) {

        String msg = "MESSAGE#" + sender + "#" + message;
        for (Map.Entry<String, ClientHandler> entry : handlers.entrySet()) {

            handlers.get(entry.getKey()).out.println(msg);
        }
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("MESSAGE TO ALL: " + msg).toUpperCase());

    }

    public void stopServer() {
        running = false;
        Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, String.format("SERVER STOPS: ").toUpperCase());
    }

    public void run() throws IOException {

        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(ip, port));

        while (running) {
            Socket socket = server.accept();
            new Thread(new ClientHandler(this, socket)).start();
        }
    }

    public static void main(String[] args) throws IOException {
        Utils.setLogFile(logFile, ChatServer.class.getName());
        ChatServer server = new ChatServer();
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("Server starts listening on port: " + port).toUpperCase());
        try {
            server.run();
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, String.format("Server failed to start").toUpperCase(), ex);

        } finally {
            Utils.closeLogger(ChatServer.class.getName());
        }
    }
}

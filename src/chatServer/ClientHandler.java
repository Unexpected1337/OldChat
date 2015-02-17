package chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//hejsa
public class ClientHandler implements Runnable {

    private final ChatServer server;
    public Socket socket;
    private final BufferedReader in;
    final PrintWriter out;
    final PrintWriter screen;
    public boolean running;
    String userID;
    private boolean connected = false;

    public ClientHandler(ChatServer server, Socket socket) throws IOException {
        this.userID = userID;
        this.server = server;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.screen = new PrintWriter(System.out, true);
        this.running = true;
    }

    @Override
    public void run() {
        do {
            try {
                String message = in.readLine();
                String[] parts = message.split("#");
                switch (parts[0]) {
                    case "CONNECT":
                        if(connected==false){
                        userID = parts[1];
                        server.connect(userID, this);
                        server.sendToAll(userID, "has logged on");
                        connected=true;
                        }
                        else{
                            out.println("MESSAGE#SERVER#YOU ARE ALLREDAY LOGGED ON");
                        }
                        break;

                    case "SEND":
                        if(connected){
                        String recieverIDparts = parts[1];
                        String messageToSend = parts[2];

                        if (recieverIDparts.equals("*")) {
                            server.sendToAll(userID, messageToSend);
                        } else {
                            String[] receivers = recieverIDparts.split(",");
                            for (String receiver : receivers) {

                                server.send(userID, messageToSend, receiver);
                            }
                        }}else{
                            out.println("MESSAGE#SERVER#YOU NEED TO BE CONNECTED");
                        }
                        break;

                    case "CLOSE":
                            if(connected){
                        server.disconnect(userID, this);
                            }else{
                                out.println("MESSAGE#SERVER#YOU NEED TO BED CONNNECTED");
                            }
                        break;

                    default:
                            out.println("MESSAGE#SERVER#SERVER DOES NOT UNDERSTAND COMMAND");
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (running);
    }

}

package chatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientListener implements Runnable {

    private static boolean running = true;
    private final BufferedReader in;
    private final PrintWriter out;
    private Collection<ChatListenerInterFace> listeners;
    private String messageTest;
    private String onlineTest;

    public ClientListener(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(System.out, true);
        listeners = new ArrayList<>();
    }

    public void register(ChatListenerInterFace l) {
        listeners.add(l);
    }

    public void unregister(ChatListenerInterFace l) {
        listeners.remove(l);
    }

    @Override
    public void run() {
        while (running) {
            try {
                String l = in.readLine();
                String[] parts = l.split("#");
                
                switch (parts[0]) {
                    case "MESSAGE":
                        setMessageTest(parts[0] + " " + parts[1] + " " + parts[2]);
                        for (ChatListenerInterFace listener : listeners) {
                            listener.onMessage(parts[1], parts[2]);
                        }
                        break;
                    case "ONLINE":
                        System.out.println("hejfraonline");
                        setOnlineTest(parts[0] + " " +parts[1]);                     
                           String[] online = parts[1].split(",");
                           for(ChatListenerInterFace listener : listeners){
                           listener.onList(online);
                           in.readLine();
                           }
                           break;
                        
                          case "CLOSE":
                            
                            out.close();
                            in.close();
                            running = false;
                            break;
                    default:
                        System.out.println("MESSAGE FROM SERVER IS NOT VALID FOR THIS CLIENT");
                }
                System.out.println(l);
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getMessageTest() {
        return messageTest;
    }

    public void setMessageTest(String testMessage) {
        this.messageTest = testMessage;
    }

    public String getOnlineTest() {
        return onlineTest;
    }

    public void setOnlineTest(String onlineTest) {
        this.onlineTest = onlineTest;
    }

}

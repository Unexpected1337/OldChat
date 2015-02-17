package chatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient {

    private static final boolean running = true;
    private BufferedReader in = null;
    private PrintWriter out = null;
    public String messageTest;
    public String onlineTest;
    Socket socket;
    ClientListener ClientListener;

    // keyboradListener make it so you can chat from netbeans. 
    private class KeyboardListener implements Runnable {

        private final BufferedReader keyboard;
        private final PrintWriter out;

        public KeyboardListener(Socket socket) throws IOException {
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            while (running) {
                try {
                    System.out.print(">> ");
                    String msg = keyboard.readLine();
                    if (!msg.isEmpty()) {
                        out.println(msg);
                        msg = "";
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public ChatClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(socket.getOutputStream(), true);
            ClientListener = new ClientListener(socket);
            new Thread(ClientListener).start();
            new Thread(new KeyboardListener(socket)).start();
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void register(ChatListenerInterFace listener) {
        ClientListener.register(listener);
    }

    public void send(String msg) {
        out.println(msg);
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

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String ip = "localhost";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        new ChatClient(ip, port);
    }
}

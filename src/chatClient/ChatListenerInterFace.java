package chatClient;


public interface ChatListenerInterFace {
    
    public void onMessage(String name, String msg);
    public void onList(String[] list);
    
}

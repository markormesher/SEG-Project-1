package server;

/**
 * Triggered when the server has any generic message to send
 */
public interface ServerMessageListener {

	public void onServerMessageReceived(String message);
}

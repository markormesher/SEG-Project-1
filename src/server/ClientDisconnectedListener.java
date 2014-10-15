package server;

/**
 * Triggered on disconnections from the server
 */
public interface ClientDisconnectedListener {

	void onClientDisconnected(String name);

}
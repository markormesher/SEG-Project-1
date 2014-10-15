package server;

/**
 * Triggered on new connections to the server
 */
public interface ClientConnectedListener {

    void onClientConnected(String name);

}
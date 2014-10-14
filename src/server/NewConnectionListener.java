package server;

/**
 * Triggered on new connections to the server
 */
public interface NewConnectionListener {
    void onNewConnection(String name);
}

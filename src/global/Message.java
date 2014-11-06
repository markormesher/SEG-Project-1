package global;

import java.io.Serializable;

public class Message implements Serializable {

	// required by ObjectIO stream serializer
	protected static final long serialVersionUID = 1112122200L;

	// message types that can be sent
	public static final int SET_USERNAME = 1,
		SET_OPPONENT = 2,
		READY_TO_PLAY = 3,
		SHOOT = 4,
		HIT = 5,
		MISS = 6,
		CHAT_MESSAGE = 7,
		SERVER_GONE = 8,
		OPPONENT_DISCONNECTED = 9,
		PLAYER_LOSE = 10,
		LOGIN = 11,
		LOGIN_OK = 12,
		LOGIN_FAILED = 13;


	// details of this message
	private String recipient;
	private int type;
	private String message;
	private int x;
	private int y;

	public Message(String recipient, int type) {
		this.recipient = recipient;
		this.type = type;
	}


	public Message(String recipient, int type, String message) {
		this.recipient = recipient;
		this.type = type;
		this.message = message;
	}

	public Message(String recipient, int type, int x, int y) {
		this.recipient = recipient;
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public String getRecipient() {
		return recipient;
	}

	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

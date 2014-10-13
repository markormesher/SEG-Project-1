package client;

import global.Message;

public interface BattleClientGuiInterface {

	public void onReceiveMessage(Message msg);

	public void onSetOpponent(String opponent);

}
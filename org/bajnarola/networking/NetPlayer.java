package org.bajnarola.networking;

import java.io.Serializable;

import org.bajnarola.game.BajnarolaServer;
import org.bajnarola.game.model.Board;
import org.bajnarola.lobby.NetPlayerAggregator;

public class NetPlayer implements Serializable {
	public String username;
	public String rmiUriMain;
	public String rmiUriBoard;
	public String rmiUriLobby;
	
	public NetPlayer(String uname, String uri) {
		this.username = uname;
		this.rmiUriMain = uri + "/" + BajnarolaServer.class.getName();
		this.rmiUriBoard = uri + "/" + Board.class.getName();
		this.rmiUriLobby = uri + "/" + NetPlayerAggregator.class.getName();
	}
}

package org.bajnarola.game;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GameOptions {

	String lobbyServerURI, playerName, localServerName;
	
	public GameOptions(String playerName, String lobbyServerURI) {
		try {
			this.localServerName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		this.playerName = playerName;
		this.lobbyServerURI = lobbyServerURI;
	}

	public String getLobbyServerURI() {
		return lobbyServerURI;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getLocalServerName() {
		return localServerName;
	}
}

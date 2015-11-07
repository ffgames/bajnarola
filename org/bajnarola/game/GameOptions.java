package org.bajnarola.game;

import java.net.MalformedURLException;
import org.bajnarola.utils.BajnarolaRegistry;

public class GameOptions {
	String lobbyHost, playerName;
	int lobbyPort = BajnarolaRegistry.DEFAULT_LOBBY_PORT;
	
	public GameOptions(String playerName, String lobbyServerURI) throws MalformedURLException {
		this.playerName = playerName;
		System.out.println("<< " + lobbyServerURI + " >>");
		
		String splittedURI[] = lobbyServerURI.split(":");
		
		if (splittedURI.length < 1 || splittedURI.length > 2)
			throw new MalformedURLException("Malformed Lobby URI");
		
		this.lobbyHost = splittedURI[0];
		
		if (splittedURI.length == 2) 
			lobbyPort = Integer.parseInt(splittedURI[1]);	
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getLobbyHost() {
		return this.lobbyHost;
	}

	public int getLobbyPort() {
		return this.lobbyPort;
	}
	
	public String getPlayerName() {
		return playerName;
	}
}

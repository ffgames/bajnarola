package org.bajnarola.game;

import java.net.MalformedURLException;

import org.bajnarola.lobby.LobbyServer;

public class GameOptions {
	String lobbyHost, lobbyName, playerName;
	
	public GameOptions(String playerName, String lobbyServerURI) throws MalformedURLException {
		this.playerName = playerName;
		System.out.println("<< " + lobbyServerURI + " >>");
		
		String splittedURI[] = lobbyServerURI.split("/");
		
		if (splittedURI.length < 1 || splittedURI.length > 2)
			throw new MalformedURLException("Malformed Lobby URI");
		
		this.lobbyHost = splittedURI[0];
		
		if (splittedURI.length == 1) 
			this.lobbyName = LobbyServer.DEFAULT_LOBBY_NAME;
		else
			this.lobbyName = splittedURI[1];
		
	}

	public String getLobbyHost() {
		return this.lobbyHost;
	}

	public String getLobbyName() {
		return this.lobbyName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
}

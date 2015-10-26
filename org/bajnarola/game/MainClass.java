package org.bajnarola.game;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.controller.GameControllerRemote;
import org.bajnarola.game.controller.GameController;
import org.bajnarola.lobby.LobbyClient;

public class MainClass {
	private static final String SERVICE = "rmi";
	private static int seed = -1; 
	
	
	public static void main(String[] argv) {
		LobbyClient     iLobby  = null;
		BajnarolaServer iServer = null;
		BajnarolaClient iClient = null;
		
		GameOptions goptions = null;
		
		String username = "";
		String server   = "";
		String lobbyserver;
		
		try {
			System.out.println("Bajnarola starting up.");
			
			System.out.print("Personal board set up...");
			GameController gBoard = new GameController();
			System.out.println("OK!");
		
			gBoard.waitOptionsFromUser();
			
			goptions = gBoard.getGameOptions();
			
			username = goptions.getPlayerName();
			server = goptions.getLocalServerName();
			lobbyserver = goptions.getLobbyServerURI();

			System.out.print("Server start up:");
			if (!username.isEmpty())
				iServer = new BajnarolaServer(SERVICE + "://" + server, username, gBoard);
			else
				iServer = new BajnarolaServer(SERVICE + "://" + server, gBoard);
			System.out.println("OK!");
						
			System.out.print("Client module initilization:");
			iClient = new BajnarolaClient();
			System.out.println("OK!");
			
			System.out.print("Registering to lobby... ");
			iLobby = new LobbyClient(lobbyserver);
			System.out.println("OK!");
			
			System.out.print("Joining the default room...");
			
			/* Join the lobby and set neighbors list */
			iClient.getPlayers(iLobby.join(iServer.getPlayer()));
			
			System.out.println("OK!");
			System.out.println("Beginning game with " + iClient.players.size() + " players.");
			
			/* Get others dice throws */
			Map<String,Integer> dices;
			
			dices = iClient.multicastInvoke(GameControllerRemote.class.getMethod("getDiceValue"));
			/* Sorting players based on dice throws */
			iClient.sortPlayerOnDiceThrow(dices);
			
			System.out.println("Got players:");
			for (String k : iClient.players.keySet()) {
				System.out.println("\t" + k + " with dice throw: " + dices.get(k));
				if (seed == -1)
					seed = dices.get(k);
			}
			
			System.out.print("Initializing the board...");
			
			List<String> playerNames = new ArrayList<>(iClient.players.keySet());
			
			gBoard.initBoard(iServer.getPlayer().username, playerNames, seed);
			System.out.println("OK");
			
			iClient.mainLoop(iServer.getPlayer().username, gBoard);
			
		} catch (RemoteException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
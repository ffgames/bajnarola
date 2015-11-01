package org.bajnarola.game;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.controller.GameControllerRemote;
import org.bajnarola.game.controller.GameController;
import org.bajnarola.game.view.LobbyScene.UnlockCause;
import org.bajnarola.lobby.LobbyClient;
import org.bajnarola.networking.NetPlayer;

public class MainClass {
	private static final String SERVICE = "rmi";
	private static int seed = -1; 
	
	public static void main(String[] argv) {
		LobbyClient     iLobby  = null;
		BajnarolaServer iServer = null;
		BajnarolaClient iClient = null;
		
		boolean okLobby = false;
		GameOptions goptions = null;
		
		String username = "";
		String server   = "";
		String lobbyserver;
		
		Map<String, NetPlayer> players = null;
		UnlockCause cause;
		
		try {
			System.out.println("Bajnarola starting up.");
			
			System.out.print("Personal board set up...");
			GameController gBoard = new GameController();
			System.out.println("OK!");
			while (!okLobby) {
			
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
				try {
					iLobby = new LobbyClient(lobbyserver);
				} catch (Exception e1) {
					iLobby = null;
					iServer = null;
					gBoard.viewCtl.unlockView(UnlockCause.lobbyNotFound);
					System.err.println("Can't reach the lobby");
					continue;
				}
				System.out.println("OK!");
				
				System.out.print("Joining the default room...");
				try {
					/* Join the lobby and set neighbors list.
					 * If there is any error, try again. */
					players = iLobby.join(iServer.getPlayer());
					okLobby = true;
				} catch (RemoteException e) {
					if (e.getMessage().contains("User")) {
						iServer = null;
						System.err.println("User already exists");
						cause = UnlockCause.userExists;
						gBoard.viewCtl.unlockView(cause);
					} else if (e.getMessage().contains("Game")){
						System.err.println("Game already started");
						cause = UnlockCause.gameStarted;
						gBoard.viewCtl.unlockView(cause);
					} else {
						iLobby = null;
						iServer = null;
						gBoard.viewCtl.unlockView(UnlockCause.lobbyError);
						System.err.println("Can't connect to the lobby");
						e.printStackTrace();
					}
				}
			}
			
			iClient.getPlayers(players);
			
			System.out.println("OK!");
			cause = UnlockCause.userOk;
			gBoard.viewCtl.unlockView(cause);
			
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
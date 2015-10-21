package org.bajnarola.game;

import java.rmi.RemoteException;
import java.util.Map;

import org.bajnarola.game.controller.BoardController;
import org.bajnarola.game.controller.GameBoard;
import org.bajnarola.game.model.Board;
import org.bajnarola.lobby.LobbyClient;

public class MainClass {
	private static final String SERVER = "localhost";
	private static final String LOBBY_SERVER = "localhost";
	private static final String SERVICE = "rmi";

	public static void main(String[] argv) {
		/* TODO: RMI modules            */
		/* TODO: new BajnarolaClient(); */
		
		/* TODO: Graphics               */
		/* TODO: new Gui();             */
		LobbyClient     iLobby  = null;
		BajnarolaServer iServer = null;
		BajnarolaClient iClient = null;
		
		String username = "";
		String server   = "";
		String lobbyserver;
		
		/* TODO getopt */
		if (argv.length > 0)
			username = argv[0];
		
		if (argv.length > 1)
			lobbyserver = argv[1];
		else
			lobbyserver = LOBBY_SERVER;
		
		if (argv.length > 2)
			server = argv[2];
		else
			server = SERVER;
		
		try {
			System.out.println("Bajnarola starting up.");
			
			System.out.print("Personal board set up...");
			GameBoard gBoard = new GameBoard();
			System.out.println("OK!");

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
			
			/* TODO: differentiate more room and real lobby dispatching */
			System.out.print("Joining the default room...");
			/* Join the lobby and set neighbors list */
			iClient.getPlayers(iLobby.join(iServer.getPlayer()));
			
			System.out.println("OK!");
			System.out.println("Beginning game with " + iClient.players.size() + " players.");
			
			/* Get others dice throws */
			Map<String,Integer> dices;
			
			dices = iClient.multicastInvoke(BoardController.class.getMethod("getDiceValue"));
			/* Sorting players based on dice throws */
			iClient.sortPlayerOnDiceThrow(dices);
			
			System.out.println("Got players:");
			for (String k : iClient.players.keySet()) {
				System.out.println("\t" + k + " with dice throw: " + dices.get(k));
			}
			
			System.out.print("Initializing the board...");
			//gBoard.initBoard(iClient.players.keySet());
			System.out.println("OK");
			
			iClient.mainLoop(iServer.getPlayer().username);
			
		} catch (RemoteException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
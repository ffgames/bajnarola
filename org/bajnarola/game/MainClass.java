package org.bajnarola.game;

import java.rmi.RemoteException;

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
		LobbyClient iLobby = null;
		BajnarolaServer iServer = null;
		String username = "";
		
		if (argv.length > 0) {
			username = argv[0];
		}
		
		try {
			System.out.println("Bajnarola starting up.");
			
			System.out.print("Personal board set up...");
			Board gBoard = new Board();
			System.out.println("OK!");

			System.out.print("Server start up:");
			if (!username.isEmpty())
				iServer = new BajnarolaServer(SERVICE + "://" + SERVER, username, gBoard);
			else
				iServer = new BajnarolaServer(SERVICE + "://" + SERVER, gBoard);
			System.out.println("OK!");
			
			System.out.print("Registering to lobby... ");
			iLobby = new LobbyClient(LOBBY_SERVER);
			System.out.println("OK!");
			
			/* TODO: differentiate more room and real lobby dispatching */
			System.out.print("Joining the default room...");
			iLobby.join(iServer.getPlayer());
			System.out.println("OK!");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
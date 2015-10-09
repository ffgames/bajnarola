package org.bajnarola.game;

import java.rmi.RemoteException;

import org.bajnarola.game.model.Board;

public class MainClass {
	private static final String SERVER = "localhost";
	private static final String LOBBY_SERVER = "localhost";
	private static final String SERVICE = "rmi";

	public static void main(String[] argv) {
		/* TODO: RMI modules            */
		/* TODO: new BajnarolaClient(); */
		
		/* TODO: Graphics               */
		/* TODO: new Gui();             */
		BajnarolaServer iServer = null;
		
		try {
			System.out.println("Bajnarola starting up.");
			
			System.out.print("Personal board set up...");
			Board gBoard = new Board();
			System.out.println("OK!");

			System.out.print("Server start up:\n\t");
			iServer = new BajnarolaServer(SERVICE + "://" + SERVER, gBoard);
			System.out.println("OK!");
			
			System.out.print("Deciding first player...");
			System.out.println("TODO");
			
			System.out.println();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
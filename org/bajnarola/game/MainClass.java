package org.bajnarola.game;

import java.rmi.RemoteException;

import org.bajnarola.game.model.Board;

public class MainClass {
	public static void main(String[] argv) {
		/* TODO: RMI modules            */
		/* TODO: new BajnarolaClient(); */
		/* TODO: new BajnarolaServer(); */
		
		/* TODO: Graphics               */
		/* TODO: new Gui();             */
		
		try {
			Board gBoard = new Board();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
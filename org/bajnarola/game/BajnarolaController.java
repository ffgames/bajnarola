package org.bajnarola.game;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BajnarolaController extends Remote {
	public void startGame() throws RemoteException;
}

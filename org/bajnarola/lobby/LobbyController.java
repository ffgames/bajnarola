package org.bajnarola.lobby;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyController extends Remote {
	public Integer join() throws RemoteException;
}

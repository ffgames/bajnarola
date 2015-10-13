package org.bajnarola.lobby;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.bajnarola.networking.NetPlayer;

public interface LobbyController extends Remote {
	public void join(NetPlayer p) throws RemoteException;
	public void join(NetPlayer p, String room) throws RemoteException;
}

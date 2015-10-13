package org.bajnarola.lobby;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.bajnarola.networking.NetPlayer;

public interface LobbyController extends Remote {
	public Map<String,NetPlayer> join(NetPlayer p) throws RemoteException;
	public Map<String,NetPlayer> join(NetPlayer p, String room) throws RemoteException;
}

package org.bajnarola.lobby;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.bajnarola.networking.NetPlayer;

public interface NetPlayerAggregatorController extends Remote {
	public void addPlayer(NetPlayer p) throws RemoteException;
	public void addPlayers(Map<String, NetPlayer> m) throws RemoteException;
}

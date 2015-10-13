package org.bajnarola.lobby;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

import org.bajnarola.networking.NetPlayer;

public class NetPlayerAggregator extends UnicastRemoteObject implements NetPlayerAggregatorController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, NetPlayer> players;

	public NetPlayerAggregator() throws RemoteException {
		this.players = new Hashtable<String, NetPlayer>();
	}
	
	@Override
	public void addPlayer(NetPlayer p) throws RemoteException {
		this.players.put(p.username, p);
	}
	
	@Override
	public void addPlayers(Map<String,NetPlayer> h) throws RemoteException {
		this.players.putAll(h);
		System.out.println("Got " + this.players.size() + " player addresses.");
	}
}

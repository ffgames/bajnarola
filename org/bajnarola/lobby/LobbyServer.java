package org.bajnarola.lobby;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

import org.bajnarola.networking.NetPlayer;
import org.bajnarola.utils.BajnarolaRegistry;

import sun.misc.Lock;

public class LobbyServer extends UnicastRemoteObject implements LobbyController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_LOBBY_NAME = "deflobby";
	
	Map<String,NetPlayer> players = new Hashtable<String,NetPlayer>();
	Integer maxPlayers = 0;
	String lpath;
	int port;
	Lock plock;

	private Boolean done;
	
	public LobbyServer(int lobbyPort, int playersNo, int timeout) throws Exception {
		this.done = new Boolean(false);
		this.maxPlayers = playersNo;
		this.plock = new Lock();
		this.port = lobbyPort;
		
		try {
			this.plock.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int i;

		
		this.lpath = this.getClass().getName();
		//Naming.rebind(lpath, this);
		
		Registry localRegistry;
		
		try {
			localRegistry = BajnarolaRegistry.createRegistry(lobbyPort);
		} catch (RemoteException e1) {
			throw new Exception("Registry already launched at this port");
		}
		
		try {
			localRegistry.lookup(lpath);
			throw new Exception("Lobby already bound");
		} catch (NotBoundException e) {
			localRegistry.rebind(lpath, this);
		}
		System.out.print("Listening on port " + lobbyPort + ", " + lpath);

		System.out.println("OK!");
		
		i = 0;
		while (!this.done && i < timeout) {
			Thread.sleep(1000);
			i++;
		}

		if (!this.done)
			this.fireTimeout();
		
	}

	public Map<String,NetPlayer> join(NetPlayer p) throws RemoteException {
		return this.join(p, "");
	}
	
	@Override
	public Map<String,NetPlayer> join(NetPlayer p, String room) throws RemoteException {
		if (this.players.containsKey(p.username))
			throw new RemoteException("User Already Present");
		if (this.done)
			throw new RemoteException("Game already started");
		
		this.players.put(p.username, p);
		
		
		System.out.println("Got a new player: " + p.username + " (" +p.rmiUriBoard + ")");
		
		try {
			p.host = getClientHost();
			System.out.println("host: " + p.host);
		} catch (ServerNotActiveException e1) {
			e1.printStackTrace();
		}
		
		if (this.players.size() < this.maxPlayers) {
			try {
				this.plock.lock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.startGame();
		this.plock.unlock();
		
		return players;
	}
	
	private void fireTimeout() {
		if (!this.done && this.players.size() >= 2) {
			this.plock.unlock();
			this.startGame();
		}
	}
	
	private void startGame() {
		/* TODO: Implementation */
		if (this.done)
			return;
		
		this.done = true;
		System.out.println("Get ready to play!");
		/* Lobby Shutdown */
		try {
			BajnarolaRegistry.getRegistry(port).unbind(this.lpath);
			UnicastRemoteObject.unexportObject(this, true);
			
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Integer players = 2;
		Integer timeout = 10;
		int lobbyPort = BajnarolaRegistry.DEFAULT_LOBBY_PORT;

		if (args.length > 0)
			players = Integer.decode(args[0]);
		if (args.length > 1 && !args[1].isEmpty())
			lobbyPort = Integer.parseInt(args[1]);
		if (args.length > 2)
			timeout = Integer.decode(args[2]);
		
		try {
			new LobbyServer(lobbyPort, players, timeout);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

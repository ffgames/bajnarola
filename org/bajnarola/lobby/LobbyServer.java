package org.bajnarola.lobby;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.bajnarola.networking.NetPlayer;
import org.bajnarola.utils.BajnarolaRegistry;

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
	ReentrantLock plock;
	Condition lobbyWaitForPlayers, playerWaitForGameStart;
	boolean force = false;

	private Boolean done;
	
	public LobbyServer(int lobbyPort, int playersNo, int timeout) throws Exception {
		this.done = new Boolean(false);
		this.maxPlayers = playersNo;
		this.plock = new ReentrantLock();
		this.port = lobbyPort;
		this.lobbyWaitForPlayers = plock.newCondition();
		this.playerWaitForGameStart = plock.newCondition();
		
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
		
		ArrayList<String> playersToRemove = new ArrayList<String>();
		NetPlayer p;
		
		plock.lock();
		while(players.size() < maxPlayers && !force){
			lobbyWaitForPlayers.await();
			for(String u : players.keySet()){
				try {
					p = players.get(u);
					BajnarolaRegistry.getRegistry(p.host, p.bindPort).lookup(p.rmiUriBoard);
				} catch (RemoteException | NotBoundException | NullPointerException e) {
					playersToRemove.add(u);
				}
			}
			for(String u : playersToRemove){
				players.remove(u);
				System.out.println("Player " + u + " left the lobby...");
			}
			playersToRemove.clear();
		}
		playerWaitForGameStart.signalAll();
		plock.unlock();

		startGame();
	}
	
	public void forceStart(){
		force = true;
		plock.lock();
		lobbyWaitForPlayers.signalAll();
		plock.unlock();
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
		
		plock.lock();
		lobbyWaitForPlayers.signal();
		try {
			playerWaitForGameStart.await();
		} catch (InterruptedException e) {
			throw new RemoteException(e.getMessage());
		} finally {
			plock.unlock();
		}
		
		return players;
	}
	
	private void startGame() {
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

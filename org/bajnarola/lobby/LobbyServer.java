package org.bajnarola.lobby;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

import org.bajnarola.networking.NetPlayer;

import sun.misc.Lock;

public class LobbyServer extends UnicastRemoteObject implements LobbyController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String SERVER = "localhost";
	private static final String SERVICE = "rmi";
	
	Map<String,NetPlayer> players = new Hashtable<String,NetPlayer>();
	Integer maxPlayers = 0;
	String lpath;
	Lock plock;

	private Boolean done;
	
	public LobbyServer(String server, int playersNo, int timeout) throws RemoteException {
		this.done = new Boolean(false);
		this.maxPlayers = playersNo;
		this.plock = new Lock();
		
		try {
			this.plock.lock();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		int i;

		try{
			this.lpath = SERVICE + "://" + server + "/" + this.getClass().getName();
			System.out.print("Listening on " + lpath + " ...");
			Naming.rebind(lpath, this);
			System.out.println("OK!");
			
			i = 0;
			while (!this.done && i < timeout) {
				Thread.sleep(1000);
				i++;
			}

			if (!this.done)
				this.fireTimeout();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			Naming.unbind(this.lpath);
			UnicastRemoteObject.unexportObject(this, true);
		} catch (RemoteException | NotBoundException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Integer players = 2;
		Integer timeout = 10;
		String server = "";
		
		if (args.length > 0)
			players = Integer.decode(args[0]);
		if (args.length > 1)
			timeout = Integer.decode(args[1]);
		if (args.length > 2)
			server = args[2];
		else
			server = SERVER;
		
		try {
			new LobbyServer(server, players, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

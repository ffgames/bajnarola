package org.bajnarola.lobby;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

import org.bajnarola.game.BajnarolaController;
import org.bajnarola.game.BajnarolaServer;
import org.bajnarola.networking.NetPlayer;

public class LobbyServer extends UnicastRemoteObject implements LobbyController {
	private static final String SERVER = "localhost";
	private static final String SERVICE = "rmi";
	
	private Map<String,NetPlayer> players = new Hashtable<String,NetPlayer>();
	private Integer maxPlayers = 0;
	private String lpath;
	
	private Boolean done;
	
	public LobbyServer(String server, int playersNo, int timeout) throws RemoteException {
		this.done = new Boolean(false);
		this.maxPlayers = playersNo;

		try{
			this.lpath = SERVICE + "://" + server + "/" + this.getClass().getName();
			System.out.print("Listening on " + lpath + " ...");
			Naming.rebind(lpath, this);
			System.out.println("OK!");
			
			while (!this.done) {
				Thread.sleep(1000 * timeout);
				this.fireTimeout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void join(NetPlayer p) throws RemoteException {
		this.join(p, "");
	}
	
	@Override
	public void join(NetPlayer p, String room) throws RemoteException {
		if (this.players.containsKey(p.username))
			throw new RemoteException("User Already Present");
		if (this.done)
			throw new RemoteException("Game already started");
		
		this.players.put(p.username, p);
		System.out.println("Got a new player: " + p.username + " (" +p.rmiUriBoard+"," + p.rmiUriLobby + ")");
		
		if (this.players.size() >= this.maxPlayers) {
			this.startGame();
		}
	}
	
	/* XXX maybe we need an atomical transaction on done? */
	private void fireTimeout() {
		if (!this.done && this.players.size() >= 2) {
			this.startGame();
		}
	}
	
	private void startGame() {
		/* TODO: Implementation */
		this.done = true;
		System.out.println("Get ready to play!");
		/* Signal all the players to all players. */
		for (String u: players.keySet()) {
			NetPlayer p = players.get(u);
			BajnarolaController b;
			NetPlayerAggregatorController na;
			try {
				na = (NetPlayerAggregatorController) Naming.lookup(p.rmiUriLobby);
				na.addPlayers(players);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				b = (BajnarolaController) Naming.lookup(p.rmiUriMain);
				b.startGame();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* XXX Lobby Shutdown */
	}

	public static void main(String[] args) {
		Integer players = 2;
		Integer timeout = 10;
		
		if (args.length > 0)
			players = Integer.decode(args[0]);
		if (args.length > 1)
			timeout = Integer.decode(args[1]);
		
		try {
			new LobbyServer(SERVER, players, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

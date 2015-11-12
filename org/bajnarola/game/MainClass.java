package org.bajnarola.game;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.GameOptions.ConfigType;
import org.bajnarola.game.controller.GameControllerRemote;
import org.bajnarola.game.controller.GameController;
import org.bajnarola.game.view.LobbyScene.JoinStatus;
import org.bajnarola.lobby.LobbyClient;
import org.bajnarola.networking.NetPlayer;
import org.newdawn.slick.SlickException;

public class MainClass {
	
	private static int seed; 
	
	public static final boolean debugPlay = false;
	
	public static void main(String[] argv) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		LobbyClient     iLobby  = null;
		BajnarolaServer iServer = null;
		BajnarolaClient iClient = null;
		
		
		boolean okLobby = false;
		GameOptions goptions = null;
		
		String username = "";
		String lobbyHost;
		int lobbyPort;
		
		
		Map<String, NetPlayer> players = null;
		
		System.setProperty( "java.library.path", "lib/native:lib/native" );
		 
		Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
		fieldSysPath.setAccessible( true );
		fieldSysPath.set( null, null );
		

		GameController gBoard = null;
		try {
			gBoard = new GameController();
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Bajnarola starting up.");
		System.out.print("Personal board set up...");
		System.out.println("OK!");
		
		
		while (true) {
			try {
				seed = -1;
				okLobby = false;
				
				gBoard.reinitLock.lock();
				gBoard.reinit();
				
				while (!okLobby) {
				
					gBoard.waitOptionsFromUser();
					
					goptions = gBoard.getGameOptions();
					
					username = goptions.getPlayerName();
					
					lobbyHost = goptions.getLobbyHost();
					lobbyPort = goptions.getLobbyPort();
					
					System.out.print("Server start up:");
					if (!username.isEmpty())
						iServer = new BajnarolaServer(username, gBoard);
					else
						iServer = new BajnarolaServer(gBoard);
					
					gBoard.setMyServer(iServer);
					
					System.out.println("OK!");
								
					System.out.print("Client module initilization:");
					iClient = new BajnarolaClient();
					System.out.println("OK!");
					
					System.out.println("Registering to lobby at " + lobbyHost + "...");
					try {
						iLobby = new LobbyClient(lobbyHost, lobbyPort);
					} catch (Exception e1) {
						iLobby = null;
						iServer = null;
						gBoard.viewCtl.joinSignalView(JoinStatus.LOBBY_NOT_FOUND);
						System.err.println("Can't reach the lobby server");
						e1.printStackTrace();
						continue;
					}
					System.out.println("OK!");
					
					System.out.print("Joining the default room...");
					try {
						/* Join the lobby and set neighbours list.
						 * If there is any error, try again. */
						players = iLobby.join(iServer.getPlayer());
						okLobby = true;
					} catch (RemoteException e) {
						iServer = null;
						iLobby = null;
						iClient = null;
						
						if (e.getMessage().contains("User")) {
							/* XXX: players do not communicate in game */
							gBoard.viewCtl.joinSignalView(JoinStatus.USER_EXISTS);
							System.err.println("User already exists");
						} else if (e.getMessage().contains("Game")){
							gBoard.viewCtl.joinSignalView(JoinStatus.GAME_STARTED);
							System.err.println("Game already started");
						} else {
							gBoard.viewCtl.joinSignalView(JoinStatus.LOBBY_ERROR);
							System.err.println("Can't connect to the lobby");
							e.printStackTrace();
						}
					}
				}
				
				goptions.storeConfig(ConfigType.LOBBY);
				
				iClient.getPlayers(players);
				
				System.out.println("OK!");
				gBoard.viewCtl.joinSignalView(JoinStatus.LOBBY_OK);
				
				System.out.println("Beginning game with " + iClient.players.size() + " players.");
				
				/* Get others dice throws */
				Map<String,Integer> dices;
				
				dices = iClient.multicastInvoke(GameControllerRemote.class.getMethod("getDiceValue"));
				/* Sorting players based on dice throws */
				iClient.sortPlayerOnDiceThrow(dices);
				
				System.out.println("Got players:");
				for (String k : iClient.players.keySet()) {
					System.out.println("\t" + k + " with dice throw: " + dices.get(k));
					if (seed == -1) {
						seed = dices.get(k);
						gBoard.gameId = Integer.toString(seed) + k;
						System.out.println(gBoard.gameId);
					}
				}
				
				System.out.print("Initializing the board...");
				
				List<String> playerNames = new ArrayList<>(iClient.players.keySet());
				
				gBoard.initBoard(iServer.getPlayer().username, playerNames, seed);
				System.out.println("OK");
				
				iClient.mainLoop(iServer.getPlayer().username, gBoard);
			} catch (SlickException | NoSuchMethodException | InterruptedException e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
	}
}

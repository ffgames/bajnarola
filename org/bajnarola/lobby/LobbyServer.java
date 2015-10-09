package org.bajnarola.lobby;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class LobbyServer implements LobbyController {
	private static final String SERVER = "localhost";
	private static final String SERVICE = "rmi";
	
	public LobbyServer(String server, int players, int timeout) {
		try {
			Naming.rebind(SERVICE + "://" + server + "/" + this.getClass().getName(), this);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LobbyServer iLobby = new LobbyServer(SERVER, 8, 10);
	}

	@Override
	public Integer join() throws RemoteException {
		/* TODO Implementation. */
		return null;
	}
}

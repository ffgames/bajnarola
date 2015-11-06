package org.bajnarola.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BajnarolaRegistry {
	
	public static final int DEFAULT_LOBBY_PORT = 42665;
	public static final int DEFAULT_PLAYER_PORT = 42666;
	

	public static Registry createRegistry(int port) throws RemoteException {
		return LocateRegistry.createRegistry(port);
	}
	
	public static Registry getRegistry(String host, int port) throws RemoteException {
		return LocateRegistry.getRegistry(host, port);
	}
	
	public static Registry getRegistry(int port) throws RemoteException {
		return LocateRegistry.getRegistry(port);
	}
	
}

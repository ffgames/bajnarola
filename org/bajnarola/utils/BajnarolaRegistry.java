package org.bajnarola.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class BajnarolaRegistry {
	
	private static final int DEFAULT_PORT = 42666;
	
	public static Registry getLocalRegistry() throws RemoteException{
		return getLocalRegistry(DEFAULT_PORT);
	}
	
	public static Registry getLocalRegistry(int port) throws RemoteException{
		java.rmi.registry.Registry rmiregisrty;
		
		rmiregisrty = null;
		
		try {
			rmiregisrty = LocateRegistry.createRegistry(port);
		} catch (ExportException e) {	
			rmiregisrty = LocateRegistry.getRegistry(port);
		}
		
		return rmiregisrty;
	}

	public static Registry getRemoteRegistry(String server) throws RemoteException{
		return getRemoteRegistry(server, DEFAULT_PORT);
	}
	
	
	public static Registry getRemoteRegistry(String server, int port) throws RemoteException {
		java.rmi.registry.Registry rmiregisrty;
		
		rmiregisrty = LocateRegistry.getRegistry(server, port);

		return rmiregisrty;
	}
}

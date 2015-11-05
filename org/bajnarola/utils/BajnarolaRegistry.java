package org.bajnarola.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class BajnarolaRegistry {
	public static Registry getLocalRegisrty() throws RemoteException{
		return getLocalRegistry(1099);
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
}

package org.bajnarola.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BajnarolaRegistry {
	public static Registry getLocalRegisrty(){
		return getLocalRegistry(1099);
	}
	
	public static Registry getLocalRegistry(int port){
		java.rmi.registry.Registry rmiregisrty;
		try{
			rmiregisrty = LocateRegistry.getRegistry(port);
		} catch (RemoteException e){
			try {
				rmiregisrty = LocateRegistry.createRegistry(port);
			} catch (RemoteException e1) {
				rmiregisrty = null;
				e1.printStackTrace();
			}
		}
		return rmiregisrty;
	}
}

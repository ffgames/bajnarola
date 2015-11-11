package org.bajnarola.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LobbyAddresser {
	private List<String> addresses;
	
	public LobbyAddresser() throws SocketException{
		this.addresses = new ArrayList<String>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                this.addresses.add(addr.getHostAddress());
            }
        }
	}
	
	public String getAddress(int i){
		return addresses.get(i);
	}
	
	public int size(){
		return addresses.size();
	}
}

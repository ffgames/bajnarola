/*****************************************************************************/
/* Client package for Bajnarola distributed game                             */
/*                                                                           */
/* Copyright (C) 2015                                                        */
/* Marco Melletti, Davide Berardi, Matteo Martelli                           */
/*                                                                           */
/* This program is free software; you can redistribute it and/or             */
/* modify it under the terms of the GNU General Public License               */
/* as published by the Free Software Foundation; either version 2            */
/* of the License, or any later version.                                     */
/*                                                                           */
/* This program is distributed in the hope that it will be useful,           */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of            */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             */
/* GNU General Public License for more details.                              */
/*                                                                           */
/* You should have received a copy of the GNU General Public License         */
/* along with this program; if not, write to the Free Software               */
/* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,*/
/* USA.                                                                      */
/*****************************************************************************/

package org.bajnarola.lobby;

import java.rmi.RemoteException;
import java.util.Map;

import org.bajnarola.lobby.LobbyController;
import org.bajnarola.lobby.LobbyServer;
import org.bajnarola.networking.NetPlayer;
import org.bajnarola.utils.BajnarolaRegistry;

public class LobbyClient {
	private final String SERVICE = "rmi";
	
	private LobbyController lobbyCallback = null;

	
	public LobbyClient(String server) throws Exception {
		
		String lookupString = SERVICE + "://" + LobbyServer.DEFAULT_LOBBY + "/" + LobbyServer.class.getName();
				
		System.out.print("\n\tLookup on: " + lookupString + " ...");
		if (server.equals("localhost"))
			this.lobbyCallback = (LobbyController)BajnarolaRegistry.getLocalRegistry().lookup(lookupString);
		else 
			this.lobbyCallback = (LobbyController)BajnarolaRegistry.getRemoteRegistry(server).lookup(lookupString);		

	}
	
	public Map<String,NetPlayer> join(NetPlayer p, String room) throws RemoteException {
		Map<String,NetPlayer> omap = null;
		
		omap = this.lobbyCallback.join(p, room);
		
		
		return omap;
	}

	public Map<String,NetPlayer> join(NetPlayer p) throws RemoteException {
		return this.join(p, "");
	}
}
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
	
	private LobbyController lobbyCallback = null;

	
	public LobbyClient(String lobbyHost, int lobbyPort) throws Exception {
		/* ServerURI format example: hostname.com/lobbyName 
		 * If lobbyName is not provided then the default lobby name should be already set */
		
		String lookupString = LobbyServer.class.getName();
					
		System.out.println("\n\tLookup on: " + lookupString + " ... at " + lobbyHost + ":" + lobbyPort);
	
		this.lobbyCallback = (LobbyController)BajnarolaRegistry.getRegistry(lobbyHost, lobbyPort).lookup(lookupString);		

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
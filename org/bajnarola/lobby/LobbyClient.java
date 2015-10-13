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

import java.rmi.Naming;
import java.util.Map;

import org.bajnarola.lobby.LobbyController;
import org.bajnarola.lobby.LobbyServer;
import org.bajnarola.networking.NetPlayer;

public class LobbyClient {
	private final String SERVICE = "rmi";
	
	private LobbyController lobbyCallback = null;

	
	public LobbyClient(String server) {
		try {
			String lookupString = SERVICE + "://" + server + "/" + LobbyServer.class.getName();
					
			System.out.print("\n\tLookup on: " + lookupString + " ...");
			this.lobbyCallback = (LobbyController) Naming.lookup(lookupString);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Map<String,NetPlayer> join(NetPlayer p, String room) {
		Map<String,NetPlayer> omap = null;
		try {
			omap = this.lobbyCallback.join(p, room);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return omap;
	}

	public Map<String,NetPlayer> join(NetPlayer p) {
		return this.join(p, "");
	}
}
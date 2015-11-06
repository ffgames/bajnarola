/*****************************************************************************/
/* Server package for Bajnarola distributed game                             */
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

package org.bajnarola.game;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.bajnarola.game.controller.GameController;
import org.bajnarola.networking.NetPlayer;
import org.bajnarola.utils.BajnarolaRegistry;
import org.bajnarola.utils.RandomString;

public class BajnarolaServer implements Remote {

	NetPlayer player = null;
	
	public NetPlayer getPlayer() {
		return this.player;
	}
	
	private void setRebind(String path, Remote o) {
		String npath = path + "/" + o.getClass().getName();
		try {
			BajnarolaRegistry.getLocalRegistry().rebind(npath, o);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		System.out.print("\n\tListening on '" + npath + "' ...");
	}
	
	private void CommonConstruct(String lobbyName, String basepath, GameController myBoard) {
		String path = lobbyName + "/" + basepath;
		this.player = new NetPlayer(basepath, path);
				
		this.setRebind(path, myBoard);
	}

	public BajnarolaServer(String server, String basepath, GameController myBoard) {
		this.CommonConstruct(server, basepath, myBoard);
	}
	public BajnarolaServer(String server, GameController myBoard) {
		String s = RandomString.generateAsciiString();
		this.CommonConstruct(server, s, myBoard);
	}
}
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

package org.bajnarola.game;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.bajnarola.game.controller.BoardController;
import org.bajnarola.game.controller.GameBoard;
import org.bajnarola.networking.NetPlayer;

import java.util.Collections;

import java.util.List;

public class BajnarolaClient {
	/* XXX maybe a list is better. */
	Map<String,BoardController> players;

	public BajnarolaClient() {
		this.players = new LinkedHashMap<String,BoardController>();
	}
	
	public void getPlayers(Map<String,NetPlayer> playersStrings) {
		for (String user : playersStrings.keySet()) {
			BoardController bc;
			try {
				bc = (BoardController) Naming.lookup(playersStrings.get(user).rmiUriBoard);
				this.players.put(user, bc);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				/* XXX crash! */
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sortPlayerOnDiceThrow(Map<String, Integer> dices) {

		Map<String,BoardController> tmpplayers = new LinkedHashMap<String,BoardController>();
		
		List<Entry<String,Integer>> tmpl = new LinkedList <Entry<String,Integer>>(dices.entrySet());

		Collections.sort(tmpl,
			new Comparator<Entry<String,Integer>>() {
				public int compare(Entry<String,Integer> e1, Entry<String,Integer> e2) {
					return e1.getValue().compareTo(e2.getValue());
				}
			}
		);
		
		tmpplayers.putAll(this.players);
		this.players.clear();
		
		for (Entry<String,Integer> tmpe : tmpl) {
			String player = tmpe.getKey();
			this.players.put(player, tmpplayers.get(player));
		}
		
		tmpplayers.clear();
	}
	
	public <T> Map<String,T> multicastInvoke(Method m) {
		BoardController cGameBoard;
		Map <String,T> retMap = new Hashtable<String,T>();
		T retVal;
		
		for (String user : this.players.keySet()) {
			cGameBoard = (BoardController) this.players.get(user);
			
			try {
				 retVal = (T) m.invoke(cGameBoard);
				 retMap.put(user, retVal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return retMap;
	}
}
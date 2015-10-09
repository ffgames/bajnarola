/*****************************************************************************/
/* Board implementation interface                                            */
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

package org.bajnarola.game.controller;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;

public interface BoardController extends Remote {
	public int getTurn() throws RemoteException;
	public void setTurn(int turn) throws RemoteException;
	public ArrayList<Tile> getScenario() throws RemoteException;
	public ArrayList<Tile> getDeck() throws RemoteException;
	public ArrayList<Player> getPlayers() throws RemoteException;
	
	public Boolean probe(int x, int y, int tile) throws RemoteException;
	public Boolean probeMeeple(int x, int y, int tile, Meeple meeple) throws RemoteException;
	public Boolean place(int x, int y, int tile, Meeple meeple) throws RemoteException;
}


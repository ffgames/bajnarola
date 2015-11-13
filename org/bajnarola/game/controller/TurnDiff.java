package org.bajnarola.game.controller;

import java.io.Serializable;

public class TurnDiff implements Serializable {
	private static final long serialVersionUID = 4189613358426938920L;
	/* If meepleTileSide == -1 no meeple is deployed */
	public short x, y, tileDirection, meepleTileSide, turn;
	public String playerName;
	
	public TurnDiff(short x, short y, short tileDirection, 
	                short meepleTileSide, String playerName, short turn) {
		this.x = x;
		this.y = y;
		this.tileDirection = tileDirection;
		this.meepleTileSide = meepleTileSide;
		this.playerName = playerName;
		this.turn = turn;
	}
}

package org.bajnarola.game.controller;

public class TurnDiff {
	/* If meepleTileSide == -1 no meeple is deployed */
	public short x, y, tileDirection, meepleTileSide;
	public String playerName;
	
	public TurnDiff(short x, short y, short tileDirection, 
	                short meepleTileSide, String playerName) {
		this.x = x;
		this.y = y;
		this.tileDirection = tileDirection;
		this.meepleTileSide = meepleTileSide;
		this.playerName = playerName;
	}
}

package org.bajnarola.game.controller;

public class TurnDiff {
	public short x, y, tileDirection;
	public short meepleTileSide;
	
	public TurnDiff(short x, short y, short tileDirection, short meepleTileSide) {
		this.x = x;
		this.y = y;
		this.tileDirection = tileDirection;
		this.meepleTileSide = meepleTileSide;
	}
}

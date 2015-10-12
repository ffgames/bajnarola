package org.bajnarola.game.model;

public class Meeple {

	short tilePos;
	Tile tile;
	Player owner;
	
	public Meeple(Player owner) {
		this.owner = owner;
		this.tile = null;
		this.tilePos = -1;
	}

	public short getTilePos() {
		return tilePos;
	}

	public void setTilePos(short tilePos) {
		this.tilePos = tilePos;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public Player getOwner() {
		return owner;
	}
	
	
}

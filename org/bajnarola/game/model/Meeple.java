package org.bajnarola.game.model;

public class Meeple {

	short tileSide;
	Tile tile;
	Player owner;
	
	public Meeple(Player owner) {
		this.owner = owner;
		this.tile = null;
		this.tileSide = -1;
	}

	public short getTileSide() {
		return tileSide;
	}

	public void setTileSide(short tilePos) {
		this.tileSide = tilePos;
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

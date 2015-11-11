package org.bajnarola.game.model;


public class Cloister extends LandscapeElement {
	
	public Cloister(Tile elementRoot, short tileSide) {
		super(elementRoot, tileSide);
	}

	@Override
	public void merge(LandscapeElement el) {
		/* NOP */
	}

	@Override
	public void addTile(Tile t, short tileSide) {
		addTileInt(t);
		
		if (tiles.size() >= 9)
			complete();
	}

	@Override
	public int getValue(boolean endGame) {
		return tiles.size();
	}
	
}

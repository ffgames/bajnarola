package org.bajnarola.game.model;


public class Cloister extends LandscapeElement {
	
	public Cloister(Tile elementRoot, short tileSide) {
		super(elementRoot, tileSide);
		updateValue((short)1);
		
	}

	@Override
	public void merge(LandscapeElement el) {
		/* NOP */
	}

	@Override
	public void addTile(Tile t, short tileSide) {
		tiles.add(t);
		
		updateValue((short)1);
		
		if (tiles.size() >= 9)
			complete();
	}

	@Override
	public short getValue(boolean endGame) {
		return value;
	}
	
}

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
		t.getLandscapes().put((int)tileSide, this);
		updateValue((short)1);
		
		if (tiles.size() >= 9)
			complete();
	}
	
}

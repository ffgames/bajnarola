package org.bajnarola.game.model;


public class Cloister extends LandscapeElement {
	
	public Cloister(Tile elementRoot) {
		super(elementRoot);
		updateValue((short)1);
		
	}

	@Override
	public void merge(LandscapeElement el) {
		/* NOP */
	}

	@Override
	public void addTile(Tile t) {
		tiles.add(t);
		t.getLandscapes().add(this);
		updateValue((short)1);
		
		if (tiles.size() >= 9)
			complete();
	}
	
}

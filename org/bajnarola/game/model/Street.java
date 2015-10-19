package org.bajnarola.game.model;

public class Street extends LandscapeElement {

	int streetEnds;
	
	public Street(Tile elementRoot, short tileSide) {
		super(elementRoot, tileSide);
		
		if (elementRoot.countElement(Tile.ELTYPE_STREET) != 2)
			streetEnds = 1;
		else 
			streetEnds = 0;
		
		updateValue((short)1);
	}
	
	
	public int getStreetEnds() {
		return streetEnds;
	}
	
	/* Merge a given landscape with this one.
	 * "Delete" the first one moving all its 
	 * references to the current one. */
	@Override
	public void merge(LandscapeElement el) {
		Street st = (Street)el;		
		relink(el);
		
		streetEnds += st.getStreetEnds();
		if (streetEnds >= 2)
			this.complete();
		
		updateValue(st.getValue());
	}

	@Override
	public void addTile(Tile t, short tileSide) {
		tiles.add(t);
		
		/* The new tile points to the current landscape */
		t.putLSElement(tileSide, this);
		
		if (t.countElement(Tile.ELTYPE_STREET) != 2)
			streetEnds++;
		
		if (streetEnds >= 2)
			this.complete();
		
		updateValue((short)1);
	}

}

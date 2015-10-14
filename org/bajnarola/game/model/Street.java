package org.bajnarola.game.model;

public class Street extends LandscapeElement {

	int streetEnds;
	
	public Street(Tile elementRoot) {
		super(elementRoot);
		
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
	public void addTile(Tile t) {
		tiles.add(t);
		if (t.getMeeple() != null)
			owners.put(t.getMeeple().getOwner(), 1);
		
		t.getLandscapes().add(this);
		
		if (t.countElement(Tile.ELTYPE_STREET) != 2)
			streetEnds++;
		
		if (streetEnds >= 2)
			this.complete();
		
		/* The new tail points to the current landscape */
		t.getLandscapes().add(this);
		
		updateValue((short)1);
	}

}

package org.bajnarola.game.model;

import java.util.ArrayList;
import java.util.List;

public class Street extends LandscapeElement {

	List<Tile> tiles;
	int streetEnds;
	
	public Street(Tile elementRoot) {
		super(elementRoot);
		tiles = new ArrayList<Tile>();
		tiles.add(elementRoot);
		if (elementRoot.countElement(Tile.ELEMENT_TYPE_STREET) != 2)
			streetEnds = 1;
		else 
			streetEnds = 0;
	}
	
	public List<Tile> getTiles() {
		return this.tiles;
	}
	
	public int getStreetEnds() {
		return streetEnds;
	}
	
	@Override
	public void merge(LandscapeElement el) {
		Street st = (Street)el;
		tiles.addAll(st.getTiles());
		List<Tile> elTiles  = st.getTiles();
		Tile tileTmp;
		while (!elTiles.isEmpty()) {
			tileTmp = elTiles.remove(0);
			tileTmp.getLandscape().remove(st);
			
			/* All the tails point to the new landscape */
			tileTmp.getLandscape().add(this);
			tiles.add(tileTmp);
		}
		
		streetEnds += st.getStreetEnds();
		if (streetEnds >= 2)
			this.complete();
	}

	@Override
	public void addTile(Tile t) {
		tiles.add(t);
		if (t.countElement(Tile.ELEMENT_TYPE_STREET) != 2)
			streetEnds++;
		
		if (streetEnds >= 2)
			this.complete();
		
		/* The new tail points to the current landscape */
		t.getLandscape().add(this);
	}

	@Override
	public void updateScores() {
		// TODO Auto-generated method stub
		
	}

}

package org.bajnarola.game.model;

public class City extends LandscapeElement {

	short openSides;
	
	public City(Tile elementRoot, short tileSide) {
		super(elementRoot, tileSide);
		
		openSides = calcOpenSides(elementRoot);
	}

	public short getOpenSides() {
		return this.openSides;
	}
	
	private short calcOpenSides(Tile tile) {
		short citySides = tile.countElement(Tile.ELTYPE_CITY);
		
		if (citySides < 2)
			return (short)1;
		
		return (short)(citySides - 1);
	}
	
	@Override
	public void merge(LandscapeElement el) {
		if (((Object)el).equals((Object)this)) {
			openSides -= 2;
		} else {
			openSides += ((City)el).getOpenSides() - 2;
			relink(el);
		}
		
		if (openSides <= 0)
			complete();
	}

	@Override
	public void addTile(Tile t, short tileSide) {
		addTileInt(t);
		
		t.putLSElement(tileSide, this);
		short tileOpenSides = calcOpenSides(t);
		
		openSides += tileOpenSides - 2;
		
		if (openSides <= 0)
			complete();
	}

	private int getCityScore(){
		int score = 0;
		for(Tile t : tiles){
			score += 1;
			if(t.hasPennant())
				score +=1;
		}
		return score;
	}
	
	@Override
	public int getValue(boolean endGame) {
		if(endGame)
			return getCityScore();
		return getCityScore()*2;
	}


}

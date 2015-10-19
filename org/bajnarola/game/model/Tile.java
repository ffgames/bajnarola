package org.bajnarola.game.model;

import java.util.Hashtable;
import java.util.Map;

public class Tile {
	
	public static final short ELTYPE_GRASS = 0;
	public static final short ELTYPE_CITY = 1;
	public static final short ELTYPE_STREET = 2;
	public static final short ELTYPE_CLOISTER = 3;
	
	public static final short SIDE_COUNT = 5;
	public static final short SIDE_TOP = 0;
	public static final short SIDE_RIGHT = 1;
	public static final short SIDE_BOTTOM = 2;
	public static final short SIDE_LEFT = 3;
	public static final short SIDE_CENTER = 4;


	
	/* Internal elements of a tile, represented as an array. */
	short elements[];
	short x, y;
	int direction;
	Meeple meeple;
	boolean pennant;
	String name;
	Map<Integer, LandscapeElement> landscapes;
	
	public Tile(short center, short top, short right, 
	            short bottom, short left, boolean pennant, String name) {
		this.elements = new short[SIDE_COUNT];
		
		/* XXX: CENTER MUST BE SET FOR CITY AND CLOISTER ONLY */
		this.elements[SIDE_CENTER] = center;
		this.elements[SIDE_TOP] = top;
		this.elements[SIDE_RIGHT] = right;
		this.elements[SIDE_BOTTOM] = bottom;
		this.elements[SIDE_LEFT] = left;
		this.direction = 0;
		this.meeple = null;
		this.pennant = pennant;
		this.landscapes = new Hashtable<Integer, LandscapeElement>();
		this.name = name;
		x = y = -1;
	}
	
	public Map<Integer,LandscapeElement> getLandscapes() {
		return landscapes;
	}
	
	public Meeple getMeeple() {
		return meeple;
	}

	public String getName() {
		return name;
	}
	
	public void setMeeple(Meeple meeple) {
		this.landscapes.get(meeple.getTileSide()).addMeeple(meeple);
		this.meeple = meeple;
	}

	public boolean hasPennant() {
		return this.pennant;
	}
	
	public void removeMeeple() {
		meeple.getOwner().getHand().add(meeple);
		meeple = null;
	}
	
	public void rotate(Boolean clockwise) {
		if (clockwise) {
			direction = (direction + 1) % 4; 
		} else {
			direction = (direction - 1) % 4;
		}
	}
	
	/* Return the internal elements of a tile, 
	 * rotated according to the direction */
	public short[] getElements() {
		short rotated[] = new short[5];
		
		rotated[SIDE_TOP] = elements[(SIDE_TOP + direction) % 4];
		rotated[SIDE_RIGHT] = elements[(SIDE_RIGHT + direction) % 4];
		rotated[SIDE_BOTTOM] = elements[(SIDE_BOTTOM + direction) % 4];
		rotated[SIDE_LEFT] = elements[(SIDE_LEFT + direction) % 4];
		rotated[SIDE_CENTER] = elements[SIDE_CENTER];

		return rotated;
	}
	
	/* Return the number of elements of type elType
	 * that are on this tile. */
	public short countElement(short elType) {
		short n = 0;
		
		for (int i = 0; i < elements.length ; i++) {
			if (elements[i] == elType)
				n++;
		}
		
		return n;
	}
	
	/* Check if this Tile can be attached
	 * to the passed tile on the given side */
	public Boolean compatible(Tile tile, short side) {
		if (elements[side] == tile.getElements()[(side + 2) % 4])
			return true;
		
		return false;
	}
	
	public void setCoordinates(short x, short y){
		this.x = x;
		this.y = y;
	}

	public short getX(){
		return x;
	}
	
	public short getY(){
		return y;
	}
}

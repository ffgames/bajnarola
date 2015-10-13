package org.bajnarola.game.model;

import java.util.ArrayList;
import java.util.List;

public class Tile {
	
	public static final short ELEMENT_TYPE_GRASS = 0;
	public static final short ELEMENT_TYPE_CITY = 1;
	public static final short ELEMENT_TYPE_STREET = 2;
	public static final short ELEMENT_TYPE_CLOISTER = 3;
	
	public static final short ELEMENT_POS_TOP = 0;
	public static final short ELEMENT_POS_RIGHT = 1;
	public static final short ELEMENT_POS_BOTTOM = 2;
	public static final short ELEMENT_POS_LEFT = 3;
	public static final short ELEMENT_POS_CENTER = 4;


	
	/* Internal elements of a tile, represented as an array. */
	short elements[];
	short x, y;
	int direction;
	Meeple meeple;
	Boolean pennant;
	List<LandscapeElement> landscapes;
	/* TODO: 
	 * - char flags? */

	public Tile(short center, short top, short right, short bottom, short left, Boolean pennant) {
		this.elements = new short[5];
		this.elements[ELEMENT_POS_CENTER] = center;
		this.elements[ELEMENT_POS_TOP] = top;
		this.elements[ELEMENT_POS_RIGHT] = right;
		this.elements[ELEMENT_POS_BOTTOM] = bottom;
		this.elements[ELEMENT_POS_LEFT] = left;
		this.direction = 0;
		this.meeple = null;
		this.pennant = pennant;
		this.landscapes = new ArrayList<LandscapeElement>();
		x = y = -1;
	}
	
	public List<LandscapeElement> getLandscape() {
		return landscapes;
	}
	
	public Meeple getMeeple() {
		return meeple;
	}

	public void setMeeple(Meeple meeple) {
		this.meeple = meeple;
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
	
	/* Return the interal elements of a tile, rotated according to the direction */
	public short[] getElements() {
		short rotatedElements[] = new short[5];
		
		rotatedElements[ELEMENT_POS_TOP] = this.elements[(ELEMENT_POS_TOP + direction) % 4];
		rotatedElements[ELEMENT_POS_RIGHT] = this.elements[(ELEMENT_POS_RIGHT + direction) % 4];
		rotatedElements[ELEMENT_POS_BOTTOM] = this.elements[(ELEMENT_POS_BOTTOM + direction) % 4];
		rotatedElements[ELEMENT_POS_LEFT] = this.elements[(ELEMENT_POS_LEFT + direction) % 4];
		rotatedElements[ELEMENT_POS_CENTER] = this.elements[ELEMENT_POS_CENTER];

		return elements;
	}
	
	public short countElement(short elType) {
		short n = 0;
		
		for (int i = 0; i < elements.length ; i++) {
			if (elements[i] == elType)
				n++;
		}
		
		return n;
	}
	
	/* Check if this Tile can be attached to the passed tile on the relativePos position */
	public Boolean compatible(Tile tile, short relativePos) {
		if (this.elements[relativePos] == tile.getElements()[(relativePos + 2) % 4])
			return true;
		
		return false;
	}
	
	/* TODO: 
	 * 	- getFlags()?
	*/
}

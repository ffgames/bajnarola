package org.bajnarola.game.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

	static final short PLAYER_N_MEEPLE = 7;
	Meeple meepleList[];
	List<Meeple> hand;
	short score;
	boolean scoreChanged;
	String name;
	
	public Player(String name) {
		meepleList = new Meeple[PLAYER_N_MEEPLE];
		hand = new ArrayList<Meeple>();
				
		
		for (int i = 0; i < meepleList.length ; i++) {
			meepleList[i] = new Meeple(this);
			hand.add(meepleList[i]);
		}
		
		score = 0;
		scoreChanged = false;
		
		this.name = name;
	}
	
	public short getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	public short getUpdateScore() {
		scoreChanged = false;
		return score;
	}

	public void setScore(short score) {
		scoreChanged = true;
		this.score = score;
	}

	public boolean isScoreChanged() {
		return scoreChanged;
	}

	public List<Meeple> getHand() {
		return hand;
	}

	public void removeAllMeeple() {
		Tile t;
		for (int i = 0; i < meepleList.length ; i++) {
			if ((t = meepleList[i].getTile()) != null) {
				t.removeMeeple();
			}
		}
		
		hand.clear();
		
		for (int i = 0; i < meepleList.length ; i++) {
			meepleList[i] = null;
		}
		
		meepleList = null;
	}
}

package org.bajnarola.game.model;

import java.util.ArrayList;

public class Player {

	short PLAYER_N_MEEPLE = 7;
	Meeple meepleList[];
	ArrayList<Meeple> hand;
	short score;
	boolean scoreChanged;
	
	public Player() {
		meepleList = new Meeple[PLAYER_N_MEEPLE];
		
		for (int i = 0; i < meepleList.length ; i++) 
			hand.add(meepleList[i]);
		
		score = 0;
		scoreChanged = false;
	}
	
	public short getScore() {
		return score;
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

	public ArrayList<Meeple> getHand() {
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

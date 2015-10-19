package org.bajnarola.tests;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.model.Board;

public class BoardTest1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		List<String> players = new ArrayList<String>();
		players.add("player1");
		players.add("player2");
		
		
		Board board = new Board(players);
		
		System.out.println("board initialized");
	}

}

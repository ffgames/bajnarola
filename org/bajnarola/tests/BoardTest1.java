package org.bajnarola.tests;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Tile;

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
		
		Tile t = board.drawTile();
		
		System.out.println("tile "+t.toString()+" extracted");
		
		boolean res = board.probe(1, 0, t);
		
		System.out.println("probe(1, 0, tile): "+res);
		
		res = board.probe(0, -1, t);
		
		System.out.println("probe(0, -1, tile): "+res);
	}

}

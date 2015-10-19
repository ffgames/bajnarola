package org.bajnarola.tests;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Tile;

public class BoardTest1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// - 0
		List<String> players = new ArrayList<String>();
		players.add("player1");
		players.add("player2");
		
		
		Board board = new Board(players);
		
		System.out.println("board initialized");
		Debugger.printBoardStats(board);
		
		// - 1
		Tile t = board.drawTile();
		
		System.out.println("tile "+t.toString()+" extracted");
		
		boolean res = board.probe(1, 0, t);
		
		System.out.println("probe(1, 0, tile): "+res);
		
		res = board.probe(0, -1, t);
		
		System.out.println("probe(0, -1, tile): "+res);
		
		board.place(0, -1, t);
		
		Player p1 = board.getPlayers().get(0);
		
		Meeple m = p1.getMeeple();
		
		m.setTileSide(Tile.SIDE_CENTER);
		
		res = board.probeMeeple(t, m);
		
		System.out.println("probeMeeple(tile, meeple): "+res);
		
		board.placeMeeple(t, m);
		
		System.out.println("placed Meeple");
		
		// - 2
		// GSSSG
		t = board.getDeck().remove(62);

		res = board.probe(1, 0, t);
		
		System.out.println("probe(1, 0, tile): "+res);
		
		Player p2 = board.getPlayers().get(1);
		
		m = p2.getMeeple();
		
		board.place(1, 0, t);
		
		m.setTileSide(Tile.SIDE_LEFT);
		
		res = board.probeMeeple(t, m);
		
		System.out.println("probeMeeple(tile, meeple): "+res);

		board.placeMeeple(t, m);
		
		// - 3
		m = p1.getMeeple();
		
		t = board.getDeck().remove(62);
		
		t.rotate(true);
		
		res = board.probe(-1, 0, t);
		
		System.out.println("probe(-1, 0, tile): "+res);
		
		t.rotate(true);
		
		t.rotate(true);
		
		res = board.probe(-1, 0, t);
		
		System.out.println("probe(-1, 0, tile): "+res);
		
		board.place(-1, 0, t);
		
		m.setTileSide(Tile.SIDE_RIGHT);
		
		res = board.probeMeeple(t, m);
		
		System.out.println("probeMeeple(tile, meeple): "+res);
		
		if(res)
			board.placeMeeple(t, m);
		else
			m.getOwner().giveMeepleBack(m);
		
		System.out.println("done??");
	}

}

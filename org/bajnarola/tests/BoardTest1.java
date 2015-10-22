package org.bajnarola.tests;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Cloister;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Street;
import org.bajnarola.game.model.Tile;

public class BoardTest1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("## - 0");
		
		List<String> players = new ArrayList<String>();
		players.add("player1");
		players.add("player2");
		
		
		Board board = new Board();
		
		board.initBoard(players, false, 0);
		
		System.out.println("board initialized");
		Debugger.printBoardStats(board);
		
		System.out.println("## - 1");
		Tile t = board.beginTurn();
		System.out.println("tile drawed");
		Debugger.printBoardStats(board);
		Debugger.printTileStats(t);
		
		boolean res = board.probe(1, 0, t);
		System.out.println("probe(1, 0, tile): "+res);
		
		res = board.probe(0, -1, t);
		System.out.println("probe(0, -1, tile): "+res);
		
		board.place(0, -1, t);
		System.out.println("tile placed on 0, -1");
		Debugger.printBoardStats(board);
		
		Player p1 = board.getPlayers().get(0);
		Meeple m = p1.getMeeple();
		m.setTileSide(Tile.SIDE_CENTER);
		System.out.println("got meeple from player 1 and set side");
		Debugger.printPlayerStats(p1);
		Debugger.printMeepleStats(m);
		
		res = board.probeMeeple(t, m);
		System.out.println("probeMeeple(tile, meeple): "+res);
		
		board.placeMeeple(t, m);
		System.out.println("meeple placed");
		Debugger.printTileStats(t);
		Debugger.printMeepleStats(m);
		Debugger.printCloysterStats((Cloister)t.getLSElement(Tile.SIDE_CENTER));
		
		board.endTurn(t);
		System.out.println("turn ended");
		
		System.out.println("## - 2");
		// GSSSG
		t = board.getDeck().remove(62);
		System.out.println("extracted specific tile");
		Debugger.printTileStats(t);
		
		res = board.probe(1, 0, t);
		System.out.println("probe(1, 0, tile): "+res);
		
		board.place(1, 0, t);
		System.out.println("tile placed on 1, 0");
		Debugger.printBoardStats(board);
		
		Player p2 = board.getPlayers().get(1);
		m = p2.getMeeple();
		m.setTileSide(Tile.SIDE_LEFT);
		System.out.println("got meeple from player 2 and set side");
		Debugger.printPlayerStats(p2);
		Debugger.printMeepleStats(m);
		
		res = board.probeMeeple(t, m);
		System.out.println("probeMeeple(tile, meeple): "+res);

		board.placeMeeple(t, m);
		System.out.println("meeple placed");
		Debugger.printTileStats(t);
		Debugger.printMeepleStats(m);
		Debugger.printStreetStats((Street)t.getLSElement(Tile.SIDE_LEFT));
	
		board.endTurn(t);
		System.out.println("turn ended");
		
		System.out.println("## - 3");
		m = p1.getMeeple();
		m.setTileSide(Tile.SIDE_RIGHT);
		System.out.println("got meeple from player 1 and set side");
		Debugger.printPlayerStats(p1);
		Debugger.printMeepleStats(m);
		
		t = board.getDeck().remove(62);
		System.out.println("extracted specific tile");
		Debugger.printTileStats(t);
		
		t.rotate(true);
		System.out.println("tile rotated clockwise");
		Debugger.printTileStats(t);
		
		res = board.probe(-1, 0, t);
		System.out.println("probe(-1, 0, tile): "+res);
		
		t.rotate(true);
		t.rotate(true);
		System.out.println("tile flipped");
		Debugger.printTileStats(t);
		
		res = board.probe(-1, 0, t);
		System.out.println("probe(-1, 0, tile): "+res);
		
		board.place(-1, 0, t);
		System.out.println("tile placed on -1, 0");
		Debugger.printBoardStats(board);
		
		res = board.probeMeeple(t, m);
		System.out.println("probeMeeple(tile, meeple): "+res);
		
		if(res){
			board.placeMeeple(t, m);
			System.out.println("meeple placed");
			Debugger.printTileStats(t);
			Debugger.printMeepleStats(m);
			Debugger.printStreetStats((Street)t.getLSElement(Tile.SIDE_RIGHT));
		}
		else{
			m.getOwner().giveMeepleBack(m);
			System.out.println("meeple returned to player");
			Debugger.printPlayerStats(p1);
			Debugger.printMeepleStats(m);
		}
		
		board.endTurn(t);
		System.out.println("turn ended");
		System.out.println("player 2 should have his meeple back and score set");
		Debugger.printPlayerStats(p2);
		Debugger.printStreetStats((Street)t.getLSElement(Tile.SIDE_RIGHT));
		
		board.finalCheckScores();
		System.out.println("endgame score checks (pl1 should have 4 points, pl2 3 points)");
		Debugger.printPlayerStats(p1);
		Debugger.printPlayerStats(p2);
		
		System.out.println("## done??");
		
		// holes simple test
		/*for(int i = 0; i < 25; i++){
			board.getDeck().remove(0);
		}
		
		t = board.beginTurn();
		Debugger.printTileStats(t);*/
	}

}

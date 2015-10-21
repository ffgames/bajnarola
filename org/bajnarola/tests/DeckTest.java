package org.bajnarola.tests;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Tile;

public class DeckTest {

	public static void main(String[] args) {
		
		ArrayList<String> players = new ArrayList<String>();
		
		players.add("player 1");
		players.add("player 2");
		players.add("player 3");
		
		Board b1 = new Board();
		Board b2 = new Board();
		Board b3 = new Board();
		Board b4 = new Board();
		
		b1.initBoard(players, 0);
		
		b2.initBoard(players, 42);
		
		b3.initBoard(players, 42);
		
		b4.initBoard(players, 43);
		
		if(compareDecks(b1.getDeck(), b2.getDeck()))
			System.out.println("ERROR: s 0 == s 42");
		

		if(!compareDecks(b3.getDeck(), b2.getDeck()))
			System.out.println("ERROR: s 42 != s 42");
		

		if(compareDecks(b3.getDeck(), b4.getDeck()))
			System.out.println("ERROR: s 42 == s 43");
		
		System.out.printf("\n##################\n");
		
		System.out.println("d1: seed = 0");
		Debugger.printDeck(b1.getDeck());
		
		System.out.println("d2: seed = 42");
		Debugger.printDeck(b2.getDeck());
		
		System.out.println("d3: seed = 42");
		Debugger.printDeck(b3.getDeck());
		
		System.out.println("d4: seed = 43");
		Debugger.printDeck(b4.getDeck());
		
	}
	
	private static boolean compareDecks(List<Tile> d1, List<Tile> d2){
		if(d1.size() != d2.size())
			return false;
		for(int i = d1.size()-1; i >= 0; i--){
			if(!d1.get(i).getName().equals(d2.get(i).getName()))
				return false;
		}
		return true;
	}
}

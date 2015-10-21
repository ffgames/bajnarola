package org.bajnarola.utils;

import java.util.List;

import org.bajnarola.game.model.Tile;

// random implementation based on Parker-Miller algorithm
public class Shuffler {
	int seed;
	
	public Shuffler(int seed){
		this.seed = seed;
	}
	
	public int nextInt(int max){
		int k1;
		int ix = seed;
		k1 = ix/127773;
		ix = 16807*(ix- k1*127773) - k1*2836;
		if(ix < 0){
			ix += 2147483647;
		}
		seed = ix;
		
		return (int)(((double)((double)seed/(double)Integer.MAX_VALUE) * (double)max));
	}
	
	public void shuffleDeck(List<Tile> deck){
		Tile a, b;
		int k;
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < deck.size(); j++){
				k = nextInt(deck.size()-1);
				a = deck.remove(j);
				b = deck.remove(k);
				if(j > deck.size())
					deck.add(b);
				else
					deck.add(j, b);
				if(k > deck.size())
					deck.add(a);
				else
					deck.add(k, a);
			}
	}
}

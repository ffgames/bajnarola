package org.bajnarola.tests;

import java.util.List;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.City;
import org.bajnarola.game.model.Cloister;
import org.bajnarola.game.model.LandscapeElement;
import org.bajnarola.game.model.Meeple;
import org.bajnarola.game.model.Player;
import org.bajnarola.game.model.Street;
import org.bajnarola.game.model.Tile;

public class Debugger {

	public static final void printBoardStats(Board board){
		separator();
		if(board != null)
			System.out.printf("Board:\ndeck:\n\tsize: %d\nplayers:\n\tsize: %d\nscenario:\n\tsize: %d\n", board.getDeck().size(), board.getPlayers().size(), board.getScenario().size());
		else
			System.out.printf("Board:\n\tNULL\n");
		separator();
	}
	
	public static final void printDeck(List<Tile> deck){
		separator();
		System.out.println("Deck:");
		for(Tile t: deck){
			System.out.printf("\t%s\n", t.getName());
		}
		separator();
	}
	
	public static final void printTileStats(Tile tile){
		separator();
		if(tile != null){
			System.out.printf("Tile:\n\tname: %s\n\tcoords: %d, %d\n\tdirection: %d (%s)\n\tmeeple: %s\n",
					tile.getName(), tile.getX(), tile.getY(), tile.getDirection(), stringSide((short)tile.getDirection()),
					(tile.getMeeple() != null ? tile.getMeeple().getOwner().getName() : "null"));
			System.out.printf("\telements:\n\t\t%s\n\t\t%s\n\t\t%s\n\t\t%s\n\t\t%s\n\tpennant: %s\n\tlsCount: %d\n", 
					stringElement(tile.getElements()[Tile.SIDE_TOP]),
					stringElement(tile.getElements()[Tile.SIDE_RIGHT]),
					stringElement(tile.getElements()[Tile.SIDE_BOTTOM]),
					stringElement(tile.getElements()[Tile.SIDE_LEFT]),
					stringElement(tile.getElements()[Tile.SIDE_CENTER]),
					stringBool(tile.hasPennant()), tile.getLSCount());
		} else {
			System.out.printf("Tile:\n\tNULL\n");
		}
		separator();
	}
	
	public static final void printMeepleStats(Meeple meeple){
		separator();
		if(meeple != null)
			System.out.printf("Meeple:\n\towner: %s\n\ttile: %s\n\ttileSide: %s\n", meeple.getOwner().getName(), stringTile(meeple.getTile()), stringSide(meeple.getTileSide()));
		else
			System.out.printf("Meeple:\n\tNULL\n");
		separator();
	}
	
	public static final void printLSElementStats(LandscapeElement element){
		printLSElementStats(element, true);
	}
	
	private static final void printLSElementStats(LandscapeElement element, boolean separe){
		if(separe){
			separator();
			System.out.println("LandscapeElement:");
		}
		if(element != null)
			System.out.printf("\tcompleted: %s\n\townersCount: %d\n\ttilesCount: %d\n\troot: %s\n\tvalue: %d\n", 
				stringBool(element.isCompleted()), element.getScoreOwners().size(), 
				element.getTiles().size(), stringTile(element.getElementRoot()), element.getValue(false));
		else
			System.out.printf("\tNULL\n");
		if(separe)
			separator();
	}
	
	public static final void printStreetStats(Street street){
		separator();
		System.out.println("Street:");
		printLSElementStats(street, false);
		if(street != null)
			System.out.printf("\tstreetEnds: %d\n", street.getStreetEnds());
		separator();
	}
	
	public static final void printCityStats(City city){
		separator();
		System.out.println("City:");
		printLSElementStats(city, false);
		if(city != null)
			System.out.printf("\topenSides: %d\n", city.getOpenSides());
		separator();
	}
	
	public static final void printCloysterStats(Cloister cloyster){
		separator();
		System.out.println("Cloyster:");
		printLSElementStats(cloyster, false);
		separator();
	}
	
	public static final void printPlayerStats(Player player){
		separator();
		if(player != null)
			System.out.printf("Player: %s\n\tscore: %d\n\tmeeples: %s\n\tscoreChanged: "+(player.isScoreChanged() ? "true" : "false")+"\n", player.getName(), player.getScore(), player.getMeepleCount());
		else
			System.out.printf("Player:\n\tNULL\n");
		separator();
	}
	
	private static final String stringBool(boolean val){
		if(val)
			return "TRUE";
		return "FALSE";
	}
	
	private static final String stringTile(Tile t){
		String outs;
		if(t != null)
			outs = t.getX() + ", " + t.getY();
		else
			outs = "null";
		return outs;
	}
	
	private static final String stringSide(short side){
		switch(side){
			case Tile.SIDE_BOTTOM:
				return "BOTTOM";
			case Tile.SIDE_CENTER:
				return "CENTER";
			case Tile.SIDE_LEFT:
				return "LEFT";
			case Tile.SIDE_RIGHT:
				return "RIGHT";
			case Tile.SIDE_TOP:
				return "TOP";
		}
		return "VOID";
	}
	
	private static final String stringElement(short el){
		switch(el){
			case Tile.ELTYPE_CITY:
				return "CITY";
			case Tile.ELTYPE_CLOISTER:
				return "CLOYSTER";
			case Tile.ELTYPE_GRASS:
				return "GRASS";
			case Tile.ELTYPE_STREET:
				return "STREET";
		}
		return "VOID";
	}
	
	public static final void separator(){
		System.out.println("--------------------------------------");
	}
}

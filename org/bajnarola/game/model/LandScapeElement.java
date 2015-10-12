package org.bajnarola.game.model;

import java.util.Hashtable;

public abstract class LandScapeElement {
	Boolean complete, visited;
	Hashtable<Player, Integer> owners;
}

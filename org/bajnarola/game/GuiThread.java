package org.bajnarola.game;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

public class GuiThread implements Runnable {
	
	AppGameContainer gameContainer;
	
	public GuiThread(AppGameContainer gameContainer) {
		this.gameContainer = gameContainer;
	}
	
	@Override
	public void run() {		
		try {
			this.gameContainer.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}

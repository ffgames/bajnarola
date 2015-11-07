package org.bajnarola.game;

import org.bajnarola.game.view.Gui;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

public class GuiThread  extends AppGameContainer implements Runnable{
	
	AppGameContainer gameContainer;
	
	public GuiThread(Gui bajnarolaGui, int resx, int resy, boolean fullscreen) throws SlickException {
		super(bajnarolaGui, resx, resy, fullscreen);
	}
	
	@Override
	public void run() throws RuntimeException {		
		
		try {
			this.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			System.out.println("Exit X");
		}
	}
}

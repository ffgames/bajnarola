package org.bajnarola.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bajnarola.game.view.Gui;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class GuiTest {

	public static void main(String[] args) {
		try{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Gui(null));
			appgc.setDisplayMode(1240, 700, false);
			appgc.start();
		} catch(SlickException ex){
			Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}

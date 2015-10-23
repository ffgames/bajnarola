package org.bajnarola.game.view;

import org.bajnarola.game.controller.ViewController;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;

public class Gui extends BasicGame implements InputProviderListener {
	
	static final String GAMENAME = "Bajnarola";
	static final float SCROLL_AREA_RATEO = (float)0.05;
	
	private InputProvider provider;
	private Input rawInput;
	
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	
	Animator animator;
	
	private enum bg_type {
		BG_TILED,
		BG_CENTERED
	};
	
	private ViewController controller;
	
	private Command selectComm = new BasicCommand("select");
	private Command rotateComm = new BasicCommand("rotate");
	private Command backComm = new BasicCommand("back");
	
	private String message = "Press something..", message2 = "";
	
	private Image boardBackground;
	private bg_type boardBackgroundType;
	
	public Gui(ViewController controller){
		super(GAMENAME);
		this.controller = controller;
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		 provider = new InputProvider(gc.getInput());
		 provider.addListener(this);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_ENTER), selectComm);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_SPACE), rotateComm);
		 		 
		 provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), backComm);
		 
		 rawInput = new Input(gc.getScreenHeight());
		 
		 animator = new Animator();
		 
		 leftBorderX = (int)((float)gc.getWidth() * SCROLL_AREA_RATEO);
		 rightBorderX = gc.getWidth() - leftBorderX;
		 upperBorderY= (int)((float)gc.getHeight() * SCROLL_AREA_RATEO);
		 lowerBorderY = gc.getHeight() - upperBorderY;
		 
		 if(gc.getHeight() > 800){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_1024.jpg");
		 } else if(gc.getHeight() > 500){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_512.jpg");
		 } else
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_256.jpg");
		 boardBackgroundType = bg_type.BG_TILED;
		 //background = new Image("res/Medieval_village.jpg");
		 //backgroundType = bg_type.BG_CENTERED;
	}
	
	private void drawBgTiled(Image background, int screenWidth, int screenHeight, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		int sw;
		int sh = screenHeight;
		ty = 0;
		while(sh > 0){
			sw = screenWidth;
			tx = 0;
			while(sw > 0){
				background.draw(tx, ty);
				tx += backgroundWidth;
				sw -= backgroundWidth;
			}
			ty += backgroundHeight;
			sh -= backgroundHeight;
		}
	}
	
	private void drawBgCentered(Image background, int screenWidth, int screenHeight, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		tx = (screenWidth - backgroundWidth) / 2;
		ty = (screenHeight - backgroundHeight) / 2;
		background.draw(tx, ty);
	}
		
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		switch(boardBackgroundType){
			case BG_TILED:
				drawBgTiled(boardBackground, gc.getWidth(), gc.getHeight(), boardBackground.getWidth(), boardBackground.getHeight());
				break;
			case BG_CENTERED:
				drawBgCentered(boardBackground, gc.getWidth(), gc.getHeight(), boardBackground.getWidth(), boardBackground.getHeight());
				break;
			default:
				break;
		}
		g.drawString(message, 10, 20);
		g.drawString(message2, 10, 50);
		
		animator.step();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		// TODO Auto-generated method stub

	}

	@Override
	public void controlPressed(Command command) {
		if(command.equals(selectComm)){
			selectAct(false);
		} else if(command.equals(rotateComm)){
			if(rawInput.isKeyDown(Input.KEY_LSHIFT) || rawInput.isKeyDown(Input.KEY_RSHIFT))
				rotateAct(false);
			else
				rotateAct(true);
		} else if(command.equals(backComm)){
			backAct();
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		if(button == 0)
			selectAct(true);
		else if(button == 1)
			backAct();
		message += " x: "+x+", y: "+y;
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		super.mouseWheelMoved(change);
		if(change > 0)
			rotateAct(true);
		else if(change < 0)
			rotateAct(false);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy){
		message2 = "";
		if(newx < leftBorderX){
			message2 += "Left ";
		} else if (newx > rightBorderX){
			message2 += "Right ";
		}
		if(newy < upperBorderY){
			message2 += "Up";
		} else if (newy > lowerBorderY){
			message2 += "Down";
		}
	}
	
	private void selectAct(boolean mouse){
		message = "select "+(mouse ? "mouse" : "keyboard");
	}
	
	private void rotateAct(boolean clockwise){
		message = "rotate "+(clockwise ? "clockwise" : "counter clockwise");
	}

	private void backAct(){
		message = "back key pressed";
	}
	
	@Override
	public void controlReleased(Command command) {
		
	}
}

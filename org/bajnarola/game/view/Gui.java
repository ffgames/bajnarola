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

import sun.misc.GC;

import com.sun.javafx.scene.traversal.SceneTraversalEngine;

public class Gui extends BasicGame implements InputProviderListener {
	
	static final String GAMENAME = "Bajnarola";
	static final float SCROLL_AREA_RATEO = (float)0.05;
	
	private InputProvider provider;
	private Input rawInput;
	
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	private int windowWidth, windowHeight;
	
	Animator animator;
	
	public enum bg_type {
		BG_TILED,
		BG_CENTERED
	};
	
	public enum scene_type {
		SCENE_GAME,
		SCENE_MENU,
		SCENE_PAUSE
	}
	
	public ViewController controller;
	
	private Command rotateComm = new BasicCommand("rotate");
	private Command backComm = new BasicCommand("back");
	private Command escComm = new BasicCommand("esc");
	
	private String message = "Press something..", message2 = "";
	
	private Image boardBackground;
	
	private IScene currentScene;
	private MenuScene menuScene;
	private GameScene gameScene;
	private PauseScene pauseScene;
	
	private GameContainer container;
	
	public Gui(ViewController controller){
		super(GAMENAME);
		this.controller = controller;
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		 provider = new InputProvider(gc.getInput());
		 provider.addListener(this);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_SPACE), rotateComm);
		 		 
		 provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), backComm);
		 
		 provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), escComm);
		 
		 rawInput = new Input(gc.getScreenHeight());
		 
		 animator = new Animator();
		 
		 windowHeight = gc.getHeight();
		 windowWidth = gc.getWidth();
		 
		 leftBorderX = (int)((float)windowWidth * SCROLL_AREA_RATEO);
		 rightBorderX = windowWidth - leftBorderX;
		 upperBorderY= (int)((float)windowHeight * SCROLL_AREA_RATEO);
		 lowerBorderY = windowHeight - upperBorderY;
		 
		 menuScene = new MenuScene(this, new Image("res/backgrounds/Medieval_village.jpg"), bg_type.BG_CENTERED);
		 
		 if(gc.getHeight() > 800){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_1024.jpg");
		 } else if(gc.getHeight() > 500){
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_512.jpg");
		 } else
			 boardBackground = new Image("res/backgrounds/Craggy_Rock_256.jpg");
		 boardBackgroundType = bg_type.BG_TILED;
		 
		 menuBackground =;
		 menuBackgroundType = ;
		 
		 pauseBackground = new Image(windowWidth, windowHeight, Image.FILTER_LINEAR);
		 pauseBackgroundType = bg_type.BG_CENTERED;
		 
		 currentScene = scene_type.SCENE_GAME;
		 
		 container = gc;
	}
	
	public void drawBackground(Image background, bg_type backgroundType){
		switch(backgroundType){
			case BG_TILED:
				drawBgTiled(background, background.getWidth(), background.getHeight());
				break;
			case BG_CENTERED:
				drawBgCentered(background, background.getWidth(), background.getHeight());
				break;
			default:
				break;
		}
	}
	
	private void drawBgTiled(Image background, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		int sw;
		int sh = windowHeight;
		ty = 0;
		while(sh > 0){
			sw = windowWidth;
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
	
	private void drawBgCentered(Image background, int backgroundWidth, int backgroundHeight){
		float tx, ty;
		tx = (windowWidth > backgroundWidth ? (windowWidth - backgroundWidth) / 2 : 0);
		ty = (windowHeight > backgroundHeight ? (windowHeight - backgroundHeight) / 2 : 0);
		background.draw(tx, ty, (windowWidth < backgroundWidth ? windowWidth : backgroundWidth), (windowHeight < backgroundHeight ? windowHeight : backgroundHeight));
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		currentScene.render(gc, g);
		
		if(currentScene.sceneType == scene_type.SCENE_GAME && ((gc.isUpdatingOnlyWhenVisible() && !gc.isPaused()) || !gc.isUpdatingOnlyWhenVisible()))
			animator.step();
		
		g.drawString(message, 10, 20);
		g.drawString(message2, 10, 50);
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		// TODO Auto-generated method stub

	}

	@Override
	public void controlPressed(Command command) {
		if(command.equals(rotateComm)){
			if(rawInput.isKeyDown(Input.KEY_LSHIFT) || rawInput.isKeyDown(Input.KEY_RSHIFT))
				rotateAct(false);
			else
				rotateAct(true);
		} else if(command.equals(backComm)){
			backAct();
		} else if(command.equals(escComm)){
			escAct();
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		if(button == 0)
			selectAct(x, y);
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
		//TODO: check if mouse is still in window space
		message2 = "";
		if(newx < leftBorderX && newx > 2){
			message2 += "Left ";
		} else if (newx > rightBorderX && newx < windowWidth-3){
			message2 += "Right ";
		}
		if(newy < upperBorderY && newy > 2){
			message2 += "Up";
		} else if (newy > lowerBorderY && newy < windowHeight-3){
			message2 += "Down";
		}
	}
	
	private void selectAct(int x, int y){
		message = "select x: "+x+", y: "+y;
		switch(currentScene){
			case SCENE_MENU:
				break;
			case SCENE_PAUSE:
				break;
			case SCENE_GAME:
				break;
		}
	}
	
	private void switchScene(scene_type newScene){
		switch(newScene){
			case SCENE_PAUSE:
				container.getGraphics().copyArea(pauseBackground, 0, 0);
				currentScene = scene_type.SCENE_PAUSE;
				break;
			case SCENE_GAME:
				currentScene = scene_type.SCENE_GAME;
				break;
			case SCENE_MENU:
				currentScene = scene_type.SCENE_MENU;
				break;
		}
	}
	
	private void rotateAct(boolean clockwise){
		message = "rotate "+(clockwise ? "clockwise" : "counter clockwise");
	}

	private void backAct(){
		message = "back key pressed";
	}
	
	private void escAct(){
		message = "esc key pressed";
		if(currentScene == scene_type.SCENE_GAME){
			switchScene(scene_type.SCENE_PAUSE);
		} else if(currentScene == scene_type.SCENE_PAUSE){
			switchScene(scene_type.SCENE_GAME);
		}
	}
	
	@Override
	public void controlReleased(Command command) {
		
	}
}

package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.List;

import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class EndgameScene extends IScene {

	Button exitButton;
	Image container, winBanner, loseBanner, sceneDump;
	float containerWidth, containerHeight;
	float containerPosX, containerPosY;
	float bannerWidth, bannerHeight;
	float bannerPosX, bannerPosY;
	float scoresWidth, scoresHeight;
	float scoresLPosX, scoresRPosX, scoresPosY;
	
	List<String> scores;
	boolean winner;
	String cause;
	
	boolean redraw;
	
	public EndgameScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_ENDGAME;
		
		container = new Image("res/misc/bgbanner.png");
		winBanner = new Image("res/misc/winbanner.png");
		loseBanner = new Image("res/misc/losebanner.png");
		sceneDump = new Image(guiManager.windowWidth, guiManager.windowHeight);

		containerWidth = (float)((float)guiManager.windowWidth *(float) 0.7);
		containerHeight = (float)((float)guiManager.windowHeight * (float)0.8);
		containerPosX = (guiManager.windowWidth/2) - containerWidth/2;
		containerPosY = (guiManager.windowHeight/2) - containerHeight/2 - 50;
		
		bannerWidth = containerWidth;
		bannerHeight = ((float)winBanner.getHeight()/(float)winBanner.getWidth())*bannerWidth;
		bannerPosX = (guiManager.windowWidth/2) - bannerWidth/2;
		bannerPosY = containerPosY + containerHeight - bannerHeight*2/3;
		
		scoresHeight = (bannerPosY - containerPosY) * 0.7f;
		scoresWidth = containerWidth / 3;
		scoresPosY = containerPosY + (containerHeight - scoresHeight)/2;
		scoresLPosX = containerPosX + scoresWidth*2/3;
		scoresRPosX = scoresLPosX + scoresWidth;
		
		exitButton = new Button(guiManager.windowWidth/4,
                guiManager.windowHeight/10,
                guiManager.windowWidth/2,
                guiManager.windowHeight - 75,
                new Image("res/menu/exitInactive.png"),
                new Image("res/menu/exitActive.png"));
		scores = new ArrayList<String>();
		
		redraw = true;
	}

	public void setScores(List<String> scores){
		this.scores = scores;
		redraw = true;
	}
	
	public void setWinner(boolean winner){
		this.winner = winner;
		redraw = true;
	}
	
	public void setCause(String cause){
		this.cause = cause;
		redraw = true;
	}
	
	public void reefresh(){
		redraw = true;
	}
	
	@Override
	public void leftClick(int x, int y) {
		
		if (exitButton.isClicked(x, y))
			guiManager.switchScene(scene_type.SCENE_MENU);
	}

	@Override
	public void rightClick(int x, int y) {
		
	}

	@Override
	public void wheelMoved(boolean up) {
		
	}

	@Override
	public void enterPressed() {
		
	}

	@Override
	public void escPressed() {
		guiManager.switchScene(scene_type.SCENE_MENU);
	}

	@Override
	public void backspacePressed() {

	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		exitButton.isClicked(newx, newy);
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		if(redraw){
			guiManager.drawBackground(background, backgroundType);
			container.draw(containerPosX, containerPosY,
			               containerWidth, containerHeight);
			
			if(winner)
				winBanner.draw(bannerPosX, bannerPosY, bannerWidth, bannerHeight);
			else
				loseBanner.draw(bannerPosX, bannerPosY, bannerWidth, bannerHeight);
			
			if(scores != null){
				int nlines = scores.size()/2 + scores.size()%2;
				for(int i = 0; i < scores.size(); i++){
					guiManager.drawString(scores.get(i), g, (int)(i%2==0? scoresLPosX : scoresRPosX), (int)(i/2*(scoresHeight/nlines)+scoresPosY));
				}
			}
			
			//TODO: draw cause string
			
			guiManager.container.getGraphics().copyArea(sceneDump, 0, 0);
		} else
			sceneDump.draw(0, 0);
		
		exitButton.draw();
	}

	@Override
	public void leftRelease(int x, int y) {
		
	}

	@Override
	public void rightRelease(int x, int y) {
		
	}

	@Override
	public void keyPressed(int key, char c) {
		
	}

	@Override
	public void keyReleased(int key, char c) {
		
	}

}

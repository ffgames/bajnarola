package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GameScene extends IScene {
	
	static final float SCROLL_AREA_RATEO = (float)0.05;
	
	private int globalCenterOffset, tileSize, meepleSize;
	private float scaleFactor;
	private boolean zoomOutView;
	
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	public int xOff, yOff, maxX, maxY;
	private int logicalMaxX, logicalMinX, logicalMaxY, logicalMinY;
	
	private Map<String, GraphicalTile> currentScenario;
	private Map<String, Boolean> currentLanscape;
	private Map<String, GraphicalMeeple> placedMeeples;
	private List<GraphicalMeeple> meeplesToRemove;
	public GraphicalMeeple meepleToPlace;
	public GraphicalTile tileToPlace;
	public boolean probing, probeResult;
	private GraphicalTile probeSquare;
	
	String message2 = "";

	public GameScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_GAME;
		
		leftBorderX = (int)((float)guiManager.windowWidth * SCROLL_AREA_RATEO);
		rightBorderX = guiManager.windowWidth - leftBorderX;
		upperBorderY= (int)((float)guiManager.windowHeight * SCROLL_AREA_RATEO);
		lowerBorderY = guiManager.windowHeight - upperBorderY;
		
		xOff = yOff = 0;
		// TODO: check if tiles have to be resized in "zoom in" mode
		tileSize = GraphicalTile.TILE_SIZE;
		meepleSize = GraphicalMeeple.MEEPLE_SIZE;
		maxX = maxY = GraphicalTile.TILE_SIZE * Board.TOTAL_TILES_COUNT;
		globalCenterOffset = maxX / 2;
		scaleFactor = 1;
		zoomOutView = false;
		
		logicalMaxX = logicalMaxY = logicalMinX = logicalMinY = 0;
		
		currentScenario = new Hashtable<String, GraphicalTile>();
		currentLanscape = null;
		placedMeeples = new Hashtable<String, GraphicalMeeple>();
		meeplesToRemove = new ArrayList<GraphicalMeeple>();
		probeSquare = new GraphicalTile("probe", "0,0", 0, globalCenterOffset, globalCenterOffset, tileSize);
		probing = false;
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
		for(GraphicalTile t : currentScenario.values()){
			if(t.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth)){
				if(currentLanscape != null && currentLanscape.containsKey(t.getCoordinates()))
					guiManager.animator.drawLandscapeGlowingTile(t, zoomOutView, scaleFactor);
				else
					t.draw(zoomOutView, scaleFactor);
			}
		}
		
		for(GraphicalMeeple m : placedMeeples.values()){
			if(m.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
				m.draw(zoomOutView, scaleFactor);
		}
		
		for(GraphicalMeeple m : meeplesToRemove){
			if(m.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
				guiManager.animator.drawMeepleRemoval(m, zoomOutView, scaleFactor);
		}
		
		if(meepleToPlace != null && meepleToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawMeeplePlacement(meepleToPlace, zoomOutView, scaleFactor);
		
		if(tileToPlace != null && tileToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawTilePlacement(tileToPlace, zoomOutView, scaleFactor);
		
		if(probing && probeSquare.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawTileProbe(probeSquare, zoomOutView, scaleFactor, probeResult);
		
		g.drawString(message2, 10, 50);
	}

	//returns true if at least one meeple has been removed
	public boolean setCurrentLandscape(Map<String, Boolean> ls){
		currentLanscape = ls;
		Boolean val;
		boolean ret = false;
		for(GraphicalMeeple m : placedMeeples.values()){
			val = ls.get(m.coords);
			if(val != null && val == false){
				ret = true;
				placedMeeples.remove(m.coords);
				meeplesToRemove.add(m);
			}
		}
		return ret;
	}
	
	public void meeplesRemoved(){
		meeplesToRemove.clear();
	}
	
	public void tilePlaced(){
		currentScenario.put(tileToPlace.coords, tileToPlace);
		tileToPlace = null;
	}
	
	public void meeplePlaced(){
		placedMeeples.put(meepleToPlace.coords, meepleToPlace);
		meepleToPlace = null;
	}
	
	public void probed(){
		probing = false;
	}
	
	public boolean placeGraphicalTile(Tile tile, String coords) throws SlickException{
		int lx = getLogicalX(coords);
		int ly = getLogicalY(coords);
		tileToPlace = new GraphicalTile(tile.getName(), coords, tile.getDirection(), getGlobalCoord(lx), getGlobalCoord(ly), tileSize);
		
		setViewScaleValues(lx, ly);
		
		if(tile.hasMeeple()){
			meepleToPlace = new GraphicalMeeple(tile.getMeeple().getOwner().getId(), coords, getGlobalCoord(lx), getGlobalCoord(ly), meepleSize);
			return true;
		}
		return false;
	}
	
	private void setViewScaleValues(int lx, int ly){
		if(lx < logicalMinX)
			logicalMinX = lx;
		if(lx > logicalMaxX)
			logicalMaxX = lx;
		if(ly < logicalMinY)
			logicalMinY = ly;
		if(ly > logicalMaxY)
			logicalMaxY = ly;
		
		int tw, th;
		tw = (logicalMaxX - logicalMinX) * tileSize;
		th = (logicalMaxY - logicalMinY) * tileSize;
		
		if(tw <= guiManager.windowWidth && th <= guiManager.windowHeight)
			scaleFactor = 1;
		else {
			float hscale, vscale;
			
			hscale = guiManager.windowHeight / th;
			vscale = guiManager.windowWidth / tw;
		
			scaleFactor = (hscale < vscale ? hscale : vscale);
		}
	}
	
	@Override
	public void leftClick(int x, int y) {
		// TODO Auto-generated method stub
		// if clicked on an empty square probe and set probe tile coords and animation
	}

	@Override
	public void rightClick(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wheelMoved(boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void escPressed() {
		guiManager.switchScene(scene_type.SCENE_PAUSE);
	}

	@Override
	public void backspacePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		message2 = "";
		if(newx < leftBorderX && newx > 2){
			message2 += "Left ";
		} else if (newx > rightBorderX && newx < guiManager.windowWidth-3){
			message2 += "Right ";
		}
		if(newy < upperBorderY && newy > 2){
			message2 += "Up";
		} else if (newy > lowerBorderY && newy < guiManager.windowHeight-3){
			message2 += "Down";
		}
	}
	
	private static final int getLogicalX(String coords){
		return Integer.parseInt(coords.split(";")[0]);
	}
	
	private static final int getLogicalY(String coords){
		return Integer.parseInt(coords.split(";")[1]);
	}
	
	private int getGlobalCoord(int lc){
		return globalCenterOffset + (lc * tileSize);
	}
}

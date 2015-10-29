package org.bajnarola.game.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.bajnarola.game.model.Board;
import org.bajnarola.game.model.Tile;
import org.bajnarola.game.view.Gui.bg_type;
import org.bajnarola.game.view.Gui.scene_type;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class GameScene extends IScene {
	
	static final float SCROLL_AREA_RATEO = (float)0.05;
	
	private int globalCenterOffset, tileSize, meepleSize;
	private float scaleFactor;
	private boolean zoomOutView, zoomable;
	
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	public int xOff, yOff, maxX, maxY;
	private int logicalMaxX, logicalMinX, logicalMaxY, logicalMinY;
	
	private Map<String, GraphicalTile> currentScenario;
	private Map<String, Boolean> currentLanscape;
	private Map<String, GraphicalMeeple> placedMeeples;
	private List<GraphicalMeeple> meeplesToRemove;
	public GraphicalMeeple meepleToPlace;
	public GraphicalTile tileToPlace, turnTile;
	public boolean probing, probeResult, mouseOverOn;
	private GraphicalTile probeSquare, holeOver;
	private Button zoomButton;
	private List<HitBox> holes;

	public GameScene(Gui guiManager, Image background, bg_type backgroundType) throws SlickException {
		super(guiManager, background, backgroundType);
		sceneType = scene_type.SCENE_GAME;
		int minWindowSize = (guiManager.windowHeight < guiManager.windowWidth ? guiManager.windowHeight : guiManager.windowWidth); 
		
		leftBorderX = (int)((float)guiManager.windowWidth * SCROLL_AREA_RATEO);
		rightBorderX = guiManager.windowWidth - leftBorderX;
		upperBorderY= (int)((float)guiManager.windowHeight * SCROLL_AREA_RATEO);
		lowerBorderY = guiManager.windowHeight - upperBorderY;
		
		tileSize = minWindowSize / 8;
		tileSize = (tileSize < GraphicalTile.TILE_SIZE ? tileSize : GraphicalTile.TILE_SIZE);
		meepleSize = GraphicalMeeple.MEEPLE_SIZE * (tileSize / GraphicalTile.TILE_SIZE);
		maxX = maxY = GraphicalTile.TILE_SIZE * (Board.TOTAL_TILES_COUNT * 2);
		globalCenterOffset = maxX / 2;
		scaleFactor = 1;
		zoomOutView = zoomable = false;
		xOff = globalCenterOffset - (guiManager.windowWidth/2);
		yOff = globalCenterOffset - (guiManager.windowHeight/2);
		
		zoomButton = new Button(minWindowSize/10, minWindowSize/10, minWindowSize/10, guiManager.windowHeight-(minWindowSize/10), new Image("res/misc/zoomIn.png"), new Image("res/misc/zoomOut.png"));
		
		logicalMaxX = logicalMaxY = logicalMinX = logicalMinY = 0;
		
		currentScenario = new Hashtable<String, GraphicalTile>();
		currentLanscape = null;
		placedMeeples = new Hashtable<String, GraphicalMeeple>();
		meeplesToRemove = new ArrayList<GraphicalMeeple>();
		probeSquare = new GraphicalTile(this, "probe", "0;0", 0, globalCenterOffset, globalCenterOffset, tileSize);
		holeOver = new GraphicalTile(this, "holeOver", "0;0", 0, globalCenterOffset, globalCenterOffset, tileSize);
		probing = mouseOverOn = false;
		tileToPlace = turnTile = null;
		holes = new ArrayList<HitBox>();
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
		
		if(mouseOverOn && holeOver.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			holeOver.draw(zoomOutView, scaleFactor);
		
		if(probing && probeSquare.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawTileProbe(probeSquare, zoomOutView, scaleFactor, probeResult);
		
		if(tileToPlace != null && tileToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawTilePlacement(tileToPlace, zoomOutView, scaleFactor);
		
		if(meepleToPlace != null && meepleToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawMeeplePlacement(meepleToPlace, zoomOutView, scaleFactor);
		
		if(turnTile != null){
			turnTile.draw();
		}
		
		if(zoomable){
			zoomButton.draw();
		}
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
		if(meepleToPlace != null){
			placedMeeples.put(meepleToPlace.coords, meepleToPlace);
			meepleToPlace = null;
		}
	}
	
	public void landscapesSet(){
		currentLanscape = null;
	}
	
	public void probed(){
		probing = false;
	}
	
	public boolean placeGraphicalTile(Tile tile, String coords) throws SlickException{
		int lx = getLogicalX(coords);
		int ly = getLogicalY(coords);
		tileToPlace = new GraphicalTile(this, tile.getName(), coords, tile.getDirection(), getGlobalCoord(lx), getGlobalCoord(ly), tileSize);
		
		setViewScaleValues(lx, ly);
		
		if(tile.hasMeeple()){
			meepleToPlace = new GraphicalMeeple(this, tile.getMeeple().getOwner().getId(), coords, getGlobalCoord(lx), getGlobalCoord(ly), meepleSize);
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
		
		if(tw <= guiManager.windowWidth && th <= guiManager.windowHeight){
			scaleFactor = 1;
			//zoomable = false; should be implicit
		}
		else {
			float hscale, vscale;
			
			hscale = guiManager.windowHeight / th;
			vscale = guiManager.windowWidth / tw;
		
			scaleFactor = (hscale < vscale ? hscale : vscale);
			
			zoomable = true;
		}
	}
	
	public void beginTurn(List<String> holes, Tile newTile) throws SlickException{
		turnTile = new GraphicalTile(this, newTile.getName(), "", newTile.getDirection(), guiManager.windowWidth -(tileSize + 5), guiManager.windowHeight - (tileSize + 5), tileSize*2);
		this.holes.clear();
		int cx, cy;
		for(String h : holes){
			cx = getGlobalCoord(getLogicalX(h));
			cy = getGlobalCoord(getLogicalY(h));
			this.holes.add(new HitBox(cx-tileSize/2, cy-tileSize/2, cx+tileSize/2, cy+tileSize/2));
		}
	}
	
	@Override
	public void leftClick(int x, int y) {
		if(zoomable && zoomButton.isClicked(x, y)){
			if(zoomOutView)
				zoomButton.deactivate();
			else
				zoomButton.activate();
			zoomOutView = !zoomOutView;
		}
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
		if(newx < leftBorderX && newx > 2){
		
		} else if (newx > rightBorderX && newx < guiManager.windowWidth-3){
		
		}
		if(newy < upperBorderY && newy > 2){
		
		} else if (newy > lowerBorderY && newy < guiManager.windowHeight-3){
		
		}
		
		mouseOverOn = false;
		if(!holes.isEmpty()){
			for(HitBox h : holes){
				if(h.hits(newx, newy, xOff, yOff)){
					holeOver.setCoordinates(h);
					mouseOverOn = true;
				}
			}
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

	@Override
	public void leftRelease(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightRelease(int x, int y) {
		// TODO Auto-generated method stub
		
	}
}

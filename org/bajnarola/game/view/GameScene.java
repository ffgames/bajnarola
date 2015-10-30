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

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

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
	private int turnTileCx, turnTileCy, turnTileSize;
	public boolean probing, probeResult, mouseOverOn, dimscreen;
	private GraphicalTile probeSquare, holeOver;
	private GraphicalElement probingTile;
	private Button zoomButton, confirmButton;
	private List<HitBox> holes;
	private int probedX, probedY;
	private Image curtain;
	
	private boolean possibleMeeples[];
	private GraphicalMeeple tmpMeeples[];

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
		confirmButton = new Button(minWindowSize/4, minWindowSize/10, guiManager.windowWidth/2, guiManager.windowHeight-(minWindowSize/20),
				 new Image("res/menu/confirmInactive.png"), new Image("res/menu/confirmActive.png"), new Image("res/menu/confirmDisabled.png"));
		confirmButton.deactivate();
		
		curtain = new Image("res/misc/gray.png");
		curtain.setAlpha(0.8f);
		
		logicalMaxX = logicalMaxY = logicalMinX = logicalMinY = 0;
		
		currentScenario = new Hashtable<String, GraphicalTile>();
		currentLanscape = null;
		placedMeeples = new Hashtable<String, GraphicalMeeple>();
		meeplesToRemove = new ArrayList<GraphicalMeeple>();
		probeSquare = new GraphicalTile(this, "probe", "0;0", 0, globalCenterOffset, globalCenterOffset, tileSize);
		holeOver = new GraphicalTile(this, "holeOver", "0;0", 0, globalCenterOffset, globalCenterOffset, tileSize);
		probing = mouseOverOn = dimscreen = false;
		probedX = probedY = 0;
		tileToPlace = turnTile = null;
		holes = new ArrayList<HitBox>();
		turnTileCx = guiManager.windowWidth -tileSize;
		turnTileCy = guiManager.windowHeight - tileSize;
		turnTileSize = tileSize * 2;
		
		tmpMeeples = new GraphicalMeeple[Tile.SIDE_COUNT];
		tmpMeeples[0] = new GraphicalMeeple(this, 42, "", turnTileCx, turnTileCy-(turnTileSize/3), turnTileSize/4);
		tmpMeeples[1] = new GraphicalMeeple(this, 42, "", turnTileCx+(turnTileSize/3), turnTileCy, turnTileSize/4);
		tmpMeeples[2] = new GraphicalMeeple(this, 42, "", turnTileCx, turnTileCy+(turnTileSize/3), turnTileSize/4);
		tmpMeeples[3] = new GraphicalMeeple(this, 42, "", turnTileCx-(turnTileSize/3), turnTileCy, turnTileSize/4);
		tmpMeeples[4] = new GraphicalMeeple(this, 42, "", turnTileCx, turnTileCy, turnTileSize/4);
	}

	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
		//Board render
		
		//Static scenario elements
		
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
		
		//User interactions
		
		if(mouseOverOn && holeOver.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			holeOver.draw(zoomOutView, scaleFactor);
		
		if(probing && probeSquare.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth)){
			if(guiManager.animator.isTileProbeGlowOn())
				guiManager.animator.drawTileProbe(probeSquare, zoomOutView, scaleFactor, probeResult);
			else
				probing = false;
		}
		
		if(probingTile != null && probingTile.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			probingTile.draw(zoomOutView, scaleFactor);
		
		//Animations
		
		for(GraphicalMeeple m : meeplesToRemove){
			if(m.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
				guiManager.animator.drawMeepleRemoval(m, zoomOutView, scaleFactor);
		}
		
		if(tileToPlace != null && tileToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawTilePlacement(tileToPlace, zoomOutView, scaleFactor);
		
		if(meepleToPlace != null && meepleToPlace.isInView(xOff, yOff, guiManager.windowWidth, guiManager.windowWidth))
			guiManager.animator.drawMeeplePlacement(meepleToPlace, zoomOutView, scaleFactor);
		
		//Board render ends here
		
		//HUD render
		
		if(!dimscreen){
			if(zoomable)
				zoomButton.draw();
			
			if(turnTile != null)
				turnTile.drawAbsolute();
			
		} else {
			//tile placed
			curtain.draw(0, 0, guiManager.windowWidth, guiManager.windowHeight);
			
			if(turnTile != null)
				turnTile.drawAbsolute();			

			for(int i = 0; i < Tile.SIDE_COUNT; i++)
				if(possibleMeeples[i])
					tmpMeeples[i].drawAbsolute();
		}
		
		if(turnTile != null)
			confirmButton.draw();
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
	
	public boolean placeGraphicalTile(Tile tile, String coords) throws SlickException{
		int lx = getLogicalX(coords);
		int ly = getLogicalY(coords);
		tileToPlace = new GraphicalTile(this, tile.getName(), coords, tile.getDirection(), getGlobalCoordX(lx), getGlobalCoordY(ly), tileSize);
		
		setViewScaleValues(lx, ly);
		
		if(tile.hasMeeple()){
			meepleToPlace = new GraphicalMeeple(this, tile.getMeeple().getOwner().getId(), coords, getGlobalCoordX(lx), getGlobalCoordY(ly), meepleSize);
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
		turnTile = new GraphicalTile(this, newTile.getName(), "", newTile.getDirection(), turnTileCx, turnTileCy, turnTileSize);
		this.holes.clear();
		int cx, cy;
		for(String h : holes){
			cx = getGlobalCoordX(getLogicalX(h));
			cy = getGlobalCoordY(getLogicalY(h));
			this.holes.add(new HitBox(cx-tileSize/2, cy-tileSize/2, cx+tileSize/2, cy+tileSize/2));
		}
		probedX = probedY = 0;
		confirmButton.disable();
	}
	
	public void endTurn(){
		turnTile = null;
		confirmButton.disable();
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
		if(mouseOverOn && !dimscreen){
			probing = true;
			probedX = getLogicalCoordX(holeOver.hitbox.getCenterX());
			probedY = getLogicalCoordY(holeOver.hitbox.getCenterY());
			if(!guiManager.controller.probe(probedX, probedY)){
				probingTile = null;
				probeSquare.setCoordinates(holeOver.hitbox);
				guiManager.animator.enableTileProbeGlow();
				confirmButton.disable();
			} else {
				probingTile = turnTile.copy(probedX+";"+probedY, getGlobalCoordX(probedX), getGlobalCoordY(probedY), tileSize);
				probingTile.setAlpha(0.5f);
				confirmButton.enable();
			}
		}
		if(confirmButton.isEnabled() && confirmButton.isClicked(x, y)){
			if(!dimscreen){
				possibleMeeples = guiManager.controller.place(probedX, probedY);
				dimscreen = true;
			} else {
				
			}
		}
	}

	private void rotate(boolean clockwise){
		if(turnTile != null){
			turnTile.rotate(clockwise);
			guiManager.controller.rotate(clockwise);
			probedX = probedY = 0;
		}
	}
	
	@Override
	public void rightClick(int x, int y) {
		rotate(true);
	}

	@Override
	public void wheelMoved(boolean up) {
		rotate(up);
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
		
		if(!dimscreen){
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
		
		if(turnTile != null)
			confirmButton.isClicked(newx, newy);
	}
	
	private static final int getLogicalX(String coords){
		return Integer.parseInt(coords.split(";")[0]);
	}
	
	private static final int getLogicalY(String coords){
		return Integer.parseInt(coords.split(";")[1]);
	}
		
	private int getLogicalCoordX(int gc){
		return (gc - globalCenterOffset) / tileSize;
	}
	
	private int getLogicalCoordY(int gc){
		return -((gc - globalCenterOffset) / tileSize);
	}
	
	private int getGlobalCoordX(int lc){
		return globalCenterOffset + (lc * tileSize);
	}
	
	private int getGlobalCoordY(int lc){
		return globalCenterOffset + ((-lc) * tileSize);
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

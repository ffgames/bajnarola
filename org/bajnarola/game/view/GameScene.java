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
	
	//Animations / scenario
	private Map<String, GraphicalTile> currentScenario;
	private Map<String, Boolean> currentLanscape;
	private Map<String, GraphicalMeeple> placedMeeples;
	private List<GraphicalMeeple> meeplesToRemove;
	public GraphicalMeeple meepleToPlace;
	public GraphicalTile tileToPlace;
	private int tileSize, meepleSize;
	
	//HUD
		//graphical elements
	public GraphicalTile turnTile;
	private GraphicalTile probeSquare, holeOver;
	private GraphicalElement probingTile, handMeeples[];
	private GraphicalMeeple tmpMeeples[], currentPlayerMeeple;
	private Button zoomButton, confirmButton;
	private Image curtain;
		//logical elements
	private List<HitBox> holes;
	private boolean possibleMeeples[];
	private boolean zoomOutView, zoomable;
	private int turnTileCx, turnTileCy, turnTileSize;
	private int meeplesInHand;
	public boolean probing, probeResult, mouseOverOn, dimscreen;
	private int probedX, probedY;
	private short turnMeepleSide;
	
	
	//view area controll
	private int globalCenterOffset;
	private float scaleFactor;
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	public int xOff, yOff, maxX, maxY;
	private int logicalMaxX, logicalMinX, logicalMaxY, logicalMinY;
	
	
	// ##  INIT  ##
	
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
		meepleSize = tileSize/4;
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
		for(short i = 0; i < Tile.SIDE_COUNT; i++)
			tmpMeeples[i] = new GraphicalMeeple(this, 42, "", getMeepleCoordX(turnTileCx, turnTileSize, i), getMeepleCoordY(turnTileCy, turnTileSize, i), turnTileSize/4);
		
		currentPlayerMeeple = null;
	}

	public void initPlayerMeeple(int playerId) throws SlickException{
		currentPlayerMeeple = new GraphicalMeeple(this, playerId, "", 0, 0, turnTileSize/4);
		
		handMeeples = new GraphicalElement[7];
		int handMeepleSize = confirmButton.hitbox.ulx/9;
		if(handMeepleSize > GraphicalMeeple.MEEPLE_SIZE)
			handMeepleSize = GraphicalMeeple.MEEPLE_SIZE;
		RelativeSizes rs = RelativeSizes.getInstance();
		int nextCenterX = (handMeepleSize/2)+rs.handMeepleXOffset();
		int nextCenterY = guiManager.windowHeight - (handMeepleSize/2)-rs.handMeepleYOffset();
		
		for(int i = 0; i < 7; i++){
			handMeeples[i] = currentPlayerMeeple.copy("", nextCenterX, nextCenterY, handMeepleSize);
			nextCenterX += handMeepleSize+rs.handMeepleXOffset();
		}
		
		meeplesInHand = 7;
	}
	
	// ##  RENDERING ##
	
	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
		boardRender(gc, g);
		
		hudRender(gc, g);
		
		/*for(GraphicalTile t : currentScenario.values()){
			g.drawString(t.getCoordinates(), t.globalCenterX-xOff, t.globalCenterY-yOff);
		}*/
	}

	private void boardRender(GameContainer gc, Graphics g){
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
				
	}
	
	private void hudRender(GameContainer gc, Graphics g){
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
			
			if(turnMeepleSide > -1){
				currentPlayerMeeple.drawAbsolute();
			}
		}
		
		int meeplesToDraw = meeplesInHand - (dimscreen && turnMeepleSide != -1 ? 1 : 0);
		for(int i = 0; i < meeplesToDraw; i++){
			if(handMeeples[i] != null)
				handMeeples[i].drawAbsolute();
		}
		
		if(turnTile != null)
			confirmButton.draw();
	}
	
	// ##  ANIMATOR CONTROLS  ##
	
	//returns true if at least one meeple has been removed
	public boolean setCurrentLandscape(Map<String, Boolean> ls){
		currentLanscape = ls;
		Boolean val;
		boolean ret = false;
		for(GraphicalMeeple m : placedMeeples.values()){
			val = ls.get(m.coords);
			if(val != null && val == false){
				ret = true;
				meeplesToRemove.add(m);
			}
		}
		for(GraphicalMeeple m : meeplesToRemove){
			placedMeeples.remove(m.coords);
		}
		return ret;
	}
	
	public void drawScoreUpdate(String position, int value){
		
	}

	public void meeplesRemoved(int meeplesInHand){
		meeplesToRemove.clear();

		this.meeplesInHand = meeplesInHand;
	}
	
	public void tilePlaced(){
		currentScenario.put(tileToPlace.coords, tileToPlace);
		tileToPlace = null;
	}
	
	public void meeplePlaced(){
		if(meepleToPlace != null){
			placedMeeples.put(meepleToPlace.coords, meepleToPlace);
			meepleToPlace.setAlpha(1f);
			meepleToPlace = null;
		}
	}
	
	public void landscapesSet(){
		currentLanscape = null;
	}
	
	// ##  TURN MANAGEMENT  ##
	
	public boolean placeGraphicalTile(Tile tile, String coords) throws SlickException{
		int lx = getLogicalX(coords);
		int ly = getLogicalY(coords);
		tileToPlace = new GraphicalTile(this, tile.getName(), coords, tile.getDirection(), getGlobalCoordX(lx), getGlobalCoordY(ly), tileSize);
		
		setViewScaleValues(lx, ly);
		
		if(tile.hasMeeple()){
			meepleToPlace = new GraphicalMeeple(this, tile.getMeeple().getOwner().getId(), coords, 
					getMeepleCoordX(getGlobalCoordX(lx), tileSize, tile.getMeeple().getTileSide()),
					getMeepleCoordY(getGlobalCoordY(ly), tileSize, tile.getMeeple().getTileSide()), meepleSize);
			return true;
		}
		
		return false;
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
		turnMeepleSide = -1;
	}
	
	public void endTurn(){
		if(turnMeepleSide != -1){
			meeplesInHand --;
		}
		turnMeepleSide = -1;
		turnTile = null;
		probingTile = null;
		confirmButton.disable();
		possibleMeeples = null;
		dimscreen = false;
	}

	// ##  INPUT HANDLERS  ##
	
	@Override
	public void leftClick(int x, int y) {
		if(zoomable && zoomButton.isClicked(x, y)){
			if(zoomOutView)
				zoomButton.deactivate();
			else
				zoomButton.activate();
			zoomOutView = !zoomOutView;
		}
		if(turnTile != null){
			if(confirmButton.isEnabled() && confirmButton.isClicked(x, y)){
				if(!dimscreen){
					possibleMeeples = guiManager.controller.place(probedX, probedY);
					dimscreen = true;
				} else {
					guiManager.controller.placeMeeple(turnMeepleSide);
					endTurn();
				}
			} else if(mouseOverOn && !dimscreen){
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
			if(dimscreen){
				for(int i = 0; i < possibleMeeples.length; i ++){
					if(possibleMeeples[i] && tmpMeeples[i].isClicked(x, y, 0, 0)){
						if(turnMeepleSide != i){
							turnMeepleSide = (short) i;
							currentPlayerMeeple.setCoordinates(tmpMeeples[i].hitbox);
						} else 
							turnMeepleSide = -1;
					}
				}
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
			shiftView(Tile.SIDE_LEFT);
		} else if (newx > rightBorderX && newx < guiManager.windowWidth-3){
			shiftView(Tile.SIDE_RIGHT);
		}
		if(newy < upperBorderY && newy > 2){
			shiftView(Tile.SIDE_TOP);
		} else if (newy > lowerBorderY && newy < guiManager.windowHeight-3){
			shiftView(Tile.SIDE_BOTTOM);
		}
		
		if(turnTile != null){
			confirmButton.isClicked(newx, newy);
			
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
		}
	}

	@Override
	public void leftRelease(int x, int y) {}

	@Override
	public void rightRelease(int x, int y) {}

	// ##  VIEW AREA CONTROL  ##
		
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

	private void shiftView(int direction){
		switch(direction){
			case Tile.SIDE_LEFT:
				
				break;
			case Tile.SIDE_RIGHT:
				
				break;
			case Tile.SIDE_BOTTOM:
				
				break;
			case Tile.SIDE_TOP:
				
				break;
		}
	}
	
	// ##  COORDINATES MANAGEMENT  ##
	
	private int getMeepleCoordX(int tileCenterX, int tileSize, short meeplePos){
		switch(meeplePos){
			case Tile.SIDE_LEFT:
				return tileCenterX-(tileSize/3);
			case Tile.SIDE_RIGHT:
				return tileCenterX+(tileSize/3);
			default:
				return tileCenterX;
		}
	}
	
	private int getMeepleCoordY(int tileCenterY, int tileSize, short meeplePos){
		switch(meeplePos){
			case Tile.SIDE_TOP:
				return tileCenterY-(tileSize/3);
			case Tile.SIDE_BOTTOM:
				return tileCenterY+(tileSize/3);
			default:
				return tileCenterY;
		}
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

}

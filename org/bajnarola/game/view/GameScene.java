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
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class GameScene extends IScene {
	
	//Animations / scenario
	private Map<String, GraphicalTile> currentScenario;
	private Map<String, Boolean> currentLanscape;
	private Map<String, GraphicalMeeple> placedMeeples;
	private List<GraphicalMeeple> meeplesToRemove;
	public GraphicalMeeple meepleToPlace;
	public GraphicalTile tileToPlace;
	public int tileSize, meepleSize;
	private int currentScoreGlobalX, currentScoreGlobalY, currentScoreVal;
	
	//HUD
		//graphical elements
	public GraphicalTile turnTile;
	private GraphicalTile probeSquare, holeOver;
	private GraphicalElement probingTile, handMeeples[];
	private GraphicalMeeple tmpMeeples[], currentPlayerMeeple;
	private Button zoomButton, confirmButton;
	private Image curtain;
	private List<String> scores;
	private String currentPlScore;
	private Color[] playerColors;
		//logical elements
	private List<HitBox> holes;
	private boolean possibleMeeples[];
	private boolean zoomOutView, zoomable;
	private int turnTileCx, turnTileCy;
	public int turnTileSize;
	private int meeplesInHand;
	public boolean probing, probeResult, mouseOverOn, dimscreen;
	private int probedX, probedY;
	private short turnMeepleSide;
	private boolean isScoreHovered;
	private HitBox scoreHitbox;
	
	//view area controll
	private int globalCenterOffset;
	private float scaleFactor;
	private int leftBorderX, rightBorderX, upperBorderY, lowerBorderY;
	public int xOff, yOff, minXOff, minYOff;
	private int logicalMaxX, logicalMinX, logicalMaxY, logicalMinY;
	public int logicalCenterX, logicalCenterY;
	private int maxXOff, maxYOff;
	private boolean hudHovered;
	private short udDir, lrDir, kbdUdDir, kbdLrDir;
	private int hudTotalHeight, hudTotalWidth;
	
	private RelativeSizes resizer;
	
	// ##  INIT  ##
	
	public GameScene(Gui guiManager, Image background, bg_type backgroundType, List<String> scores, String currentPlScore) throws SlickException {
		super(guiManager, background, backgroundType);
		resizer = RelativeSizes.getInstance();
		sceneType = scene_type.SCENE_GAME;
		int minWindowSize = (guiManager.windowHeight < guiManager.windowWidth ? guiManager.windowHeight : guiManager.windowWidth); 
		
		leftBorderX = (int)((float)guiManager.windowWidth * resizer.scrollAreaRateo());
		rightBorderX = guiManager.windowWidth - leftBorderX;
		upperBorderY= (int)((float)guiManager.windowHeight * resizer.scrollAreaRateo());
		lowerBorderY = guiManager.windowHeight - upperBorderY;
		
		tileSize = minWindowSize / 8;
		tileSize = (tileSize < GraphicalTile.TILE_SIZE ? tileSize : GraphicalTile.TILE_SIZE);
		meepleSize = tileSize/4;
		globalCenterOffset = (tileSize * (Board.TOTAL_TILES_COUNT * 2)) / 2;
		scaleFactor = 1;
		zoomOutView = zoomable = false;
		xOff = globalCenterOffset - (guiManager.windowWidth/2);
		yOff = globalCenterOffset - (guiManager.windowHeight/2);
		
		zoomButton = new Button(minWindowSize/10, minWindowSize/10, guiManager.windowWidth-(minWindowSize/10), minWindowSize/10, new Image("res/misc/zoomOut.png"), new Image("res/misc/zoomIn.png"));
		confirmButton = new Button(minWindowSize/4, minWindowSize/10, guiManager.windowWidth/2, guiManager.windowHeight-(minWindowSize/20),
				 new Image("res/menu/placeInactive.png"), new Image("res/menu/placeActive.png"), new Image("res/menu/placeDisabled.png"));
		confirmButton.setSecImages(new Image("res/menu/confirmInactive.png"), new Image("res/menu/confirmActive.png"), new Image("res/menu/confirmDisabled.png"));
		confirmButton.deactivate();
		
		curtain = new Image("res/misc/gray.png");
		curtain.setAlpha(0.8f);
		
		logicalCenterX = logicalCenterY = logicalMaxX = logicalMaxY = logicalMinX = logicalMinY = 0;
		minXOff = maxXOff = xOff;
		minYOff = maxYOff = yOff;
		
		kbdUdDir = kbdLrDir = udDir = lrDir = Tile.SIDE_CENTER;
		
		currentScoreGlobalX = currentScoreGlobalY = currentScoreVal = -1;
		
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
		
		isScoreHovered = false;
		scoreHitbox = new HitBox(resizer.scoresXOffset(), resizer.scoresYOffset(), 0, 0);
		setScores(currentPlScore, scores);
		playerColors = new Color[8];
		playerColors[0] = new Color(0xEA, 0x3F, 0x1B);
		playerColors[1] = new Color(0x3E, 0x3F, 0xD8);
		playerColors[2] = new Color(0xE6, 0xBD, 0x4A);
		playerColors[3] = new Color(0x27, 0xA1, 0xDE);
		playerColors[4] = new Color(0xB2, 0x70, 0x49);
		playerColors[5] = new Color(0xFF, 0xFF, 0xFF);
		playerColors[6] = new Color(0x93, 0x41, 0xD2);
		playerColors[7] = new Color(0x73, 0xCB, 0x49);
		
		
		tmpMeeples = new GraphicalMeeple[Tile.SIDE_COUNT];
		for(short i = 0; i < Tile.SIDE_COUNT; i++)
			tmpMeeples[i] = new GraphicalMeeple(this, 42, "", i, getMeepleCoordX(turnTileCx, turnTileSize, i), getMeepleCoordY(turnTileCy, turnTileSize, i), turnTileSize/4);
		
		currentPlayerMeeple = null;
		
		hudTotalHeight = 0;
		hudTotalWidth = turnTileSize;
	}

	public void initPlayerMeeple(int playerId) throws SlickException{
		currentPlayerMeeple = new GraphicalMeeple(this, playerId, "", (short)-1, 0, 0, turnTileSize/4);
		
		handMeeples = new GraphicalElement[7];
		int handMeepleSize = confirmButton.hitbox.ulx/9;
		if(handMeepleSize > GraphicalMeeple.MEEPLE_SIZE)
			handMeepleSize = GraphicalMeeple.MEEPLE_SIZE;
		int nextCenterX = (handMeepleSize/2)+resizer.handMeepleXOffset();
		int nextCenterY = guiManager.windowHeight - (handMeepleSize/2)-resizer.handMeepleYOffset();
		
		for(int i = 0; i < 7; i++){
			handMeeples[i] = currentPlayerMeeple.copy("", nextCenterX, nextCenterY, handMeepleSize);
			nextCenterX += handMeepleSize+resizer.handMeepleXOffset();
		}
		
		meeplesInHand = 7;
		
		hudTotalHeight = guiManager.windowHeight - Math.min(handMeeples[0].hitbox.uly, Math.min(confirmButton.hitbox.uly, (turnTileCy - (turnTileSize/2))));
	}
	
	// ##  RENDERING ##
	
	@Override
	public void render(GameContainer gc, Graphics g) {
		guiManager.drawBackground(background, backgroundType);
		
		shiftView();
		
		boardRender(gc, g);
		
		hudRender(gc, g);
		
		//g.drawString(String.format("lmx: %d, lMx: %d, lmy: %d, lMy: %d", logicalMinX, logicalMaxX, logicalMinY, logicalMaxY), 400, 10);
		//g.drawString(String.format("mxo: %d, Mxo: %d, myo: %d, Myo: %d", minXOff, maxXOff, minYOff, maxYOff), 400, 30);
		//g.drawString(String.format("xOff: %d, yOff: %d, hh: %s, zm: %s", xOff, yOff, (hudHovered ? "true" : "false"), (zoomable ? "true" : "false")), 400, 50);
		/*for(GraphicalTile t : currentScenario.values()){
			char arrow = '^';
			switch (t.direction) {
			case 0:
				arrow = '^';
				break;
			case 1:
				arrow = '>';
				break;
			case 2:
				arrow = 'v';
				break;
			case 3:
				arrow = '<';
				break;
			}
			g.drawString(t.getCoordinates()+arrow, t.globalCenterX-xOff, t.globalCenterY-yOff);	
		}*/
	}

	private void boardRender(GameContainer gc, Graphics g){
		//Static scenario elements
		
		for(GraphicalTile t : currentScenario.values()){
			if(currentLanscape != null && currentLanscape.containsKey(t.getCoordinates()))
				guiManager.animator.drawLandscapeGlowingTile(t, zoomOutView, scaleFactor);
			else
				t.draw(zoomOutView, scaleFactor);
		}
		
		for(GraphicalMeeple m : placedMeeples.values()){
			m.draw(zoomOutView, scaleFactor);
		}
		
		//User interactions
		
		if(mouseOverOn)
			holeOver.draw(zoomOutView, scaleFactor);
		
		if(probing){
			if(guiManager.animator.isTileProbeGlowOn())
				guiManager.animator.drawTileProbe(probeSquare, zoomOutView, scaleFactor, probeResult);
			else
				probing = false;
		}
		
		if(probingTile != null)
			probingTile.draw(zoomOutView, scaleFactor);
		
		//Animations
		
		for(GraphicalMeeple m : meeplesToRemove){
			guiManager.animator.drawMeepleRemoval(m, zoomOutView, scaleFactor);
		}
		
		if(tileToPlace != null)
			guiManager.animator.drawTilePlacement(tileToPlace, zoomOutView, scaleFactor);
		
		if(meepleToPlace != null)
			guiManager.animator.drawMeeplePlacement(meepleToPlace, zoomOutView, scaleFactor);
		
		if(currentScoreGlobalX > -1 && currentScoreGlobalY > -1 && currentScoreVal > -1 && 
		   isStringInView(xOff, yOff, guiManager.windowWidth, guiManager.windowHeight, currentScoreGlobalX, currentScoreGlobalY)){
			guiManager.animator.drawShowScore(g, currentScoreVal, currentScoreGlobalX-xOff, currentScoreGlobalY-yOff);
		}
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
		
		if(isScoreHovered){
			for(int i = 0; i < scores.size(); i++){
				drawScore(resizer.scoresXOffset(), resizer.scoresYOffset()+(i*guiManager.mainFont.getLineHeight()), scores.get(i), g);
			}
		} else {
			drawScore(resizer.scoresXOffset(), resizer.scoresYOffset(), currentPlScore, g);
		}
		
		if(turnTile != null)
			confirmButton.draw();
	}
	
	private void drawScore(int x, int y, String score, Graphics g){
		if(score != null && !score.isEmpty()){
			int player = Integer.parseInt(score.split("-")[0]);
			String scoreStr = score.split("-")[1];
			guiManager.drawString(scoreStr, x, y, playerColors[player]);
		}
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
	
	public void drawScoreUpdate(String score, List<String> plScores, String curPlScore){
		//score is in the format "[x];[y]:[score]"
		currentScoreGlobalX = getGlobalCoordX(Integer.parseInt(score.split(";")[0]));
		currentScoreGlobalY = getGlobalCoordY(Integer.parseInt(score.split(";")[1].split(":")[0]));
		currentScoreVal = Integer.parseInt(score.split(":")[1]);
		setScores(curPlScore, plScores);
	}

	public void scoreDrawed(){
		currentScoreGlobalX = currentScoreGlobalY = currentScoreVal = -1;
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
			meepleToPlace = new GraphicalMeeple(this, tile.getMeeple().getOwner().getId(), coords, tile.getMeeple().getTileSide(),
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
		confirmButton.setPrimary(true);
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

	public void setScores(String currentPlayerScore, List<String> scores){
		this.currentPlScore = currentPlayerScore;
		this.scores = scores;
		if(currentPlayerScore != null && !currentPlayerScore.isEmpty()){
			scoreHitbox.reset(resizer.scoresXOffset(), resizer.scoresYOffset(), 
					resizer.scoresXOffset()+guiManager.container.getGraphics().getFont().getWidth(currentPlayerScore), 
					resizer.scoresYOffset()+guiManager.container.getGraphics().getFont().getHeight(currentPlayerScore)+resizer.scoreOverHeight());
		}
	}
	
	// ##  INPUT HANDLERS  ##
	
	@Override
	public void leftClick(int x, int y) {
		if(zoomable && zoomButton.hits(x, y)){
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
					confirmButton.setPrimary(false);
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
							confirmButton.setPrimary(true);
						} else {
							turnMeepleSide = -1;
							confirmButton.setPrimary(false);
						}
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
	}

	@Override
	public void escPressed() {
		guiManager.switchScene(scene_type.SCENE_PAUSE);
	}

	@Override
	public void backspacePressed() {
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		//view should not shift if someone is trying to interuct with the hud
		hudHovered = false;
		hudHovered |= isScoreHovered = scoreHitbox.hits(newx, newy);
		
		hudHovered |= confirmButton.hits(newx, newy);
		if(turnTile != null){
			confirmButton.isClicked(newx, newy);
			
			if(!dimscreen && !zoomOutView){
				mouseOverOn = false;
				if(!holes.isEmpty()){
					for(HitBox h : holes){
						if(h.hits(newx, newy, xOff, yOff)){
							holeOver.setCoordinates(h);
							mouseOverOn = true;
						}
					}
				}
			} else
				hudHovered = true;
		}
		
		hudHovered |= zoomButton.hits(newx, newy);
		
		if(!hudHovered){
			if(newx < leftBorderX){
				lrDir = Tile.SIDE_LEFT;
			} else if (newx > rightBorderX){
				lrDir = Tile.SIDE_RIGHT;
			} else {
				lrDir = Tile.SIDE_CENTER;
			}
			if(newy < upperBorderY){
				udDir = Tile.SIDE_TOP;
			} else if (newy > lowerBorderY){
				udDir = Tile.SIDE_BOTTOM;
			} else {
				udDir = Tile.SIDE_CENTER;
			}
		}
	}

	public void stopScrolling(){
		udDir = Tile.SIDE_CENTER;
		lrDir = Tile.SIDE_CENTER;
	}
	
	@Override
	public void leftRelease(int x, int y) {}

	@Override
	public void rightRelease(int x, int y) {}

	// ##  VIEW AREA CONTROL  ##
		
	private void setViewScaleValues(int lx, int ly){
		if(lx-2 < logicalMinX){
			logicalMinX = lx-2;
			minXOff = Math.min(minXOff, (logicalMinX*tileSize)+globalCenterOffset);
			if(lrDir == Tile.SIDE_CENTER)
				lrDir = Tile.SIDE_LEFT;
		}
		if(lx+2 > logicalMaxX){
			logicalMaxX = lx+2;
			maxXOff = Math.max(maxXOff, (logicalMaxX*tileSize)+globalCenterOffset-(guiManager.windowWidth-hudTotalWidth));
			if(lrDir == Tile.SIDE_CENTER)
				lrDir = Tile.SIDE_RIGHT;
		}
		if(ly-2 < logicalMinY){
			logicalMinY = ly-2;
			maxYOff = Math.max(maxYOff, globalCenterOffset-(logicalMinY*tileSize)-(guiManager.windowHeight-hudTotalHeight));
			if(udDir == Tile.SIDE_CENTER)
				udDir = Tile.SIDE_BOTTOM;
		}
		if(ly+2 > logicalMaxY){
			logicalMaxY = ly+2;
			minYOff = Math.min(minYOff, globalCenterOffset-(logicalMaxY*tileSize));
			if(udDir == Tile.SIDE_CENTER)
				udDir = Tile.SIDE_TOP;
		}
		
		logicalCenterX = (logicalMaxX + logicalMinX)/2;
		logicalCenterY = (logicalMaxY + logicalMinY)/2;
		
		int tw, th;
		tw = (logicalMaxX - logicalMinX) * tileSize;
		th = (logicalMaxY - logicalMinY) * tileSize;
		
		if(tw > (guiManager.windowWidth - hudTotalWidth) || th > (guiManager.windowHeight - hudTotalHeight)){
			float hscale, vscale;
			
			hscale = (float)guiManager.windowHeight / th;
			vscale = (float)guiManager.windowWidth / tw;
		
			scaleFactor = Math.min(1, Math.min(hscale, vscale));
			
			zoomable = true;
		}
		
		//one tile could have been placed near the screen edges but the board still fits the window.. in case shift bound values
		if(tw <= (guiManager.windowWidth - hudTotalWidth) && minXOff != maxXOff){
			if(minXOff < xOff)
				maxXOff = minXOff;
			else if(maxXOff > xOff)
				minXOff = maxXOff;
		}
		
		if(th <= (guiManager.windowHeight - hudTotalHeight) && minYOff != maxYOff){
			if(minYOff < yOff)
				maxYOff = minYOff;
			else if(maxYOff > yOff)
				minYOff = maxYOff;
		}
	}

	private void shiftView(){
		int offset = guiManager.animator.getViewShiftOffset();
		short hdir, vdir;
		
		if(kbdUdDir != Tile.SIDE_CENTER)
			hdir = kbdUdDir;
		else
			hdir = udDir;
		if(kbdLrDir != Tile.SIDE_CENTER)
			vdir = kbdLrDir;
		else
			vdir = lrDir;
		
		if(vdir == Tile.SIDE_LEFT && xOff > minXOff)
			xOff -= Math.min(offset, xOff - minXOff);
		else if(vdir == Tile.SIDE_RIGHT && xOff < maxXOff)
			xOff += Math.min(offset, maxXOff - xOff);
		if(hdir == Tile.SIDE_BOTTOM && yOff < maxYOff)
			yOff += Math.min(offset, maxYOff - yOff);
		else if(hdir == Tile.SIDE_TOP && yOff > minYOff)
			yOff -= Math.min(offset, yOff - minYOff);
	}
	
	private static boolean isStringInView(int xOff, int yOff, int windowWidth, int windowHeight, int stringX, int stringY){
		if(stringX > xOff && stringX < (xOff + windowWidth) && stringY > yOff && stringY < (yOff + windowHeight))
			return true;
		return false;
	}
	
	// ##  COORDINATES MANAGEMENT  ##
	
	public int getMeepleCoordX(int tileCenterX, int tileSize, short meeplePos){
		switch(meeplePos){
			case Tile.SIDE_LEFT:
				return tileCenterX-(tileSize/3);
			case Tile.SIDE_RIGHT:
				return tileCenterX+(tileSize/3);
			default:
				return tileCenterX;
		}
	}
	
	public int getMeepleCoordY(int tileCenterY, int tileSize, short meeplePos){
		switch(meeplePos){
			case Tile.SIDE_TOP:
				return tileCenterY-(tileSize/3);
			case Tile.SIDE_BOTTOM:
				return tileCenterY+(tileSize/3);
			default:
				return tileCenterY;
		}
	}
	
	public static final int getLogicalX(String coords){
		return Integer.parseInt(coords.split(";")[0]);
	}
	
	public static final int getLogicalY(String coords){
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
	public void keyPressed(int key, char c) {
		if(key == Input.KEY_UP || key == Input.KEY_W)
			kbdUdDir = Tile.SIDE_TOP;
		if(key == Input.KEY_RIGHT || key == Input.KEY_D)
			kbdLrDir = Tile.SIDE_RIGHT;
		if(key == Input.KEY_DOWN || key == Input.KEY_S)
			kbdUdDir = Tile.SIDE_BOTTOM;
		if(key == Input.KEY_LEFT || key == Input.KEY_A)
			kbdLrDir = Tile.SIDE_LEFT;
	}

	@Override
	public void keyReleased(int key, char c) {
		if(key == Input.KEY_UP || key == Input.KEY_W || key == Input.KEY_DOWN || key == Input.KEY_S)
			kbdUdDir = Tile.SIDE_CENTER;
		if(key == Input.KEY_RIGHT || key == Input.KEY_D || key == Input.KEY_LEFT || key == Input.KEY_A)
			kbdLrDir = Tile.SIDE_CENTER;
	}

}

package org.bajnarola.game.view;

public class Animator {
	final static int TILE_PLACEMENT_DURATION = 50; //in frames (~2sec)
	final static int LANDSCAPE_GLOW_DURATION = 120; // (~5sec)
	final static int TILE_PROBE_GLOW_DURATION = 40; // (<2sec)
	
	final static int LG_GRADIENT_BEGIN_START = 0;
	final static int LG_GRADIENT_BEGIN_END = 40;
	final static int LG_GRADIENT_FINISH_START = 80;
	final static int LG_GRADIENT_FINISH_END = 120;
	
	final static float TILE_PLACEMENT_INITIAL_SCALE = (float)1.7;
	final static float TILE_PLACEMENT_FINAL_SCALE = (float)1;
	final static float LANDSCAPE_GLOW_INITIAL_GRADIENT = (float)0;
	final static float LANDSCAPE_GLOW_FINAL_GRADIENT = (float)0.5;
	final static float TILE_PROBE_GLOW_INITIAL_OPACITY = (float)0;
	final static float TILE_PROBE_GLOW_FINAL_OPACITY = (float)1;
	
	// XXX: mettiamo qui anche le coordinate logiche da aggiornare?
	
	int tilePlacementFrame;
	int landscapeGlowFrame;
	int tileProbeGlowFrame;
	
	boolean tilePlacementOn;
	boolean landscapeGlowOn;
	boolean tileProbeGlowOn;
	
	public Animator(){
		tilePlacementOn = landscapeGlowOn = tileProbeGlowOn = false;
		tilePlacementFrame = landscapeGlowFrame = tileProbeGlowFrame = 0;
	}
	
	public float getTilePlacementScale(){
		if(tilePlacementOn){//assuming inital scale > final scale
			return (((float)(TILE_PLACEMENT_INITIAL_SCALE - TILE_PLACEMENT_FINAL_SCALE) / (float)TILE_PLACEMENT_DURATION) * (TILE_PLACEMENT_DURATION - tilePlacementFrame)) + TILE_PLACEMENT_FINAL_SCALE;
		}
		return -1;
	}
	
	public float getLandscapeGlowGradient(){
		if(landscapeGlowOn){
			if(landscapeGlowFrame < LG_GRADIENT_BEGIN_END){
				return ((LANDSCAPE_GLOW_FINAL_GRADIENT - LANDSCAPE_GLOW_INITIAL_GRADIENT) / (LG_GRADIENT_BEGIN_END - LG_GRADIENT_BEGIN_START) * landscapeGlowFrame) + LANDSCAPE_GLOW_INITIAL_GRADIENT;
			} else if(landscapeGlowFrame >= LG_GRADIENT_FINISH_START){
				return ((LANDSCAPE_GLOW_FINAL_GRADIENT - LANDSCAPE_GLOW_INITIAL_GRADIENT) / (LG_GRADIENT_BEGIN_END - LG_GRADIENT_BEGIN_START) * (LG_GRADIENT_FINISH_END - landscapeGlowFrame)) + LANDSCAPE_GLOW_INITIAL_GRADIENT;
			} else
				return LANDSCAPE_GLOW_FINAL_GRADIENT;
		}
		return -1;
	}
	
	public float getTileProbeOpacity(){
		if(tileProbeGlowOn){
			return (((TILE_PROBE_GLOW_FINAL_OPACITY - TILE_PROBE_GLOW_INITIAL_OPACITY) / TILE_PROBE_GLOW_DURATION) * tileProbeGlowFrame) + TILE_PROBE_GLOW_INITIAL_OPACITY;
		}
		return -1;
	}
	
	public void step(){
		if(tilePlacementOn){
			tilePlacementFrame++;
			if(tilePlacementFrame > TILE_PLACEMENT_DURATION)
				tilePlacementOn = false;
		}
		if(landscapeGlowOn){
			landscapeGlowFrame++;
			if(landscapeGlowFrame > LANDSCAPE_GLOW_DURATION)
				landscapeGlowOn = false;
		}
		if(tileProbeGlowOn){
			tileProbeGlowFrame++;
			if(tileProbeGlowFrame > TILE_PROBE_GLOW_DURATION)
				tileProbeGlowOn = false;
		}
	}
	
	public boolean isTilePlacementOn(){
		return tilePlacementOn;
	}
	
	public boolean isLandscapeGlowOn(){
		return landscapeGlowOn;
	}
	
	public boolean isTileProbeGlowOn(){
		return tileProbeGlowOn;
	}
	
	public void enableTilePlacement(){
		tilePlacementFrame = 0;
		tilePlacementOn = true;
	}
	
	public void enableLandscapeGlow(){
		landscapeGlowFrame = 0;
		landscapeGlowOn = true;
	}
	
	public void enableTileProbeGlow(){
		tileProbeGlowFrame = 0;
		tileProbeGlowOn = true;
	}
}

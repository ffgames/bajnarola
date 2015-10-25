package org.bajnarola.game.view;

public class HitBox {
	int ulx, uly, lrx, lry;
	
	public HitBox(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY, int globalCenterX, int globalCenterY){
		this.ulx = upperLeftX;
		this.uly = upperLeftY;
		this.lrx = lowerRightX;
		this.lry = lowerRightY;
	}
	
	public boolean hits(int x, int y){
		if(x >= ulx && x <= lrx && y >= uly && y <= lry)
			return true;
		return false;
	}
	
	public boolean hits(int mouseX, int mouseY, int offX, int offY){
		return hits(mouseX+offX, mouseY+offY);
	}
}

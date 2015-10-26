package org.bajnarola.game.view;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


public class InputBox {
	HitBox hitbox;
	Image image;
	String text = "prova";
	int centerX, centerY, width, height;
	
	public InputBox(int width, int height, int centerX, int centerY, Image activeImage){
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.image = activeImage;
		hitbox = new HitBox(centerX-(width/2), centerY-(height/2), centerX+(width/2), centerY+(height/2));
	}
	
	public boolean isClicked(int x, int y){
		return hitbox.hits(x, y);
	}
	
	public void draw(Graphics g){
		image.draw(hitbox.ulx, hitbox.uly, width, height);
		g.drawString(text, hitbox.ulx + 4, hitbox.uly + 1);
	}
}

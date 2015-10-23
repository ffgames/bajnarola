package org.bajnarola.networking;

import java.io.Serializable;

import org.bajnarola.game.controller.GameController;

public class NetPlayer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String username;
	public String rmiUriBoard;
	
	public NetPlayer(String uname, String uri) {
		this.username = uname;
		this.rmiUriBoard = uri + "/" + GameController.class.getName();
	}
}

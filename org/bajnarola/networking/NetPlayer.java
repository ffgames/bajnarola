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
	public String host; /* Ip or Hostname */
	public int bindPort; 
	
	public NetPlayer(String uname, int bindPort) {
		this.username = uname;
		this.bindPort = bindPort;
		this.rmiUriBoard = uname + "/" + GameController.class.getName();
	}
}

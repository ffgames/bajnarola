package org.bajnarola.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.bajnarola.utils.BajnarolaRegistry;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameOptions {
	public class ViewOptions {
		private int resx, resy;
		private boolean fullscreen;

		public ViewOptions(int resx, int resy, boolean fullscreen) {
			this.resx = resx;
			this.resy = resy;
			this.fullscreen = fullscreen;
		}

		public int getResx() {
			return resx;
		}

		public int getResy() {
			return resy;
		}

		public boolean isFullscreen() {
			return fullscreen;
		}
	}
	
	public class LobbyOptions {
		String lobbyUri, playerName;
		public LobbyOptions(String lobbyUri, String playerName) {
			this.lobbyUri = lobbyUri;
			this.playerName = playerName;
		}
		public String getLobbyUri() {
			return lobbyUri;
		}
		public String getPlayerName() {
			return playerName;
		}
		
	}
	
	public static final boolean defaultFullscreen = false; 
	public static final String optFileName = ".bajnarola.conf";
	public static String optFilePath;
	public static final int defaultResX = 1200;
	public static final int defaultResY = 700;
	public static final String defaultPlayerName = "Username";
	public static final String defaultLobbyHost = "localhost";
	
	int resx = defaultResX, resy = defaultResY;
	boolean fullscreen = defaultFullscreen;
	
	String lobbyHost = defaultLobbyHost, playerName = defaultPlayerName;
	int lobbyPort = BajnarolaRegistry.DEFAULT_LOBBY_PORT;
	
	public GameOptions()  {
		
		try {
			File cwd = new File(System.getProperty("user.home"));
			File optFile = new File(cwd, optFileName);
			optFilePath = optFile.getAbsolutePath();
			if (!optFile.exists()) {
				System.out.println("Config file not found. Creating it...");
				optFile.createNewFile();
			}
		} catch (IOException e) {
			System.err.println("Can't create the config file");
		} 
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(optFilePath));
			if (prop.isEmpty()) {
				storeViewConfig(prop);
				storeLobbyConfig(prop);
			} else {
				loadViewConfig(prop);
				loadLobbyConfig(prop);
			}
		} catch (IOException|NullPointerException e) {
			e.printStackTrace();
			System.err.println("Can't access to the config file");
		}
		
		
		if (fullscreen) {
			//Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			DisplayMode mode;
			mode = Display.getDisplayMode();
			resx = mode.getWidth();
			resy = mode.getHeight();
		}
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public void setLobbyHostPort(String lobbyServerURI) throws MalformedURLException {

		System.out.println("<< " + lobbyServerURI + " >>");
		
		String splittedURI[] = lobbyServerURI.split(":");
		
		if (splittedURI.length < 1 || splittedURI.length > 2)
			throw new MalformedURLException("Malformed Lobby URI");
		
		this.lobbyHost = splittedURI[0];
		
		if (splittedURI.length == 2) 
			lobbyPort = Integer.parseInt(splittedURI[1]);	
		else 
			lobbyPort = BajnarolaRegistry.DEFAULT_LOBBY_PORT;
	}
	
	public void setViewOptions(int resx, int resy, boolean fullscreen) {
		this.resx = resx;
		this.resy = resy;
		this.fullscreen = fullscreen;
		storeConfig(true);
	}

	
	public ViewOptions getViewOptions(){
		return new ViewOptions(resx, resy, fullscreen);
	}
	
	public LobbyOptions getLobbyOptions(){
		String lobbyUri = this.lobbyHost;
		
		if (lobbyPort != BajnarolaRegistry.DEFAULT_LOBBY_PORT)
			lobbyUri += ":" + Integer.toString(lobbyPort);
		
		return new LobbyOptions(lobbyUri, this.playerName);
	}
	
	public void storeLobbyConfig(Properties prop) throws IOException {
		prop.setProperty("playerName", playerName);
		prop.setProperty("lobbyHost", lobbyHost);
		prop.setProperty("lobbyPort", Integer.toString(lobbyPort));
		prop.store(new FileOutputStream(optFilePath), "game options");
	}
	
	private void storeViewConfig(Properties prop) throws IOException {

		prop.setProperty("resx", Integer.toString(resx));
		prop.setProperty("resy", Integer.toString(resy));
		prop.setProperty("fullscreen", Boolean.toString(fullscreen));
		prop.store(new FileOutputStream(optFilePath), "game options");

	}
	
	public void storeConfig(boolean viewOptions) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(optFilePath));
			if (viewOptions)
				storeViewConfig(prop);
			else 
				storeLobbyConfig(prop);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't access to the config file");
		}
	}
	
	private void loadViewConfig(Properties prop) {
		String tmp;
		if ((tmp = prop.getProperty("resx")) != null)
				resx = Integer.parseInt(tmp);
		if ((tmp = prop.getProperty("resy")) != null)
			resy = Integer.parseInt(tmp);
		if ((tmp = prop.getProperty("fullscreen")) != null)
			fullscreen = Boolean.parseBoolean(tmp);
	}
	
	private void loadLobbyConfig(Properties prop) {
		String tmp;
		if ((tmp = prop.getProperty("playerName")) != null)
				this.playerName = tmp;
		if ((tmp = prop.getProperty("lobbyHost")) != null)
			this.lobbyHost = tmp;
		if ((tmp = prop.getProperty("lobbyPort")) != null)
			this.lobbyPort = Integer.parseInt(tmp);
	}
	
	
	public String getLobbyHost() {
		return this.lobbyHost;
	}

	public int getLobbyPort() {
		return this.lobbyPort;
	}
	
	public String getPlayerName() {
		return playerName;
	}
}

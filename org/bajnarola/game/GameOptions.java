package org.bajnarola.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.bajnarola.utils.BajnarolaRegistry;
import org.lwjgl.LWJGLException;
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
	String lobbyHost, playerName;
	
	
	static final boolean defaultFullscreen = false; 
	static final String optFileName = ".bajnarola.conf";
	static String optFilePath;
	static final int defaultResX = 1200;
	static final int defaultResY = 700;
	
	int resx = defaultResX, resy = defaultResY;
	boolean fullscreen = defaultFullscreen;
	
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
				storeConfig(prop);
			} else {
				loadConfig(prop);
			}
		} catch (IOException|NullPointerException e) {
			e.printStackTrace();
			System.err.println("Can't access to the config file");
		}
		
		
		if (fullscreen) {
			//Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			DisplayMode[] modes;
			try {
				modes = Display.getAvailableDisplayModes();
				
				resx = modes[0].getWidth();
				resy = modes[0].getHeight();
			} catch (LWJGLException e1) {
				e1.printStackTrace();
			}
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
	}
	

	public void setViewOptions(int resx, int resy, boolean fullscreen) {
		this.resx = resx;
		this.resy = resy;
		this.fullscreen = fullscreen;
		storeConfig();
	}
	
	public ViewOptions getViewOptions(){
		return new ViewOptions(resx, resy, fullscreen);
	}
	
	private void storeConfig(Properties prop) throws IOException {

		prop.setProperty("resx", Integer.toString(resx));
		prop.setProperty("resy", Integer.toString(resy));
		prop.setProperty("fullscreen", Boolean.toString(fullscreen));
		prop.store(new FileOutputStream(optFilePath), "game options");

	}
	
	public void storeConfig() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(optFilePath));
			storeConfig(prop);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't access to the config file");
		}
	}
	
	private void loadConfig(Properties prop) {
		String tmp;
		if ((tmp = prop.getProperty("resx")) != null)
				resx = Integer.parseInt(tmp);
		if ((tmp = prop.getProperty("resy")) != null)
			resy = Integer.parseInt(tmp);
		if ((tmp = prop.getProperty("fullscreen")) != null)
			fullscreen = Boolean.parseBoolean(tmp);
	}
	
	public void loadConfig() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(optFilePath));
			if (!prop.isEmpty())
				loadConfig(prop);
			
		} catch (IOException|NullPointerException e) {
			e.printStackTrace();
			System.err.println("Can't access to the config file");
		}
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

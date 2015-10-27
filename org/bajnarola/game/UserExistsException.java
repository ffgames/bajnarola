package org.bajnarola.game;

import java.rmi.RemoteException;

public class UserExistsException extends RemoteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserExistsException() {
		// TODO Auto-generated constructor stub
	}

	public UserExistsException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	public UserExistsException(String s, Throwable cause) {
		super(s, cause);
		// TODO Auto-generated constructor stub
	}

}

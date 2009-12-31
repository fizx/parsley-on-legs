package com.eightylegs.customer;

public class Custom80AppFactory {
	/**
	 * Create and return your custom code object that implements the I80App
	 * interface
	 * 
	 * @return your custom code object that implements the I80App interface
	 */
	public static I80App get80App() {
		return new ParsleyApp(); // Make sure to return correct object type
	}
}
package com.avygeil.util;

public final class StringUtils
{
	private StringUtils() {}
	
	public static boolean isAlpha(String str)
	{
		return str.matches("^[a-zA-Z]*$");
	}
	
	public static boolean isNumeric(String str)
	{
		return str.matches("^-?\\d+$");
	}
}

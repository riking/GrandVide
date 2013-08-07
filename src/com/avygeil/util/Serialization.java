package com.avygeil.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Serialization
{
	private Serialization() {}
	
	public static byte[] concatByteArrays(byte[] first, byte[]... args)
	{
		if (first == null)
			return args[0];
		
		if (args == null)
			return first;
		
		int length = first.length;
		
		for (byte[] array : args)
			length += array.length;
		
		byte[] result = Arrays.copyOf(first, length);
		int offset = first.length;
		
		for (byte[] array : args)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		
		return result;
	}
	
	public static byte[] toByteArray(double value)
	{
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}
	
	public static byte[] toByteArray(float value)
	{
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putFloat(value);
		return bytes;
	}
	
	public static byte[] toByteArray(int value)
	{
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(value);
		return bytes;
	}
	
	public static double toDouble(byte[] bytes)
	{
		if (bytes.length != 8)
			throw new IllegalArgumentException("Le double (" + bytes.length + ") doit mesurer 8 octets");
		
		return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static float toFloat(byte[] bytes)
	{
		if (bytes.length != 4)
			throw new IllegalArgumentException("Le float (" + bytes.length + ") doit mesurer 4 octets");
		
		return ByteBuffer.wrap(bytes).getFloat();
	}
	
	public static int toInt(byte[] bytes)
	{
		if (bytes.length != 4)
			throw new IllegalArgumentException("Le int (" + bytes.length + ") doit mesurer 4 octets");
		
		return ByteBuffer.wrap(bytes).getInt();
	}
}

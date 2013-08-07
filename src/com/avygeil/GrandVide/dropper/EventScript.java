package com.avygeil.GrandVide.dropper;

public class EventScript
{
	private String name;
	private boolean repeat;
	private String sequence;
	
	public EventScript(String name, boolean repeat, String sequence)
	{
		this.name = name;
		this.repeat = repeat;
		this.sequence = sequence;
	}
	
	public EventScript(EventScript toCopy)
	{
		name = new String(toCopy.name);
		repeat = toCopy.repeat;
		sequence = new String(toCopy.sequence);
	}
	
	public String getName()
	{
		return name;
	}
	
	public EventScript setName(String name)
	{
		this.name = name;
		return this;
	}
	
	public boolean getRepeat()
	{
		return repeat;
	}
	
	public EventScript setRepeat(boolean repeat)
	{
		this.repeat = repeat;
		return this;
	}
	
	public String getSequence()
	{
		return sequence;
	}
	
	public EventScript setSequence(String sequence)
	{
		this.sequence = sequence;
		return this;
	}
}

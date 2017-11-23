package com.raphydaphy.raphybot.race;

import sx.blah.discord.handle.obj.IUser;

public class Bet
{
	private final IUser player;
	private final int amount;
	
	public Bet(IUser player, int amount)
	{
		this.player = player;
		this.amount = amount;
	}
	
	public IUser getPlayer()
	{
		return player;
	}
	
	public int getAmount()
	{
		return amount;
	}
}

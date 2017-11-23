package com.raphydaphy.raphybot.race;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.raphydaphy.raphybot.BotUtils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class Race
{
	private final List<Bet> bets;
	private int counter;
	private boolean raceStarted;
	private final IChannel channel;

	public Race(int time, IChannel channel)
	{
		bets = new ArrayList<>();
		counter = time;
		this.channel = channel;
		Timer timer = new Timer();
		timer.schedule(new RaceUpdater(), 0, 1000);
	}

	public boolean makeBet(IUser player, int amount)
	{
		if (!raceStarted)
		{
			for (Bet bet : bets)
			{
				if (bet.getPlayer().getLongID() == player.getLongID())
				{
					return false;
				}
			}
			bets.add(new Bet(player, amount));
			return true;
		}
		return false;
	}

	private class RaceUpdater extends TimerTask
	{
		@Override
		public void run()
		{
			if (counter > 0 && !raceStarted)
			{
				counter--;
				BotUtils.sendMessage(channel, "Race starting in " + counter + " seconds!");
			} else if (counter <= 0 && !raceStarted)
			{
				raceStarted = true;
				BotUtils.sendMessage(channel, "The race has started!");
			}
		}

	}
}

package com.raphydaphy.raphybot;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.raphydaphy.raphybot.race.Race;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils
{
	public static String PREFIX = "!";
	public static Map<Long, Integer> points = new HashMap<>();
	public static Map<Long, Race> races = new HashMap<>();
	public static Color messageColor = new Color(0, 0, 0);

	public static IDiscordClient getClient(String token)
	{
		return new ClientBuilder().withToken(token).build();
	}

	public static void sendMessage(IChannel channel, String message)
	{
		RequestBuffer.request(() ->
		{
			try
			{
				channel.sendMessage(message);
			} catch (DiscordException e)
			{
				System.err.println("There was an error sending a message. Printing stack trace... ");
				e.printStackTrace();
			}
		});
	}

	public static void sendMessage(IChannel channel, EmbedObject message)
	{
		RequestBuffer.request(() ->
		{
			try
			{
				channel.sendMessage(message);
			} catch (DiscordException e)
			{
				System.err.println("There was an error sending a message. Printing stack trace... ");
				e.printStackTrace();
			}
		});
	}

	public static int getPoints(IUser user)
	{
		if (points.containsKey(user.getLongID()))
		{
			return points.get(user.getLongID());
		}
		return 0;
	}

	public static void addPoints(IUser user, int amount)
	{
		if (user != null)
		{
			if (!points.containsKey(user.getLongID()))
			{
				points.put(user.getLongID(), 0);
			}

			points.put(user.getLongID(), getPoints(user) + amount);
		}
	}

	public static boolean usePoints(IUser user, int amount)
	{
		if (user != null)
		{
			if (getPoints(user) >= amount)
			{
				points.put(user.getLongID(), getPoints(user) - amount);
				return true;
			}
		}
		return false;
	}

	public static Race getRace(IChannel channel)
	{
		if (races.containsKey(channel.getLongID()))
		{
			return races.get(channel.getLongID());
		}
		return null;
	}

	public static void setRace(IChannel channel, Race race)
	{
		races.put(channel.getLongID(), race);
	}
}

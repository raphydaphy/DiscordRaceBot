package com.raphydaphy.raphybot.util;

import java.util.HashMap;
import java.util.Map;

import com.raphydaphy.raphybot.data.GuildData;
import com.raphydaphy.raphybot.race.Race;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils
{
	// All the guilds!
	public static Map<Long, GuildData> guilds = new HashMap<>();

	// Channel ID -> current race ongoing in the channel
	public static Map<Long, Race> races = new HashMap<>();

	public static GuildData getData(IGuild guild)
	{
		if (!guilds.containsKey(guild.getLongID()))
		{
			guilds.put(guild.getLongID(), new GuildData());
		}
		return guilds.get(guild.getLongID());
	}

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

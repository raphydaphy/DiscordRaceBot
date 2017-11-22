package com.raphydaphy.raphybot;

import java.util.HashMap;
import java.util.Map;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils 
{
	public static String PREFIX = "!";
	public static Map<Long, Integer> points = new HashMap<>();
	
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
			}
			catch (DiscordException e)
			{
				System.err.println("There was an error sending a message. Printing stack trace... ");
				e.printStackTrace();
			}
		});
	}
}

package com.raphydaphy.raphybot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

public class RaphyBot
{
	public static void main(String[] args) 
	{
		if (args.length < 1)
		{
			System.out.println("Invalid arguments provided, please include the token!");
			return;
		}
		IDiscordClient client = BotUtils.getClient(args[0]);
		
		client.getDispatcher().registerListener(new BotEvents());
		
		client.login();
		
		
		for (IUser user : client.getUsers())
		{
			BotUtils.points.put(user.getLongID(), 0);
		}
	}
}

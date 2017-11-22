package com.raphydaphy.raphybot;

import sx.blah.discord.api.IDiscordClient;

public class RaphyBot
{
	public static IDiscordClient client;

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Invalid arguments provided, please include the token!");
			return;
		}
		client = BotUtils.getClient(args[0]);

		client.getDispatcher().registerListener(new BotEvents());

		client.login();
	}
}

package com.raphydaphy.raphybot.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import com.raphydaphy.raphybot.RaphyBot;
import com.raphydaphy.raphybot.command.Command;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class BotEvents
{
	// User ID -> Milliseconds since last message
	public static Map<Long, Integer> timeSinceLastMsg = new HashMap<>();

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (timeSinceLastMsg.containsKey(event.getAuthor().getLongID()))
		{
			if (timeSinceLastMsg.get(event.getAuthor().getLongID()) >= 250)
			{
				if (!event.getAuthor().isBot() && RaphyBot.rand.nextInt(7) == 1)
				{
					BotUtils.getData(event.getGuild()).addPoints(event.getAuthor(), BigInteger.ONE);
				}
			}
		}

		if (event.getMessage().getContent()
				.startsWith(Character.toString(BotUtils.getData(event.getGuild()).getPrefix())))
		{
			String[] arguments = event.getMessage().getContent().split(" ");
			String[] shortArgs = new String[arguments.length - 1];
			arguments[0] = arguments[0].substring(1, arguments[0].length());

			for (int arg = 1; arg < arguments.length; arg++)
			{
				shortArgs[arg - 1] = arguments[arg];
			}

			for (Command command : Command.REGISTRY)
			{
				if (command.matches(arguments[0].toLowerCase()))
				{
					command.run(shortArgs, event);
				}
			}
		}
		timeSinceLastMsg.put(event.getAuthor().getLongID(), 0);
	}

	public static class OnTick extends TimerTask
	{
		@Override
		public void run()
		{
			for (long id : timeSinceLastMsg.keySet())
			{
				timeSinceLastMsg.put(id, timeSinceLastMsg.get(id) + 1);
			}
		}
	}
}

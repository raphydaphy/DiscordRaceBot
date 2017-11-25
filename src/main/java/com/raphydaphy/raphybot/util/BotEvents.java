package com.raphydaphy.raphybot.util;

import com.raphydaphy.raphybot.RaphyBot;
import com.raphydaphy.raphybot.command.Command;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class BotEvents
{
	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (!event.getAuthor().isBot() && RaphyBot.rand.nextInt(7) == 1)
		{
			BotUtils.getData(event.getGuild()).addPoints(event.getAuthor(), 1);
		}
		if (event.getMessage().getContent().startsWith(Character.toString(BotUtils.getData(event.getGuild()).getPrefix())))
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
					return;
				}
			}
		}
	}
}

package com.raphydaphy.raphybot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class BotEvents 
{
	
	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (event.getMessage().getContent().startsWith(BotUtils.PREFIX))
		{
			String[] arguments = event.getMessage().getContent().split(" ");
			
			arguments[0] = arguments[0].substring(1, arguments[0].length());
			
			String content = "";
			for (int word = 1; word < arguments.length; word++)
			{
				content += arguments[word];
			}
			
			if (arguments[0].equals("say"))
			{
				if (arguments.length > 1)
				{
					arguments[0] = "";
					BotUtils.sendMessage(event.getChannel(), content);
					return;
				}
			}
			else if (arguments[0].equals("points"))
			{
				if (arguments.length == 1)
				{
					BotUtils.sendMessage(event.getChannel(), event.getAuthor().getDisplayName(event.getGuild()) + " has " + BotUtils.points.get(event.getAuthor().getLongID()) + " points!");
				}
			}
		}
	}
}

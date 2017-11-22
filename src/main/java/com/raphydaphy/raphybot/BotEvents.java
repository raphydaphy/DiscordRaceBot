package com.raphydaphy.raphybot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

public class BotEvents 
{
	
	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (event.getMessage().getContent().startsWith(BotUtils.PREFIX))
		{
			long authorID = event.getAuthor().getLongID();
			String authorName = event.getAuthor().getDisplayName(event.getGuild());
			IChannel channel = event.getChannel();
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
			else if (arguments[0].equals("point"))
			{
				int authorPoints = BotUtils.points.get(authorID);
				if (arguments.length == 1 || arguments[1].equals("amount"))
				{
					BotUtils.sendMessage(channel, authorName + " has " + authorPoints + " points!");
					return;
				}
				else if (arguments[1].equals("give"))
				{
					if (arguments.length == 4)
					{
						try
						{
							BotUtils.points.put(authorID, authorPoints + Integer.valueOf(arguments[3]));
							BotUtils.sendMessage(channel, "Given " + arguments[3] + " points to " + authorName);
						}
						catch (NumberFormatException e)
						{
							BotUtils.sendMessage(channel, arguments[3] + " is not a number!");
						}
						return;
					}
				}
			}
		}
	}
}

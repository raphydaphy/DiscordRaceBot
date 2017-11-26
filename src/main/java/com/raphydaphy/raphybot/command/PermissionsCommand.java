package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;

public class PermissionsCommand extends Command
{

	public PermissionsCommand()
	{
		super("permissions", "Used to manage user's permission levels.");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (arguments.length > 0)
		{
			if (arguments[0].equals("list"))
			{
				if (arguments.length == 2)
				{
					BotUtils.sendMessage(event.getChannel(), BotUtils.getUserByString(arguments[1], event.getGuild()).toString());
				} else
				{
					BotUtils.sendMessage(event.getChannel(), invalidArgs(event.getGuild()));
				}
			}
			else
			{
				BotUtils.sendMessage(event.getChannel(), invalidArgs(event.getGuild()));
			}
		} else
		{
			BotUtils.sendMessage(event.getChannel(), invalidArgs(event.getGuild()));
		}
	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "The `" + getCommand(guild, true)
				+ "` command is used to view and change user's permissions within a guild.";
	}
}

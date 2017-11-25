package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

public class SetPrefixCommand extends Command
{

	public SetPrefixCommand()
	{
		super("setprefix", "Used to change my prefix!");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR))
		{
			if (arguments.length == 1)
			{
				char[] chars = arguments[0].toCharArray();
				if (chars.length == 1)
				{
					BotUtils.getData(event.getGuild()).setPrefix(chars[0]);
					BotUtils.sendMessage(event.getChannel(), event.getAuthor().getDisplayName(event.getGuild()) + " set the prefix to `" + chars[0] + "`");
				} else
				{
					BotUtils.sendMessage(event.getChannel(), "The prefix can only be one character!");
				}
			} else
			{
				BotUtils.sendMessage(event.getChannel(), "Invalid arguments! Expected one.");
			}
		} else
		{
			BotUtils.sendMessage(event.getChannel(), "You must be an administrator to set the prefix!");
		}
	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "Used to change the prefix used to summon me! Default is `$`, and Administrators on the server can use `"
				+ BotUtils.getData(guild).getPrefix() + getCommand() + " [prefix]` to change it!";
	}

}

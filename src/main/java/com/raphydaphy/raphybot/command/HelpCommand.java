package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand extends Command
{

	public HelpCommand()
	{
		super("help", "Lists the avalable commands and what they do.");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(BotUtils.getColor(event.getGuild()).getRed(), BotUtils.getColor(event.getGuild()).getGreen(),
				BotUtils.getColor(event.getGuild()).getBlue());

		boolean matches = false;
		if (arguments.length == 0)
		{
			String commandInfo = "";
			for (Command command : Command.REGISTRY)
			{
				commandInfo += BotUtils.getPrefix(event.getGuild()) + command.getCommand() + ": " + " " + command.getShortDesc() +"\n";
			}
			builder.appendField("Avalable Commands", commandInfo, true);
			matches = true;
		} else if (arguments.length == 1)
		{
			for (Command command : Command.REGISTRY)
			{
				if (command.matches(arguments[0]))
				{
					builder.appendField("Command Information", command.getInfo(event.getGuild()), true);
					matches = true;
					break;
				}
			}
		}
		if (!matches)
		{
			builder.appendField("Invalid Arguments", "You either tried to lookup an invalid command, or you used too many arguments.", true);
		}
		BotUtils.sendMessage(event.getChannel(), builder.build());
	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "`" + BotUtils.getPrefix(guild) + getCommand()
				+ "` can be used to gain information about a command. Using the command on its own will list all possible commands. If you want to learn about a specific commmand, use `"
				+ BotUtils.getPrefix(guild) + getCommand() + " [command]` to get some information.";
	}

}

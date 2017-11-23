package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand extends Command
{

	public HelpCommand()
	{
		super("help");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(BotUtils.messageColor.getRed(), BotUtils.messageColor.getGreen(),
				BotUtils.messageColor.getBlue());

		boolean matches = false;
		if (arguments.length == 0)
		{
			String commandInfo = "";
			for (Command command : Command.REGISTRY)
			{
				commandInfo += BotUtils.PREFIX + command.getCommand() + ": " + " does something useful!\n";
			}
			builder.appendField("Avalable Commands", commandInfo, true);
			matches = true;
		} else if (arguments.length == 1)
		{
			for (Command command : Command.REGISTRY)
			{
				if (command.matches(arguments[0]))
				{
					builder.appendField("Command Information", command.getInfo(), true);
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
	public String getInfo()
	{
		return "`" + BotUtils.PREFIX + getCommand()
				+ "` can be used to gain information about a command. Using the command on its own will list all possible commands. If you want to learn about a specific commmand, use `"
				+ BotUtils.PREFIX + getCommand() + " [command]` to get some information.";
	}

}

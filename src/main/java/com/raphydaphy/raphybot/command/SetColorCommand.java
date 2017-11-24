package com.raphydaphy.raphybot.command;

import java.awt.Color;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;

public class SetColorCommand extends Command
{

	public SetColorCommand()
	{
		super("setcolor", "Changes the color of bot text boxes.");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (arguments.length == 3)
		{
			int points = BotUtils.getPoints(event.getAuthor());
			if (points >= 150)
			{
				try
				{
					int r = Integer.valueOf(arguments[0]);
					int g = Integer.valueOf(arguments[1]);
					int b = Integer.valueOf(arguments[2]);

					BotUtils.colors.put(event.getGuild().getLongID(), new Color(r, g, b));

				} catch (NumberFormatException e)
				{
					BotUtils.sendMessage(event.getChannel(), "Color values must be an integer!");
					return;
				}

				EmbedBuilder builder = new EmbedBuilder();

				builder.withColor(BotUtils.getColor(event.getGuild()).getRed(), BotUtils.getColor(event.getGuild()).getGreen(),
						BotUtils.getColor(event.getGuild()).getBlue());

				builder.appendField("Color Changed!", "Operation consumed 150 points", true);

				BotUtils.sendMessage(event.getChannel(), builder.build());

				BotUtils.points.put(event.getAuthor().getLongID(), points - 150);
			} else
			{
				BotUtils.sendMessage(event.getChannel(),
						"You don't have enough points to change the color! Minimum 150.");
			}
			return;
		} else
		{
			BotUtils.sendMessage(event.getChannel(),
					"Invalid arguments! Expected: \n`" + BotUtils.getPrefix(event.getGuild()) + getCommand() + " [r] [g] [b]`");
		}
	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "For the cost of 150 points, you can use this command to change the color of all bot messages. The format is `"
				+ BotUtils.getPrefix(guild) + getCommand() + " [r] [g] [b]`";
	}

}

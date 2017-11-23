package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.race.Race;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class RaceCommand extends Command
{

	public RaceCommand()
	{
		super("race");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (BotUtils.getRace(event.getChannel()) != null && BotUtils.getRace(event.getChannel()).isFinished())
		{
			BotUtils.setRace(event.getChannel(), null);
		}
		if (arguments.length > 0)
		{
			if (arguments[0].toLowerCase().equals("start"))
			{
				if (BotUtils.getRace(event.getChannel()) == null)
				{
					int length = 120;
					if (arguments.length > 1)
					{
						try
						{
							length = Integer.valueOf(arguments[1]);
						} catch (NumberFormatException e)
						{
							BotUtils.sendMessage(event.getChannel(),
									event.getAuthor() + " tried to start a race in " + event.getChannel().mention()
											+ " with an invalid time limit! Defaulting to 120 seconds...");
						}
					}
					BotUtils.setRace(event.getChannel(), new Race(Math.min(length, 120), event.getChannel()));

					EmbedBuilder builder = new EmbedBuilder();
					builder.withColor(BotUtils.messageColor.getRed(), BotUtils.messageColor.getGreen(),
							BotUtils.messageColor.getBlue());
					builder.appendField("Race Started!",
							"A new race has begun in " + event.getChannel().mention() + ".", true);
					BotUtils.getRace(event.getChannel()).postNewMessage(builder.build());

					return;
				} else
				{
					BotUtils.sendMessage(event.getChannel(),
							"A race is already ongoing in " + event.getChannel().mention() + " !");
					return;
				}
			} else if (arguments[0].toLowerCase().equals("bet"))
			{
				if (BotUtils.getRace(event.getChannel()) != null)
				{
					int bet = 1;
					if (arguments.length > 1)
					{
						try
						{
							bet = Integer.valueOf(arguments[1]);
						} catch (NumberFormatException e)
						{
							BotUtils.sendMessage(event.getChannel(),
									event.getAuthor().getDisplayName(event.getGuild())
											+ " tried to bet an invalid amount on a race in "
											+ event.getChannel().mention() + " Defaulting to 1...");
						}
					}
					if (BotUtils.getRace(event.getChannel()).makeBet(event.getAuthor(), bet))
					{
						BotUtils.sendMessage(event.getChannel(), event.getAuthor().getDisplayName(event.getGuild())
								+ " bet " + bet + " on a race in " + event.getChannel().mention() + "!");
					} else
					{
						BotUtils.sendMessage(event.getChannel(), event.getAuthor().getDisplayName(event.getGuild())
								+ "'s bet could not be placed at this time!");
					}
					return;
				} else
				{
					BotUtils.sendMessage(event.getChannel(), "There is no race currently ongoing! Start one with `"
							+ BotUtils.PREFIX + getCommand() + " start`!");
					return;
				}
			} else if (arguments[0].toLowerCase().equals("help") || arguments[0].toLowerCase().equals("info"))
			{
				BotUtils.sendMessage(event.getChannel(), getInfo());
			}
		}
	}

	@Override
	public String getInfo()
	{
		return "Races are virtual competitions between other online players!\n\nYou can start a race with `"
				+ BotUtils.PREFIX + getCommand()
				+ " start [time]`, where the time you specify is the length of time allowed to place bets. The maximum this can be set to is 120, and all units are in seconds. Once a race has began, you can use `"
				+ BotUtils.PREFIX + getCommand()
				+ " bet [amount]` to bet your points in favor of yourself winning the race. You can only bet once per race, so place your bets wisely.";
	}

}

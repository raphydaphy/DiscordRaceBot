package com.raphydaphy.raphybot.command;

import java.math.BigInteger;

import com.raphydaphy.raphybot.race.Race;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

public class RaceCommand extends Command
{

	public RaceCommand()
	{
		super("race", "Have a race, to give you a sense of pride and acomplishment.");
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
					builder.withColor(BotUtils.getData(event.getChannel().getGuild()).getColor().getRed(),
							BotUtils.getData(event.getChannel().getGuild()).getColor().getGreen(),
							BotUtils.getData(event.getChannel().getGuild()).getColor().getBlue());
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
			} else if (arguments[0].toLowerCase().equals("cancel"))
			{
				if (BotUtils.getRace(event.getChannel()) != null && !BotUtils.getRace(event.getChannel()).isStarted()
						&& event.getAuthor().getPermissionsForGuild(event.getGuild())
								.contains(Permissions.ADMINISTRATOR))
				{
					BotUtils.getRace(event.getChannel()).refundAll();
					BotUtils.getRace(event.getChannel()).setFinished();
					BotUtils.setRace(event.getChannel(), null);
					BotUtils.sendMessage(event.getChannel(),
							event.getAuthor().getDisplayName(event.getGuild()) + " cancelled the race!");
				}
			} else if (arguments[0].toLowerCase().equals("force"))
			{
				if (BotUtils.getRace(event.getChannel()) != null && !BotUtils.getRace(event.getChannel()).isStarted())
				{
					if (BotUtils.getData(event.getGuild()).getPoints(event.getAuthor()).compareTo(BigInteger.valueOf(50)) >= 0)
					{
						if (BotUtils.getRace(event.getChannel()).forceStart(true))
						{
							BotUtils.getData(event.getGuild()).usePoints(event.getAuthor(), BigInteger.valueOf(50));
							BotUtils.sendMessage(event.getChannel(), event.getAuthor().getDisplayName(event.getGuild())
									+ " force started a race in " + event.getChannel().mention() + " for 50 points!");
							return;
						}
					} else
					{
						BotUtils.sendMessage(event.getChannel(), "You need at least 50 points to force start a race!");
						return;
					}
				}
			} else if (arguments[0].toLowerCase().equals("bet"))
			{
				if (BotUtils.getRace(event.getChannel()) != null)
				{
					BigInteger bet = BigInteger.ONE;
					if (arguments.length > 1)
					{
						try
						{
							bet = new BigInteger(arguments[1]);
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
							+ getCommand(event.getGuild(), true) + " start`!");
					return;
				}
			} else if (arguments[0].toLowerCase().equals("help") || arguments[0].toLowerCase().equals("info"))
			{
				BotUtils.sendMessage(event.getChannel(), getInfo(event.getGuild()));
			}
		}
	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "Races are virtual competitions between other online players!\n\nYou can start a race with `"
				+ getCommand(guild, true)
				+ " start [time]`, where the time you specify is the length of time allowed to place bets. The maximum this can be set to is 120, and all units are in seconds. Once a race has began, you can use `"
				+ getCommand(guild, true)
				+ " bet [amount]` to bet your points in favor of yourself winning the race. You can only bet once per race, so place your bets wisely.\n\nAdministrators can cancel the current race as long as the betting has not closed yet, using `"
				+ getCommand(guild, true)
				+ " cancel`, and any user can forcefully start a race during the betting period using `"
				+ getCommand(guild, true)
				+ " force`, but 50 points will be consumed when running the command, so use it only when you must.";
	}

}

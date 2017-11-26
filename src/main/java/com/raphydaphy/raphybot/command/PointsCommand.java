package com.raphydaphy.raphybot.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.raphydaphy.raphybot.RaphyBot;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class PointsCommand extends Command
{

	public PointsCommand()
	{
		super("points", "Used to manage your point balance");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (arguments.length == 0 || arguments[0].toLowerCase().equals("amount"))
		{
			IUser of = event.getAuthor();
			if (arguments.length == 2)
			{
				of = BotUtils.getUserByString(arguments[1], event.getGuild());
			}
			if (of != null)
			{
				BotUtils.sendMessage(event.getChannel(), of.getDisplayName(event.getGuild()) + " has "
						+ BotUtils.getData(event.getGuild()).getPoints(of) + " points!");
			} else
			{
				BotUtils.sendMessage(event.getChannel(), "The user you specified could not be found!");
			}
			return;
		} else if (arguments[0].toLowerCase().equals("lb"))
		{
			boolean onlyOnline = arguments.length > 1 && arguments[1].equals("online");

			EmbedBuilder builder = new EmbedBuilder();

			builder.withColor(BotUtils.getData(event.getChannel().getGuild()).getColor().getRed(),
					BotUtils.getData(event.getChannel().getGuild()).getColor().getGreen(),
					BotUtils.getData(event.getChannel().getGuild()).getColor().getBlue());
			if (onlyOnline)
			{
				builder.withDescription("Only Online Users");
			}

			String people = "";
			Comparator<IUser> userSorter = new Comparator<IUser>()
			{
				@Override
				public int compare(IUser o1, IUser o2)
				{
					return BotUtils.getData(event.getGuild()).getPoints(o2)
							- BotUtils.getData(event.getGuild()).getPoints(o1);
				}
			};
			List<IUser> users = new ArrayList<>();
			for (Long id : BotUtils.getData(event.getGuild()).points.keySet())
			{
				users.add(RaphyBot.client.getUserByID(id));
			}
			Collections.sort(users, userSorter);
			for (IUser user : users)
			{
				if ((!user.getPresence().getStatus().equals(StatusType.OFFLINE)) || !onlyOnline)
				{
					int points = BotUtils.getData(event.getGuild()).getPoints(user);
					if (points > 0)
					{
						people += user.getName() + " -> " + BotUtils.getData(event.getGuild()).getPoints(user) + "\n";
					}
				}

			}
			if (people.isEmpty())
			{
				people = "There is noone on the leaderboard yet!";
			}
			builder.appendField("Leaderboard", people, true);
			RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
			return;
		} else if (arguments[0].toLowerCase().equals("give"))
		{
			if (event.getAuthor().getStringID().equals("231774499027156993"))
			{
				if (arguments.length == 3)
				{
					IUser user = BotUtils.getUserByString(arguments[1], event.getGuild());
					if (user != null)
					{
						try
						{
							BotUtils.getData(event.getGuild()).addPoints(user, Integer.valueOf(arguments[2]));
							BotUtils.sendMessage(event.getChannel(),
									"Given " + arguments[2] + " points to " + user.getDisplayName(event.getGuild()));
						} catch (NumberFormatException e)
						{
							BotUtils.sendMessage(event.getChannel(), arguments[2] + " is not a number!");
						}

					} else
					{
						BotUtils.sendMessage(event.getChannel(), "Could not find the specified user!");
					}
					return;
				}
			} else
			{
				BotUtils.sendMessage(event.getChannel(), "You do not have permission to use this command!");
			}
		} else
		{
			BotUtils.sendMessage(event.getChannel(), "Invalid arguments. Valid options for `"
					+ getCommand(event.getGuild(), true) + "` are:\n`amount`, `lb`, `give`");
			return;
		}

	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "The `" + getCommand(guild, true)
				+ "` command is used to check your virtual points balance. Using the command with no arguments, or `!points amount`, will inform you of your current balance in points. `"
				+ getCommand(guild, true) + " lb` displays a leaderboard with the richest users of all time, and `"
				+ getCommand(guild, true)
				+ " give [user] [amount]` will add the specified amount of points to the users balance, as long as the command sender has the required permissions.";
	}
}

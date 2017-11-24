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
		int authorPoints = 0;
		if (BotUtils.points.containsKey(event.getAuthor().getLongID()))
		{
			authorPoints = BotUtils.points.get(event.getAuthor().getLongID());
		}
		if (arguments.length == 0 || arguments[0].toLowerCase().equals("amount"))
		{
			BotUtils.sendMessage(event.getChannel(),
					event.getAuthor().getDisplayName(event.getGuild()) + " has " + authorPoints + " points!");
			return;
		} else if (arguments[0].toLowerCase().equals("lb"))
		{
			boolean onlyOnline = arguments.length > 1 && arguments[1].equals("online");

			EmbedBuilder builder = new EmbedBuilder();

			builder.withColor(BotUtils.getColor(event.getGuild()).getRed(), BotUtils.getColor(event.getGuild()).getGreen(),
					BotUtils.getColor(event.getGuild()).getBlue());
			if (onlyOnline)
			{
				builder.withDescription("Only Online Users");
			}

			String people = "";
			Comparator<IUser> userSorter=new Comparator<IUser>()
			{
				@Override
				public int compare(IUser o1, IUser o2) 
				{
				    return BotUtils.getPoints(o2) -  BotUtils.getPoints(o1);
				}
			};
			List<IUser> users = new ArrayList<>();
			for (IUser user : RaphyBot.client.getUsers())
			{
				users.add(user);
			}
			Collections.sort(users, userSorter);
			for (IUser user : users)
			{
				if ((!user.getPresence().getStatus().equals(StatusType.OFFLINE)) || !onlyOnline)
				{
					int points = BotUtils.getPoints(user);
					if (points > 0)
					{
						people += user.getName() + " -> " + BotUtils.getPoints(user) + "\n";
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
					List<IUser> specifiedUsers = RaphyBot.client.getUsersByName(arguments[1]);

					if (specifiedUsers.size() > 0)
					{
						for (IUser user : specifiedUsers)
						{
							try
							{
								BotUtils.addPoints(user, Integer.valueOf(arguments[2]));
								BotUtils.sendMessage(event.getChannel(),
										"Given " + arguments[2] + " points to " + arguments[1]);
							} catch (NumberFormatException e)
							{
								BotUtils.sendMessage(event.getChannel(), arguments[2] + " is not a number!");
							}
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
			BotUtils.sendMessage(event.getChannel(),
					"Invalid arguments. Valid options for `" + BotUtils.getPrefix(event.getGuild()) + getCommand() + "` are:\n`amount`, `lb`, `give`");
			return;
		}

	}

	@Override
	public String getInfo(IGuild guild)
	{
		return "The `" + BotUtils.getPrefix(guild) + getCommand()
				+ "` command is used to check your virtual points balance. Using the command with no arguments, or `!points amount`, will inform you of your current balance in points. `!points lb` displays a leaderboard with the richest users of all time, and `!points give [user] [amount]` will add the specified amount of points to the users balance, as long as the command sender has the required permissions.";
	}
}

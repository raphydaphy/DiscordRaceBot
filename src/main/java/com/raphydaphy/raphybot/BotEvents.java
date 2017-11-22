package com.raphydaphy.raphybot;

import java.util.List;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

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
			} else if (arguments[0].equals("spam"))
			{
				for (int i = 0; i < 10; i++)
				{
					BotUtils.sendMessage(channel, "hello!");
				}
			} else if (arguments[0].equals("points"))
			{
				int authorPoints = 0;
				if (BotUtils.points.containsKey(authorID))
				{
					authorPoints = BotUtils.points.get(authorID);
				}
				if (arguments.length == 1 || arguments[1].equals("amount"))
				{
					BotUtils.sendMessage(channel, authorName + " has " + authorPoints + " points!");
					return;
				} else if (arguments[1].equals("lb"))
				{
					boolean onlyOnline = arguments.length > 2 && arguments[2].equals("online");

					EmbedBuilder builder = new EmbedBuilder();

					builder.withColor(255, 0, 0);
					if (onlyOnline)
					{
						builder.withDescription("Only Online Users");
					}
					

					
					String people = "";
					for (IUser user : RaphyBot.client.getUsers())
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
					builder.appendField("Leaderboard", people, true);
					RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
					return;
				} else if (arguments[1].equals("give"))
				{
					if (arguments.length == 4)
					{
						List<IUser> specifiedUsers = RaphyBot.client.getUsersByName(arguments[2]);

						if (specifiedUsers.size() > 0)
						{
							if (!(BotUtils.points.containsKey(specifiedUsers.get(0).getLongID())))
							{
								BotUtils.points.put(specifiedUsers.get(0).getLongID(), 0);
							}
							try
							{
								BotUtils.points.put(specifiedUsers.get(0).getLongID(),
										BotUtils.points.get(specifiedUsers.get(0).getLongID())
												+ Integer.valueOf(arguments[3]));
								BotUtils.sendMessage(channel, "Given " + arguments[3] + " points to " + arguments[2]);
							} catch (NumberFormatException e)
							{
								BotUtils.sendMessage(channel, arguments[3] + " is not a number!");
							}
						} else
						{
							BotUtils.sendMessage(channel, "Could not find the specified user!");
						}
						return;
					}
				} else
				{
					BotUtils.sendMessage(channel,
							"Invalid arguments. Valid options for `!points` are:\n`amount`, `lb`, `give`");
					return;
				}
			} else
			{
				if (!arguments[1].equals("exec") && !arguments[1].equals("commands") && !arguments[1].equals("lmgtfy")
						&& !arguments[1].equals("cf") && !arguments[1].equals("drama") && !arguments[1].equals("help")
						&& !arguments[1].equals("kickclear") && !arguments[1].equals("mcpc")
						&& !arguments[1].equals("mcpm") && !arguments[1].equals("mcpf") && !arguments[1].equals("mcpv")
						&& !arguments[1].equals("ping") && !arguments[1].equals("quote")
						&& !arguments[1].equals("slap"))
				{
					BotUtils.sendMessage(channel, "Invalid command. Valid options are listed below:\n`say`, `points`");
				}
				return;
			}
		}
	}
}

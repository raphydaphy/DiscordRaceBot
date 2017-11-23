package com.raphydaphy.raphybot;

import java.awt.Color;
import java.util.List;

import com.raphydaphy.raphybot.race.Race;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class BotEvents
{
	private Race curRace = null;
	
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
			for (int word = 0; word < arguments.length; word++)
			{
				if (word > 0)
				{
					content += arguments[word] + " ";
				}
			}
			if (arguments[0].toLowerCase().equals("say"))
			{
				if (arguments.length > 1)
				{
					arguments[0] = "";
					BotUtils.sendMessage(event.getChannel(), content);
					return;
				}
			} else if (arguments[0].toLowerCase().equals("setcolor"))
			{
				if (arguments.length == 4)
				{
					int points = BotUtils.getPoints(event.getAuthor());
					if (points >= 150)
					{
						try
						{
							int r = Integer.valueOf(arguments[1]);
							int g = Integer.valueOf(arguments[2]);
							int b = Integer.valueOf(arguments[3]);

							BotUtils.messageColor = new Color(r, g, b);

						} catch (NumberFormatException e)
						{
							BotUtils.sendMessage(channel, "Color values must be an integer!");
							return;
						}

						EmbedBuilder builder = new EmbedBuilder();

						builder.withColor(BotUtils.messageColor.getRed(), BotUtils.messageColor.getGreen(),
								BotUtils.messageColor.getBlue());

						builder.appendField("Color Changed!", "Operation consumed 150 points", true);

						RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

						BotUtils.points.put(event.getAuthor().getLongID(), points - 150);
					} else
					{
						BotUtils.sendMessage(channel, "You don't have enough points to change the color! Minimum 150.");
					}
					return;
				} else
				{
					BotUtils.sendMessage(channel, "Invalid arguments! Expected: \n`!setcolor [r] [g] [b]`");
				}
			} else if (arguments[0].toLowerCase().equals("points"))
			{
				int authorPoints = 0;
				if (BotUtils.points.containsKey(authorID))
				{
					authorPoints = BotUtils.points.get(authorID);
				}
				if (arguments.length == 1 || arguments[1].toLowerCase().equals("amount"))
				{
					BotUtils.sendMessage(channel, authorName + " has " + authorPoints + " points!");
					return;
				} else if (arguments[1].toLowerCase().equals("lb"))
				{
					boolean onlyOnline = arguments.length > 2 && arguments[2].equals("online");

					EmbedBuilder builder = new EmbedBuilder();

					builder.withColor(BotUtils.messageColor.getRed(), BotUtils.messageColor.getGreen(),
							BotUtils.messageColor.getBlue());
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
					if (people.isEmpty())
					{
						people = "There is noone on the leaderboard yet!";
					}
					builder.appendField("Leaderboard", people, true);
					RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
					return;
				} else if (arguments[1].toLowerCase().equals("give"))
				{
					if (arguments.length == 4)
					{
						List<IUser> specifiedUsers = RaphyBot.client.getUsersByName(arguments[2]);

						if (specifiedUsers.size() > 0)
						{
							for (IUser user : specifiedUsers)
							{
								try
								{
									BotUtils.addPoints(user, Integer.valueOf(arguments[3]));
									BotUtils.sendMessage(channel, "Given " + arguments[3] + " points to " + arguments[2]);
								} catch (NumberFormatException e)
								{
									BotUtils.sendMessage(channel, arguments[3] + " is not a number!");
								}
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
			} else if (arguments[0].toLowerCase().equals("race"))
			{
				if (arguments.length > 1)
				{
					if (arguments[1].toLowerCase().equals("start"))
					{
						if (curRace == null)
						{
							int length = 120;
							if (arguments.length > 2)
							{
								try
								{
									length = Integer.valueOf(arguments[2]);
								}
								catch (NumberFormatException e)
								{
									BotUtils.sendMessage(channel, authorName + " tried to start a race in " + channel.toString() + " with an invalid time limit! Defaulting to 120 seconds...");
								}
							}
							curRace = new Race(length, channel);
							BotUtils.sendMessage(channel, "A new race has been started in " + channel.toString() + " by " + authorName);
							return;
						}
						else
						{
							BotUtils.sendMessage(channel, "A race is already ongoing in " + channel.toString() + " !");
							return;
						}
					} else if (arguments[1].toLowerCase().equals("bet"))
					{
						if (curRace != null)
						{
							int bet = 1;
							if (arguments.length > 2)
							{
								try
								{
									bet = Integer.valueOf(arguments[2]);
								}
								catch (NumberFormatException e)
								{
									BotUtils.sendMessage(channel, authorName + " tried to bet an invalid amount on a race in " + channel.toString() + " Defaulting to 1...");
								}
							}
							if (curRace.makeBet(event.getAuthor(), bet))
							{
								BotUtils.sendMessage(channel, authorName + " bet " + bet + " on a race in " + channel.toString() + "!");
							}
							else
							{
								BotUtils.sendMessage(channel, authorName + "'s bet could not be placed at this time!");
							}
							return;
						}
						else
						{
							BotUtils.sendMessage(channel, "There is no race currently ongoing! Start one with `!race start`!");
							return;
						}
					}
				}
			} else if (arguments[0].toLowerCase().equals("help"))
			{
				BotUtils.sendMessage(channel, "Valid commands are listed below:\n`say`, `points`, `setcolor`, `race`");

				return;
			}
		}
	}
}

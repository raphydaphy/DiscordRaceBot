package com.raphydaphy.raphybot.race;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.raphydaphy.raphybot.RaphyBot;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Race
{
	private final List<Bet> bets;
	private int counter;
	private boolean raceStarted;
	private final IChannel channel;
	private IMessage raceInfo;
	private Timer raceTimer;
	private boolean isFinished;

	public Race(int time, IChannel channel)
	{
		bets = new ArrayList<>();
		counter = time;
		this.channel = channel;
		raceStarted = false;
		raceTimer = new Timer();
		isFinished = false;
		raceTimer.schedule(new RaceUpdater(), 0, 1000);

		if (!makeBet(RaphyBot.client.getOurUser(),
				BotUtils.getData(channel.getGuild()).getPoints(RaphyBot.client.getOurUser()).add(BigInteger.valueOf(1))
						.divide(BigInteger.valueOf(1000)).multiply(BigInteger.valueOf((int) (Math.random() * 1000)))))
		{
			bets.add(new Bet(RaphyBot.client.getOurUser(), BigInteger.valueOf(RaphyBot.rand.nextInt(10)),
					RaphyBot.rand));
		}
	}

	public void postNewMessage(EmbedObject message)
	{
		if (getRaceMessage() != null)
		{
			getRaceMessage().delete();
		}
		RequestBuffer.request(() ->
		{
			try
			{
				setRaceMessage(channel.sendMessage(message));
			} catch (DiscordException e)
			{
				System.err.println("There was an error sending a message. Printing stack trace... ");
				e.printStackTrace();
			}
		});
	}

	public boolean makeBet(IUser player, BigInteger amount)
	{
		if (!raceStarted)
		{
			for (Bet bet : bets)
			{
				if (bet.getPlayer().getLongID() == player.getLongID())
				{
					return false;
				}
			}
			if (BotUtils.getData(channel.getGuild()).usePoints(player, amount.abs()))
			{
				bets.add(new Bet(player, amount.abs(), RaphyBot.rand));
				return true;
			}
		}
		return false;
	}

	public IMessage getRaceMessage()
	{
		return raceInfo;
	}

	public void setRaceMessage(IMessage raceMessage)
	{
		this.raceInfo = raceMessage;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public void setFinished()
	{
		this.isFinished = true;
	}

	public boolean isStarted()
	{
		return raceStarted;
	}

	public void refundAll()
	{
		for (Bet bet : bets)
		{
			BotUtils.getData(channel.getGuild()).addPoints(bet.getPlayer(), bet.getAmount().abs());
		}
	}

	public boolean forceStart(boolean ignoreFail)
	{
		if (bets.size() >= 2)
		{
			counter = 0;
			raceStarted = true;

			EmbedBuilder builder = new EmbedBuilder();
			builder.withColor(BotUtils.getData(channel.getGuild()).getColor().getRed(),
					BotUtils.getData(channel.getGuild()).getColor().getGreen(),
					BotUtils.getData(channel.getGuild()).getColor().getBlue());
			builder.appendField("Race Started!", "Good luck...", true);
			postNewMessage(builder.build());
			return true;
		} else if (!ignoreFail)
		{

			raceStarted = true;
			refundAll();
			isFinished = true;
			raceTimer.cancel();

		}
		BotUtils.sendMessage(channel, "Not enough players for the race to start! Minimum 2.");
		return false;
	}

	private class RaceUpdater extends TimerTask
	{
		@Override
		public void run()
		{
			if (counter > 0 && !raceStarted && !isFinished)
			{
				counter--;

				if (counter % 5 == 0 && raceInfo != null)
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.withColor(BotUtils.getData(channel.getGuild()).getColor().getRed(),
							BotUtils.getData(channel.getGuild()).getColor().getGreen(),
							BotUtils.getData(channel.getGuild()).getColor().getBlue());
					String betInfo = "";
					for (Bet bet : bets)
					{
						String name = bet.getPlayer().getDisplayName(channel.getGuild());
						if (bet.getPlayer().getLongID() == RaphyBot.client.getOurUser().getLongID())
						{

							name = bet.getPlayer().getName();
						}
						betInfo += bet.getIcon() + " " + name + ": " + bet.getAmount() + "\n";
					}
					if (bets.isEmpty())
					{
						betInfo = "Noone has placed a bet yet! Use `!race bet [amount]` to be the first :D";
					}
					builder.withTitle("Betting Open");
					builder.withDesc(counter + " seconds to go!");
					builder.appendField("Bets", betInfo, false);

					if (counter == 0)
					{
						postNewMessage(builder.build());
					} else
					{
						raceInfo.edit(builder.build());
					}
				}
			} else if (counter <= 0 && !raceStarted && !isFinished)
			{
				forceStart(false);
			} else if (raceStarted && !isFinished)
			{
				counter++;
				if (counter % 3 == 0)
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.withColor(BotUtils.getData(channel.getGuild()).getColor().getRed(),
							BotUtils.getData(channel.getGuild()).getColor().getGreen(),
							BotUtils.getData(channel.getGuild()).getColor().getBlue());
					BigInteger pot = BigInteger.ZERO;
					float winnerPercent = 0;
					String betInfo = "";
					IUser winner = null;
					BigInteger highestBet = BigInteger.ZERO;
					for (Bet bet : bets)
					{
						if (bet.getAmount().compareTo(highestBet) > 0)
						{
							highestBet = bet.getAmount();
						}
					}
					BigInteger top = highestBet.min(BigInteger.valueOf(100));
					for (Bet bet : bets)
					{
						float percent = (bet.getAmount().compareTo(top) >= 0 ? 100
								: (((float) bet.getAmount().intValueExact() / (float) top.intValue()) * 100));
						bet.setProgress(Math.min(bet.getProgress() + RaphyBot.rand.nextInt(5), 24));

						pot = pot.add(bet.getAmount().multiply(BigInteger.valueOf(2)));
						String progressLine = "";
						for (int i = 0; i < 25; i++)
						{
							if (bet.getProgress() == i)
							{
								progressLine += bet.getIcon();
							} else
							{
								progressLine += "=";
							}
						}
						String name = bet.getPlayer().getDisplayName(channel.getGuild());
						if (bet.getPlayer().getLongID() == RaphyBot.client.getOurUser().getLongID())
						{

							name = bet.getPlayer().getName();
						}
						progressLine += "| " + name + " (" + (int) percent + "%) " + "\n";
						betInfo += progressLine;

						if (bet.getProgress() >= 24)
						{
							winner = bet.getPlayer();
							winnerPercent = percent;
						}
					}
					BigDecimal winnings = new BigDecimal(pot).multiply(BigDecimal.valueOf(winnerPercent / 100));
					if (winner != null)
					{
						BotUtils.getData(channel.getGuild()).addPoints(winner, winnings.toBigInteger());

						builder.withAuthorName("Winner: " + winner.getDisplayName(channel.getGuild()));
						builder.withAuthorIcon(winner.getAvatarURL());
					} else
					{
						builder.withTitle("Race Active!");
					}
					builder.withDesc(betInfo + "Pot: " + pot);
					raceInfo.edit(builder.build());

					if (winner != null)
					{
						BotUtils.sendMessage(channel, winner.getDisplayName(channel.getGuild()) + " won the race for "
								+ winnings.toBigInteger() + " points!");

						isFinished = true;
					}

				}
			} else
			{
				raceTimer.cancel();
				isFinished = true;
			}
		}

	}
}

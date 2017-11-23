package com.raphydaphy.raphybot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.raphydaphy.raphybot.command.Command;
import com.raphydaphy.raphybot.command.HelpCommand;
import com.raphydaphy.raphybot.command.PointsCommand;
import com.raphydaphy.raphybot.command.RaceCommand;
import com.raphydaphy.raphybot.command.SayCommand;
import com.raphydaphy.raphybot.command.SetColorCommand;
import com.raphydaphy.raphybot.util.BotEvents;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.api.IDiscordClient;

public class RaphyBot
{
	public static final String SAVE_FILE = "data.txt";
	public static Random rand;
	public static IDiscordClient client;

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Invalid arguments provided, please include the token!");
			return;
		}

		client = BotUtils.getClient(args[0]);
		client.getDispatcher().registerListener(new BotEvents());
		client.login();

		readData();
		new Timer().schedule(new SaveFunc(), 0, 30000);
		
		rand = new Random();
		
		Command.REGISTRY.add(new PointsCommand());
		Command.REGISTRY.add(new RaceCommand());
		Command.REGISTRY.add(new SayCommand());
		Command.REGISTRY.add(new SetColorCommand());
		Command.REGISTRY.add(new HelpCommand());
	}

	public static class SaveFunc extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE, false));

				for (long userID : BotUtils.points.keySet())
				{
					writer.write(userID + "=" + BotUtils.points.get(userID));
					writer.newLine();
				}
				writer.close();
				System.out.println("Saved data!");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void readData()
	{
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(SAVE_FILE));
			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
			{
				lines.add(line);
			}
			bufferedReader.close();
			for (String entry : lines.toArray(new String[lines.size()]))
			{
				String[] parts = entry.split("=");
				if (parts.length != 2)
				{
					System.err.println("Malformed save file found, skipping corrupt entry.");
					continue;
				}
				long userID = Long.valueOf(parts[0]);
				int points = Integer.valueOf(parts[1]);
				
				BotUtils.points.put(userID, points);
			}
			
			System.out.println("Loaded data!");
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (NumberFormatException e)
		{
			System.err.println("Invalid data found in save file, data loading process abruptly stopped.");
			e.printStackTrace();
		}
	}
}

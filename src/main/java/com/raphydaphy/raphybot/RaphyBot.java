package com.raphydaphy.raphybot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raphydaphy.raphybot.command.Command;
import com.raphydaphy.raphybot.command.HelpCommand;
import com.raphydaphy.raphybot.command.PermissionsCommand;
import com.raphydaphy.raphybot.command.PointsCommand;
import com.raphydaphy.raphybot.command.RaceCommand;
import com.raphydaphy.raphybot.command.SayCommand;
import com.raphydaphy.raphybot.command.SetColorCommand;
import com.raphydaphy.raphybot.command.SetPrefixCommand;
import com.raphydaphy.raphybot.data.GuildData;
import com.raphydaphy.raphybot.util.BotEvents;
import com.raphydaphy.raphybot.util.BotEvents.OnTick;
import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class RaphyBot
{
	public static final String SAVE_FOLDER = "data/";
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
		new Timer().schedule(new OnTick(), 0, 10);

		rand = new Random();

		Command.REGISTRY.add(new PointsCommand());
		Command.REGISTRY.add(new RaceCommand());
		Command.REGISTRY.add(new SayCommand());
		Command.REGISTRY.add(new SetColorCommand());
		Command.REGISTRY.add(new SetPrefixCommand());
		Command.REGISTRY.add(new PermissionsCommand());
		Command.REGISTRY.add(new HelpCommand());

		for (IGuild guild : client.getGuilds())
		{
			guild.setUserNickname(RaphyBot.client.getOurUser(), "RaphyBot - " + BotUtils.getData(guild).getPrefix() + "help");
		}
	}

	public static class SaveFunc extends TimerTask
	{
		@Override
		public void run()
		{
			JsonObject json = new JsonObject();
			for (long longID : BotUtils.guilds.keySet())
			{
				json.add(String.valueOf(longID), BotUtils.guilds.get(longID).toJson());
			}

			try
			{
				FileWriter writer = new FileWriter(SAVE_FOLDER + "guild_data.json");
				writer.write(json.toString());
				writer.flush();
				writer.close();
			} catch (IOException e)
			{
				System.err.print("There was an issue when saving guild data! Printing stack trace...");
				e.printStackTrace();
			}
			System.out.println("Saved Data!");
		}
	}

	public static void readData()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(SAVE_FOLDER + "guild_data.json"));
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(br).getAsJsonObject();

			for (Map.Entry<String, JsonElement> guildJson : jsonObject.entrySet())
			{
				BotUtils.guilds.put(Long.valueOf(guildJson.getKey()),
						GuildData.fromJson(guildJson.getValue().getAsJsonObject()));

			}
			System.out.println("Loaded data!");

		} catch (FileNotFoundException e)
		{
			System.out.println("Bot was used for the first time or the save file was deleted!");
		}
	}
}

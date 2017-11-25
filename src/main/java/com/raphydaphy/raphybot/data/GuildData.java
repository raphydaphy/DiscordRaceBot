package com.raphydaphy.raphybot.data;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raphydaphy.raphybot.RaphyBot;

import sx.blah.discord.handle.obj.IUser;

public class GuildData
{
	// Prefix used to summon the bot
	private Character prefix = '$';

	// Color used for messages in the guild
	private Color color = new Color(0, 0, 0);

	// User ID -> amount of points the user has in the guild
	public static Map<Long, Integer> points = new HashMap<>();

	public GuildData setPrefix(Character prefix)
	{
		this.prefix = prefix;
		return this;
	}

	public Character getPrefix()
	{
		return prefix;
	}

	public GuildData setColor(Color color)
	{
		this.color = color;
		return this;
	}

	public Color getColor()
	{
		return color;
	}

	public int getPoints(IUser user)
	{
		if (!points.containsKey(user.getLongID()))
		{
			points.put(user.getLongID(), 0);
		}
		return points.get(user.getLongID());
	}

	public int addPoints(IUser user, int amount)
	{
		int curPoints = getPoints(user) + amount;
		points.put(user.getLongID(), curPoints);
		return curPoints;
	}

	public GuildData setPoints(IUser user, int amount)
	{
		points.put(user.getLongID(), amount);
		return this;
	}

	public boolean usePoints(IUser user, int amount)
	{
		int curPoints = getPoints(user);
		if (curPoints >= amount)
		{
			points.put(user.getLongID(), curPoints - amount);
			return true;
		}
		return false;
	}

	public JsonObject toJson()
	{
		JsonObject mainJson = new JsonObject();
		mainJson.addProperty("prefix", prefix);
		JsonArray colorJson = new JsonArray();
		colorJson.add(color.getRed());
		colorJson.add(color.getGreen());
		colorJson.add(color.getBlue());
		mainJson.add("color", colorJson);
		JsonObject pointsJson = new JsonObject();
		for (long longID : points.keySet())
		{
			pointsJson.addProperty(RaphyBot.client.getUserByID(longID).getStringID(), points.get(longID));
		}
		mainJson.add("points", pointsJson);
		return mainJson;
	}

	public static GuildData fromJson(JsonObject mainJson)
	{
		GuildData data = new GuildData();
		if (mainJson.has("prefix"))
		{
			data.setPrefix(mainJson.get("prefix").getAsCharacter());
		}
		if (mainJson.has("color"))
		{
			JsonArray colorJson = mainJson.get("color").getAsJsonArray();

			data.setColor(
					new Color(colorJson.get(0).getAsInt(), colorJson.get(1).getAsInt(), colorJson.get(2).getAsInt()));
		}
		if (mainJson.has("points"))
		{
			JsonObject pointsJson = mainJson.get("points").getAsJsonObject();
			for (Map.Entry<String, JsonElement> pointJson : pointsJson.entrySet())
			{
				@SuppressWarnings("deprecation")
				IUser user = RaphyBot.client.getUserByID(pointJson.getKey());
				int points = pointJson.getValue().getAsInt();
				
				data.setPoints(user, points);
			}
		}
		return data;
	}
}

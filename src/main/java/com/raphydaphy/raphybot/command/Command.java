package com.raphydaphy.raphybot.command;

import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class Command
{
	private final String command;
	private final String shortDesc;
	public static final List<Command> REGISTRY = new ArrayList<>();

	public Command(String command, String shortDesc)
	{
		this.command = command;
		this.shortDesc = shortDesc;
	}

	public boolean matches(String argument)
	{
		return argument.equals(command);
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public String getShortDesc()
	{
		return shortDesc;
	}

	public abstract void run(String[] arguments, MessageReceivedEvent event);
	public abstract String getInfo();
	
	public static String getContent(String[] arguments)
	{
		String content = "";
		for (int word = 0; word < arguments.length; word++)
		{
			content += arguments[word] + " ";

		}
		return content;
	}
}

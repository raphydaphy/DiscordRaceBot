package com.raphydaphy.raphybot.command;

import com.raphydaphy.raphybot.util.BotUtils;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class SayCommand extends Command
{

	public SayCommand()
	{
		super("say", "What do you think this does?");
	}

	@Override
	public void run(String[] arguments, MessageReceivedEvent event)
	{
		if (arguments.length > 0)
		{
			arguments[0] = "";
			if (getContent(arguments).contains("@here") || getContent(arguments).contains("@everyone"))
			{
				BotUtils.sendMessage(event.getChannel(), "No.");
				return;
			}
			BotUtils.sendMessage(event.getChannel(), getContent(arguments));
			return;
		}
	}

	@Override
	public String getInfo()
	{
		return "This is a simple command that causes me to respond back with whatever you wish me to. Use `" + BotUtils.PREFIX + getCommand() + " [message] to invoke it.`";
	}

}

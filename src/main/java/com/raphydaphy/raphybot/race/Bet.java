package com.raphydaphy.raphybot.race;

import java.util.Random;

import sx.blah.discord.handle.obj.IUser;

public class Bet
{
	private final IUser player;
	private final int amount;
	private final String icon;

	private static final String[] icons = { ":cat:", ":hamster:", ":frog:", ":bear:", ":cow:", ":monkey:", ":camel:",
			":panda_face:", ":baby_chick:", ":chicken:", ":bug:", ":beetle:", ":tropical_fish:", ":whale2:", ":ram:",
			":tiger2:", ":goat:", ":pig2:", ":dragon_face:", ":dromedary_camel:", ":poodle:", ":squirrel:",
			":smiling_imp:", ":woman:", ":smiley_cat:", ":skull:", ":snowman:", ":dog:", ":rabbit:", ":tiger:", ":pig:",
			":boar:", ":horse:", ":sheep:", ":snake:", ":hatched_chick:", ":penguin:", ":bee:", ":snail:", ":fish:",
			":dolphin:", ":rat:", ":rabbit2:", ":rooster:", ":mouse2:", ":blowfish:", ":leopard:", ":mouse:", ":wolf:",
			":koala:", ":pig_nose:", ":monkey_face:", ":racehorse:", ":elephant:", ":bird:", ":hatching_chick:",
			":turtle:", ":ant:", ":octopus:", ":whale:", ":cow2:", ":water_buffalo:", ":dragon:", ":dog2:", ":ox:",
			":crocodile:", ":cat2:", ":octocat:" };

	public Bet(IUser player, int amount)
	{
		this.player = player;
		this.amount = amount;
		Random random = new Random();
		icon = icons[random.nextInt(icons.length)];
	}

	public IUser getPlayer()
	{
		return player;
	}

	public int getAmount()
	{
		return amount;
	}

	public String getIcon()
	{
		return icon;
	}
}

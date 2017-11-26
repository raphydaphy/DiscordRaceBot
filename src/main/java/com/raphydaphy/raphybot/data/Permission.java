package com.raphydaphy.raphybot.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Permission
{
	USER(0, "User"), HELPER(3, "Helper"), MODERATOR(5, "Moderator");
	
	public static List<Permission> REGISTRY;
	
	static
	{
		REGISTRY = new ArrayList<>();
		
		for (Permission permission : EnumSet.allOf(Permission.class))
		{
			REGISTRY.add(permission);
		}
	}
	
	private int level;
	private String name;
	
	Permission(int level, String name)
	{
		this.level = level;
		this.name = name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getName()
	{
		return name;
	}
}

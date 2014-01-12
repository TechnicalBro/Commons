package com.caved_in.commons;

import com.caved_in.commons.player.PlayerHandler;
import org.bukkit.entity.Player;

public class Messages {
	public static final String INVENTORY_CLEARED = "&aYour inventory has been cleared";
	public static final String PLAYER_OFFLINE = "&cThe requested player is offline";
	public static final String CHAT_SILENCED = "&7Chat is currently silenced, you are only able to chat if you have the required permissions";
	public static final String HELP_INCLUDE_PAGE_NUMBER = "&cPlease include a page number for the help menu";
	public static final String NO_PENDING_FRIENDS = "&eYou don't have any pending friend requests";
	public static final String PLAYER_HEALED = "&eYou've been healed!";

	public static String PLAYER_OFFLINE(String playerName) {
		return "&e" + playerName + " &cis offline";
	}

	public static String TELEPORTED_TO_PLAYER(String playerName) {
		return "&eYou were teleported to &a" + playerName;
	}

	public static String ITEM_DOESNT_EXIST(String itemName) {
		return "&cSorry, but &e" + itemName + "&c isn't a valid item";
	}

	public static String INVALID_ITEM_DATA(String input) {
		return "&cSorry; &e" + input + "&c isn't a valid data value";
	}

	public static String PROPER_USAGE(String usage) {
		return "&ePlease use &a" + usage;
	}

	public static String FRIEND_REQUEST_RECEIVED(String playerRequesting) {
		return String.format("&b%s&a has added you as a friend, do &e/friends accept %s &ato accept, or &e/friends deny %s&a to deny them",playerRequesting,playerRequesting,playerRequesting);
	}

	public static String FRIEND_REQUEST_SENT(String playerName) {
		return String.format("&aYour friend request to &e%s&a has been sent",playerName);
	}

	public static String FRIEND_DOESNT_EXIST(String playerName) {
		return String.format("&e%s&c isn't on your friends list",playerName);
	}

	public static String FRIEND_REQUEST_DENIED(String playerName) {
		return String.format("&aYou've denied the friend request from &e%s",playerName);
	}

	public static String FRIEND_DENIED_REQUEST(String playerName) {
		return String.format("&e%s&c has denied your friend request", playerName);
	}

	public static String FRIEND_ALREADY_EXISTS(String playerName) {
		return String.format("&cYou're already friends with &e%s", playerName);
	}

	public static String FRIEND_REQUEST_ALREADY_EXISTS(String playerName) {
		return String.format("&cYou've already sent &e%s&c a friend request", playerName);
	}

	public static String FRIEND_DELETED_FROM_FRIEND(String playerName) {
		return String.format("&b%s&e has removed you from their friends list", playerName);
	}

	public static String FRIEND_DELETED(String playerName) {
		return String.format("&aYou've removed &e%s &afrom your friends list",playerName);
	}

	public static String FRIEND_NO_REQUEST(String playerName) {
		return String.format("&cYou don't have a friend request from &e%s", playerName);
	}

	public static String FRIEND_ACCEPTED_REQUEST(String playerName) {
		return String.format("&aYou've accepted the friend request from &b%s", playerName);
	}

	public static String FRIEND_REQUEST_ACCEPTED(String playerName) {
		return String.format("&b%s&a has accepted your friend request!", playerName);
	}

	public static String TUNNELS_XP_BALANCE(Player player) {
		return String.format("&aYou have &e%s&a Tunnels XP", (int)PlayerHandler.getData(player).getCurrency());
	}

	public static String INVALID_COMMAND_USAGE(String... requiredArguments) {
		String[] requiredArgs = requiredArguments.clone();
		String returnString = "&cThis command requires the following arguments: ";
		if (requiredArgs.length > 0) {
			for (int I = 0; I < requiredArgs.length; I++) {
				returnString += "&e[" + requiredArgs[I] + "]&r" + (I < (requiredArgs.length - 1) ? ", " : "");
			}
			return returnString;
		} else {
			return "&cPlease validate the syntax of the command you performed";
		}
	}

	public static String TELEPORTED_TO(String description) {
		return String.format("&eYou've been teleported to &a%s", description);
	}

	public static String TELEPORTED_TO(String item, String target) {
		return String.format("&eYou've teleported &a%s&e to &a%s", item, target);
	}

	public static String SPEED_UPDATED(boolean isFlying, double speed) {
		return String.format("&aYou've set your &e%s&a speed to &e%s&a; to reset it to default use &e/speed", isFlying ? "fly" : "walk", speed);
	}

	public static String SPEED_RESET(boolean isFlying) {
		return String.format("&aYou've reset your &e%s&a speed to default", isFlying ? "fly" : "walk");
	}

	public static String WORLD_DOESNT_EXIST(String worldName) {
		return String.format("&cThe world &e%s&c doesn't exist, or isn't loaded", worldName);
	}

	public static String TIME_CHANGED(String worldName, String time) {
		return String.format("&aThe time for the world &7%s&a has been set to &e%s", worldName, time);
	}

	public static String ADDED_XP(String playerName, int amount) {
		return String.format("&aYou've added &e%s&a tunnels xp to &b%s",amount,playerName);
	}
}
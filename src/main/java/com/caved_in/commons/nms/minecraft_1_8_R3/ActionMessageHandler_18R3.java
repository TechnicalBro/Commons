package com.caved_in.commons.nms.minecraft_1_8_R3;

import com.caved_in.commons.nms.ActionMessageHandler;
import com.caved_in.commons.nms.NmsPlayers;
import com.caved_in.commons.reflection.ReflectionUtilities;
import com.caved_in.commons.utilities.StringUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class ActionMessageHandler_18R3 implements ActionMessageHandler {
	private static final String ACTION_JSON = "{\"text\": \"%s\"}";

	@Override
	public void actionMessage(Player player, String message) {
		try {
			Object packet;

			Class<?> packetPlayOutChatClass = ReflectionUtilities.getNMSClass("PacketPlayOutChat");

			Class<?> chatComponentTextClass = ReflectionUtilities.getNMSClass("ChatComponentText");
			Class<?> iChatBaseComponentClass = ReflectionUtilities.getNMSClass("IChatBaseComponent");

			Constructor chatComponentConstructor = ReflectionUtilities.getConstructor(chatComponentTextClass, String.class);

			Object chatComponent = chatComponentConstructor.newInstance(StringUtil.colorize(message));

			Constructor packetPlayOutChatConstructor = ReflectionUtilities.getConstructor(packetPlayOutChatClass, iChatBaseComponentClass, byte.class);

			packet = packetPlayOutChatConstructor.newInstance(chatComponent, (byte) 2);

			NmsPlayers.sendPacket(player, packet);


		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		IChatBaseComponent actionComponent = IChatBaseComponent.ChatSerializer.a(String.format(ACTION_JSON, StringUtil.colorize(message)));
//		PacketPlayOutChat actionChatPacket = new PacketPlayOutChat(actionComponent, (byte) 2);
//		NmsPlayers.sendPacket(player, actionChatPacket);
	}
}

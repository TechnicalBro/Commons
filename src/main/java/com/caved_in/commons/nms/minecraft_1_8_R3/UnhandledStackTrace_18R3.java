package com.caved_in.commons.nms.minecraft_1_8_R3;

import com.caved_in.commons.chat.Chat;
import com.caved_in.commons.event.StackTraceEvent;
import com.caved_in.commons.nms.UnhandledStackTrace;
import org.bukkit.Bukkit;
import org.joor.Reflect;

public class UnhandledStackTrace_18R3 implements UnhandledStackTrace, Thread.UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			StackTraceEvent.call(e);
		} catch (Throwable th) {
			e.printStackTrace();
		}
	}

	@Override
	public void register() {
		Thread.setDefaultUncaughtExceptionHandler(this);

		try {
			Object minecraftServer = Reflect.on(Bukkit.getServer()).call("getServer").get();

			Thread serverThread = Reflect.on(minecraftServer).get("serverThread");
			serverThread.setUncaughtExceptionHandler(this);

			Thread primaryThread = Reflect.on(minecraftServer).get("primaryThread");
			primaryThread.setUncaughtExceptionHandler(this);
			Chat.messageConsole("Registered uncaught exception handler for serverThread and PrimaryThread");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}

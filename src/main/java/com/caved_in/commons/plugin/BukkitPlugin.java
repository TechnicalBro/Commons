package com.caved_in.commons.plugin;

import com.caved_in.commons.chat.Chat;
import com.caved_in.commons.command.CommandHandler;
import com.caved_in.commons.debug.DebugAction;
import com.caved_in.commons.debug.Debugger;
import com.caved_in.commons.game.gadget.Gadget;
import com.caved_in.commons.game.gadget.Gadgets;
import com.caved_in.commons.item.ItemMessage;
import com.caved_in.commons.player.Players;
import com.caved_in.commons.scoreboard.BoardManager;
import com.caved_in.commons.scoreboard.ScoreboardManager;
import com.caved_in.commons.threading.RunnableManager;
import com.caved_in.commons.threading.executors.BukkitExecutors;
import com.caved_in.commons.threading.executors.BukkitScheduledExecutorService;
import com.caved_in.commons.utilities.ListUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class BukkitPlugin extends JavaPlugin implements CommonPlugin {

    private BukkitScheduledExecutorService syncExecuter;

    private BukkitScheduledExecutorService asyncExecuter;

    private BoardManager scoreboardManager;

    private RunnableManager threadManager;

    private ItemMessage itemMessage;

    private Logger logger = null;

    private CommandHandler commandHandler;

    public void onEnable() {
        initLogger();

        /*
		Create the command handler for annotation-based commands.
         */
        commandHandler = new CommandHandler(this);

		/*
		Create the thread manager, used to wrap tasks.
         */
        threadManager = new RunnableManager(this);

        /*
		Create the scoreboard manager, incase you wish to do
        fancy shmancy work with the scoreboard.
         */
        scoreboardManager = new ScoreboardManager(this, 15l);

        /*
		Create the syncronous promise listener
         */
        syncExecuter = BukkitExecutors.newSynchronous(this);

        /*
		Create the asyncronous promise listener
         */
        asyncExecuter = BukkitExecutors.newAsynchronous(this);

        if (Plugins.hasProtocolLib()) {
			/*
			If protocolLib is enabled then we also want to create the ItemMessage
            handler, where you use item meta packets to send actionbar-like messages
             */
            itemMessage = new ItemMessage(this);
        }

        //If the game doesn't have a data folder then make one
        if (!Plugins.hasDataFolder(this)) {
            Plugins.makeDataFolder(this);
        }

        //Assure we've got our configuration initialized, incase any startup options require it.
        initConfig();

        //Call the startup method, where the game performs its startup logic
        startup();
    }

    public void onDisable() {
        threadManager.cancelTasks();
        shutdown();
        Plugins.unregisterHooks(this);
    }


    public abstract void startup();

    public abstract void shutdown();

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    public String getAuthor() {
        return ListUtils.implode(",", getDescription().getAuthors());
    }

    public abstract void initConfig();

    public void registerCommands(Object... commands) {
        commandHandler.registerCommands(commands);
    }

    /**
     * Iterate through all the classes inside a package, and determine if it's a class that has
     * {@link com.caved_in.commons.command.Command} annotations available on any of its methods. If so, attempt to register them.
     * Note: Mirror method for {@link com.caved_in.commons.command.CommandHandler}.registerCommandsByPackage(String pkg)
     *
     * @param pkg Package to scan classes which contain {@link com.caved_in.commons.command.Command} annotations.
     */
    public void registerCommandsByPackage(String pkg) {
        commandHandler.registerCommandsByPackage(pkg);
    }

    public void registerListeners(Listener... listeners) {
        Plugins.registerListeners(this, listeners);
    }

    public void registerGadgets(Gadget... gadgets) {
        for (Gadget gadget : gadgets) {
            Gadgets.registerGadget(gadget);
        }
    }

    public void registerDebugActions(DebugAction... actions) {
        Debugger.addDebugAction(actions);
    }

    public void registerDebugActionsByPackage(String pkg) {
        Debugger.addDebugActionsByPackage(pkg);
    }

    public BukkitScheduledExecutorService getSyncExecuter() {
        return syncExecuter;
    }

    public BukkitScheduledExecutorService getAsyncExecuter() {
        return asyncExecuter;
    }

    public RunnableManager getThreadManager() {
        return threadManager;
    }

    public ItemMessage getItemMessage() {
        return itemMessage;
    }

    public void debug(String... message) {
        Chat.messageAll(Players.getAllDebugging(), message);
        for (String m : message) {
            logger.log(Level.INFO, m);
        }
    }

    public BoardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Logger getPluginLogger() {
        return logger;
    }

    protected void initLogger() {
        if (logger != null) {
            return;
        }
        logger = new PluginLogger(this);
        logger.setUseParentHandlers(true);
    }
}

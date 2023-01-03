package io.github.evercraftmc.rp_utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.evercraftmc.rp_utils.commands.Command;
import io.github.evercraftmc.rp_utils.commands.morph.MorphCommand;
import io.github.evercraftmc.rp_utils.commands.sit.LayCommand;
import io.github.evercraftmc.rp_utils.commands.sit.SitCommand;
import io.github.evercraftmc.rp_utils.config.FileConfig;
import io.github.evercraftmc.rp_utils.listeners.Listener;
import io.github.evercraftmc.rp_utils.listeners.MorphListener;
import io.github.evercraftmc.rp_utils.listeners.PlayerMoveListener;
import io.github.evercraftmc.rp_utils.listeners.SitListener;

public class Main extends JavaPlugin {
    private static Main Instance;

    private FileConfig<Data> data;

    private List<Command> commands;
    private List<Listener> listeners;

    @Override
    public void onLoad() {
        Main.Instance = this;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.getLogger().info("Loading data..");

        this.data = new FileConfig<Data>(Data.class, this.getDataFolder().getAbsolutePath() + File.separator + "data.json");
        this.data.reload();

        if (this.data.getParsed() != null) {
            this.data.save();
        }

        this.getLogger().info("Finished loading data");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<Command>();

        this.commands.add(new MorphCommand("morph", "Morph into another entity", Arrays.asList(), "rp_utils.commands.morph").register());

        this.commands.add(new SitCommand("sit", "Sit on the floor", Arrays.asList(), "rp_utils.commands.sit").register());
        this.commands.add(new LayCommand("lay", "Lay on the floor", Arrays.asList("sleep"), "rp_utils.commands.lay").register());

        this.getLogger().info("Finished loading commands");

        this.getLogger().info("Loading listeners..");

        this.listeners = new ArrayList<Listener>();

        this.listeners.add(new MorphListener().register());
        this.listeners.add(new SitListener().register());
        this.listeners.add(new PlayerMoveListener().register());

        this.getLogger().info("Finished loading listeners");

        this.getLogger().info("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling plugin..");

        this.getLogger().info("Closing data..");

        this.data.close();

        this.getLogger().info("Finished closing data..");

        this.getLogger().info("Unregistering commands..");

        for (Command command : this.commands) {
            command.unregister();
        }

        this.getLogger().info("Finished unregistering commands..");

        this.getLogger().info("Unregistering listeners..");

        for (Listener listener : this.listeners) {
            listener.unregister();
        }

        this.getLogger().info("Finished unregistering listeners..");

        this.getLogger().info("Finished disabling plugin");
    }

    public static Main getInstance() {
        return Main.Instance;
    }

    public FileConfig<Data> getPluginData() {
        return this.data;
    }
}
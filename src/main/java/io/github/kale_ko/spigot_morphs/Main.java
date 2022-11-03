package io.github.kale_ko.spigot_morphs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.kale_ko.spigot_morphs.commands.Command;
import io.github.kale_ko.spigot_morphs.commands.morph.MorphCommand;
import io.github.kale_ko.spigot_morphs.commands.staff.ReloadCommand;
import io.github.kale_ko.spigot_morphs.config.FileConfig;
import io.github.kale_ko.spigot_morphs.listeners.Listener;
import io.github.kale_ko.spigot_morphs.listeners.MorphListener;
import io.github.kale_ko.spigot_morphs.listeners.PlayerMoveListener;

public class Main extends JavaPlugin {
    private static Main Instance;

    private FileConfig<Config> config;
    private FileConfig<Messages> messages;
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

        this.getLogger().info("Loading config..");

        this.config = new FileConfig<Config>(Config.class, this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
        this.config.reload();

        if (this.config.getParsed() != null) {
            this.config.save();
        }

        this.getLogger().info("Finished loading config");

        this.getLogger().info("Loading messages..");

        this.messages = new FileConfig<Messages>(Messages.class, this.getDataFolder().getAbsolutePath() + File.separator + "messages.json");
        this.messages.reload();

        if (this.messages.getParsed() != null) {
            this.messages.save();
        }

        this.getLogger().info("Finished loading messages");

        this.getLogger().info("Loading player data..");

        this.data = new FileConfig<Data>(Data.class, this.getDataFolder().getAbsolutePath() + File.separator + "data.json");
        this.data.reload();

        if (this.data.getParsed() != null) {
            this.data.save();
        }

        this.getLogger().info("Finished loading player data");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<Command>();

        this.commands.add(new MorphCommand("morph", "Reload the plugin", Arrays.asList(), "spigot_morphs.commands.morph").register());
        this.commands.add(new ReloadCommand("spigotmorphsreload", "Reload the plugin", Arrays.asList("smreload"), "spigot_morphs.commands.reload").register());

        this.getLogger().info("Finished loading commands");

        this.getLogger().info("Loading listeners..");

        this.listeners = new ArrayList<Listener>();

        this.listeners.add(new MorphListener().register());
        this.listeners.add(new PlayerMoveListener().register());

        this.getLogger().info("Finished loading listeners");

        this.getLogger().info("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling plugin..");

        this.getLogger().info("Closing config..");

        this.config.close();

        this.getLogger().info("Finished closing config..");

        this.getLogger().info("Closing messages..");

        this.messages.close();

        this.getLogger().info("Finished closing messages..");

        this.getLogger().info("Closing player data..");

        this.data.close();

        this.getLogger().info("Finished closing player data..");

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

    public void reload() {
        this.getLogger().info("Reloading plugin..");

        this.onDisable();

        this.onEnable();

        this.getLogger().info("Finished reloading plugin");
    }

    public static Main getInstance() {
        return Main.Instance;
    }

    public FileConfig<Config> getPluginConfig() {
        return this.config;
    }

    public FileConfig<Messages> getPluginMessages() {
        return this.messages;
    }

    public FileConfig<Data> getPluginData() {
        return this.data;
    }
}
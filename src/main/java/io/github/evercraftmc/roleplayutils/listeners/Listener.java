package io.github.evercraftmc.roleplayutils.listeners;

import org.bukkit.event.HandlerList;
import io.github.evercraftmc.roleplayutils.Main;

public abstract class Listener implements org.bukkit.event.Listener {
    public Listener register() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        return this;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
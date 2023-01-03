package io.github.evercraftmc.rp_utils.listeners;

import org.bukkit.event.HandlerList;
import io.github.evercraftmc.rp_utils.Main;

public abstract class Listener implements org.bukkit.event.Listener {
    public Listener register() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        return this;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
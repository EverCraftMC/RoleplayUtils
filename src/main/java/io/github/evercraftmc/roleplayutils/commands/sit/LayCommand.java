package io.github.evercraftmc.roleplayutils.commands.sit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.evercraftmc.roleplayutils.Data;
import io.github.evercraftmc.roleplayutils.Main;
import io.github.evercraftmc.roleplayutils.commands.Command;
import io.github.evercraftmc.roleplayutils.listeners.SitListener;
import io.github.evercraftmc.roleplayutils.util.StringUtils;
import io.github.evercraftmc.roleplayutils.util.formatting.ComponentFormatter;
import io.github.evercraftmc.roleplayutils.util.formatting.TextFormatter;
import io.github.evercraftmc.roleplayutils.util.types.SerializableLocation;

public class LayCommand extends Command {
    public LayCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (!Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting) {
                    if (args[0].equalsIgnoreCase("precise")) {
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting = true;
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.LAYING;
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(player.getLocation().getX() % 1, 0.5, player.getLocation().getZ() % 1));
                        try {
                            Main.getInstance().getPluginData().save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SitListener.onSitStand(player);
                    } else {
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting = true;
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.LAYING;
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5));
                        try {
                            Main.getInstance().getPluginData().save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SitListener.onSitStand(player);
                    }
                } else {
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingType = null;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    try {
                        Main.getInstance().getPluginData().save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SitListener.onSitStand(player);
                }
            } else {
                if (!Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting) {
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting = true;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.LAYING;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5));
                    try {
                        Main.getInstance().getPluginData().save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SitListener.onSitStand(player);
                } else {
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingType = null;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    try {
                        Main.getInstance().getPluginData().save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SitListener.onSitStand(player);
                }
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cYou can't do that from the console")));
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<String>();

        if (args.length == 1) {
            list.add("precise");
        } else {
            return Arrays.asList();
        }

        if (args.length > 0) {
            return StringUtils.matchPartial(args[args.length - 1], list);
        } else {
            return list;
        }
    }
}
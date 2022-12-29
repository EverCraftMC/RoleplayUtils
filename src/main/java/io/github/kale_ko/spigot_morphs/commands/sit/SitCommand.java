package io.github.kale_ko.spigot_morphs.commands.sit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.commands.Command;
import io.github.kale_ko.spigot_morphs.listeners.SitListener;
import io.github.kale_ko.spigot_morphs.util.StringUtils;
import io.github.kale_ko.spigot_morphs.util.formatting.TextFormatter;
import io.github.kale_ko.spigot_morphs.util.types.SerializableLocation;

public class SitCommand extends Command {
    public SitCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (!Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
                    if (args[0].equalsIgnoreCase("precise")) {
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = true;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation());
                        Main.getInstance().getPluginData().save();

                        SitListener.onSitStand(player);
                    } else {
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = true;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation());
                        Main.getInstance().getPluginData().save();

                        SitListener.onSitStand(player);
                    }
                } else {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

                    SitListener.onSitStand(player);
                }
            } else {
                if (!Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = true;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation());
                    Main.getInstance().getPluginData().save();

                    SitListener.onSitStand(player);
                } else {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

                    SitListener.onSitStand(player);
                }
            }
        } else {
            sender.sendMessage(TextFormatter.translateColors("&cYou can't do that from the console"));
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
package io.github.evercraftmc.rp_utils.commands.sit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.evercraftmc.rp_utils.Data;
import io.github.evercraftmc.rp_utils.Main;
import io.github.evercraftmc.rp_utils.commands.Command;
import io.github.evercraftmc.rp_utils.listeners.SitListener;
import io.github.evercraftmc.rp_utils.util.StringUtils;
import io.github.evercraftmc.rp_utils.util.formatting.ComponentFormatter;
import io.github.evercraftmc.rp_utils.util.formatting.TextFormatter;
import io.github.evercraftmc.rp_utils.util.types.SerializableLocation;

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
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.SITTING;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(player.getLocation().getX() % 1, 0.5, player.getLocation().getZ() % 1));
                        Main.getInstance().getPluginData().save();

                        SitListener.onSitStand(player);
                    } else {
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = true;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.SITTING;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5));
                        Main.getInstance().getPluginData().save();

                        SitListener.onSitStand(player);
                    }
                } else {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = null;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

                    SitListener.onSitStand(player);
                }
            } else {
                if (!Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = true;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = Data.SittingType.SITTING;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = SerializableLocation.fromBukkitLocation(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5));
                    Main.getInstance().getPluginData().save();

                    SitListener.onSitStand(player);
                } else {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = null;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

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
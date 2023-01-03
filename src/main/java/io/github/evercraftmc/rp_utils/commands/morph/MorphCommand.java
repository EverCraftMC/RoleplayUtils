package io.github.evercraftmc.rp_utils.commands.morph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import io.github.evercraftmc.rp_utils.Main;
import io.github.evercraftmc.rp_utils.commands.Command;
import io.github.evercraftmc.rp_utils.listeners.MorphListener;
import io.github.evercraftmc.rp_utils.util.StringUtils;
import io.github.evercraftmc.rp_utils.util.formatting.ComponentFormatter;
import io.github.evercraftmc.rp_utils.util.formatting.TextFormatter;

public class MorphCommand extends Command {
    public MorphCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("clear")) {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isMorphed = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).currentMorph = null;
                    Main.getInstance().getPluginData().save();

                    MorphListener.onMorphChange(player);

                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&aSuccessfully removed your morph")));
                } else {
                    try {
                        EntityType entityType = EntityType.valueOf(args[0].replace("minecraft:", "").replace("-", "_").toUpperCase());

                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isMorphed = true;
                        Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).currentMorph = entityType;
                        Main.getInstance().getPluginData().save();

                        MorphListener.onMorphChange(player);

                        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&aSuccessfully morphed into a" + ((entityType.toString().charAt(0) == 'A' || entityType.toString().charAt(0) == 'E' || entityType.toString().charAt(0) == 'I' || entityType.toString().charAt(0) == 'O' || entityType.toString().charAt(0) == 'U') ? "n" : "") + " " + entityType.toString().toLowerCase().replace("_", " "))));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cInvalid arguments")));
                    }
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cInvalid arguments")));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cYou can't do that from the console")));
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<String>();

        if (args.length == 1) {
            if (sender instanceof Player player) {
                if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isMorphed) {
                    list.add("clear");
                }
            } else {
                list.add("clear");
            }

            for (EntityType entityType : EntityType.values()) {
                list.add("minecraft:" + entityType.toString().toLowerCase());
            }
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
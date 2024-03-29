package io.github.evercraftmc.roleplayutils.commands.morph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import io.github.evercraftmc.roleplayutils.Main;
import io.github.evercraftmc.roleplayutils.commands.Command;
import io.github.evercraftmc.roleplayutils.listeners.MorphListener;
import io.github.evercraftmc.roleplayutils.util.StringUtils;
import io.github.evercraftmc.roleplayutils.util.formatting.ComponentFormatter;
import io.github.evercraftmc.roleplayutils.util.formatting.TextFormatter;
import io.github.kale_ko.bjsl.BJSL;
import io.github.kale_ko.bjsl.elements.ParsedObject;

@SuppressWarnings("deprecation")
public class MorphCommand extends Command {
    public MorphCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("clear")) {
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isMorphed = false;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorph = null;
                    Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorphNbt = null;
                    try {
                        Main.getInstance().getPluginData().save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MorphListener.onMorphChange(player);

                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&aSuccessfully removed your morph")));
                } else {
                    EntityType entityType = EntityType.fromName(args[0].toLowerCase().replace("minecraft:", "").replace("-", "_"));

                    if (entityType != null) {
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isMorphed = true;
                        Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorph = entityType;

                        if (args.length > 1) {
                            StringBuilder nbtBuilder = new StringBuilder();
                            for (Integer i = 1; i < args.length; i++) {
                                nbtBuilder.append(args[i] + " ");
                            }

                            ParsedObject nbtObject = null;
                            try {
                                nbtObject = BJSL.parseJson(nbtBuilder.toString().trim()).asObject();
                            } catch (Exception e) {
                                nbtObject = null;
                            }

                            if (nbtObject != null) {
                                Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorphNbt = BJSL.stringifyJson(nbtObject);
                            } else {
                                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cInvalid ntb")));
                            }
                        } else {
                            Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorphNbt = null;
                        }

                        try {
                            Main.getInstance().getPluginData().save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MorphListener.onMorphChange(player);

                        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&aSuccessfully morphed into a" + ((entityType.toString().charAt(0) == 'A' || entityType.toString().charAt(0) == 'E' || entityType.toString().charAt(0) == 'I' || entityType.toString().charAt(0) == 'O' || entityType.toString().charAt(0) == 'U') ? "n" : "") + " " + entityType.toString().toLowerCase().replace("_", " "))));
                    } else {
                        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors("&cInvalid entity id")));
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
                if (Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isMorphed) {
                    list.add("clear");
                }
            } else {
                list.add("clear");
            }

            for (EntityType entityType : EntityType.values()) {
                list.add("minecraft:" + entityType.getName().toLowerCase());
            }

            list.remove("minecraft:player");
            list.remove("minecraft:ender_dragon"); // FIXME Doesn't work because the dragon is multi-parted
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
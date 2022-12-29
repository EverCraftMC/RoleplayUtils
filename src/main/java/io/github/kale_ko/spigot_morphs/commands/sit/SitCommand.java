package io.github.kale_ko.spigot_morphs.commands.sit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import io.github.kale_ko.spigot_morphs.commands.Command;
import io.github.kale_ko.spigot_morphs.util.StringUtils;
import io.github.kale_ko.spigot_morphs.util.formatting.TextFormatter;

public class SitCommand extends Command {
    public SitCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            Pig pig;
            if (args.length > 0 && args[0].equalsIgnoreCase("precise")) {
                pig = (Pig) player.getWorld().spawnEntity(player.getLocation().add(0, -1, 0), EntityType.PIG);
            } else {
                pig = (Pig) player.getWorld().spawnEntity(player.getLocation().getBlock().getLocation().add(0.5, player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation().add(0, -1, 0)).getType().getKey().getKey().endsWith("_stairs") || player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation().add(0, -1, 0)).getType().getKey().getKey().endsWith("_slab") || player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation().add(0, -1, 0)).getType().getKey().getKey().endsWith("_bed") ? -1.5 : (player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation()).getType().getKey().getKey().endsWith("_stairs") || player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation()).getType().getKey().getKey().endsWith("_slab") || player.getWorld().getBlockAt(player.getLocation().getBlock().getLocation()).getType().getKey().getKey().endsWith("_bed") ? -0.5 : -1), 0.5), EntityType.PIG);
            }

            pig.setInvisible(true);
            pig.setInvulnerable(true);
            pig.setAI(false);
            pig.setGravity(false);
            pig.setSilent(true);
            pig.addScoreboardTag("playerSeat:" + player.getUniqueId().toString() + ":" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ());
            pig.addPassenger(player);
        } else {
            sender.sendMessage(TextFormatter.translateColors("&cYou can't do that from the console"));
        }
    }

    @Override
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
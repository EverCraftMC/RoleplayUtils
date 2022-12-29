package io.github.kale_ko.spigot_morphs.listeners;

import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import io.github.kale_ko.spigot_morphs.Data;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.util.bukkit.MetadataUtil;

public class SitListener extends Listener {
    public static void onSitStand(Player player) {
        if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
            if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())).remove();
                }

                MetadataUtil.removeMetadata(player, "seat");
                MetadataUtil.removeMetadata(player, "sitting");
            }

            Pig entity = (Pig) player.getWorld().spawnEntity(Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation.toBukkitLocation(), EntityType.PIG);

            entity.setPersistent(true);

            MetadataUtil.setMetadata(entity, "isSeat", true);
            MetadataUtil.setMetadata(entity, "rider", player.getUniqueId().toString());

            entity.setAI(false);
            entity.setGravity(false);
            entity.setCollidable(false);
            entity.setSilent(true);
            entity.setRemoveWhenFarAway(false);

            MetadataUtil.setMetadata(player, "sitting", true);
            MetadataUtil.setMetadata(player, "seat", entity.getUniqueId().toString());

            entity.setInvisible(true);
            entity.setSaddle(true);

            entity.addPassenger(player);
        } else {
            if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())).remove();
                }

                MetadataUtil.removeMetadata(player, "seat");
                MetadataUtil.removeMetadata(player, "sitting");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Main.getInstance().getPluginData().getParsed().players.containsKey(event.getPlayer().getUniqueId().toString())) {
            Main.getInstance().getPluginData().getParsed().players.put(event.getPlayer().getUniqueId().toString(), new Data.Player());
            Main.getInstance().getPluginData().save();
        }

        onSitStand(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
        Main.getInstance().getPluginData().save();

        onSitStand(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player && event.getDismounted() instanceof Pig) {
            if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
                if (event.getDismounted().getUniqueId().equals(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString()))) {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

                    onSitStand(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
        Main.getInstance().getPluginData().save();

        onSitStand(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
        Main.getInstance().getPluginData().save();

        onSitStand(event.getPlayer());
    }
}
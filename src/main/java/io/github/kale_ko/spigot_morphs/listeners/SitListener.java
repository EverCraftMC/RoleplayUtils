package io.github.kale_ko.spigot_morphs.listeners;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.spigotmc.event.entity.EntityDismountEvent;
import io.github.kale_ko.spigot_morphs.Data;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.util.bukkit.MetadataUtil;
import io.github.kale_ko.spigot_morphs.util.types.SerializableLocation;

public class SitListener extends Listener {
    public static void onSitStand(Player player) {
        if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
            if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())).remove();
                }

                MetadataUtil.removeMetadata(player, "seat");
                MetadataUtil.removeMetadata(player, "sitting");
            } else {
                Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingFromLocation = SerializableLocation.fromBukkitLocation(player.getLocation());
            }

            Location location = Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation.toBukkitLocation();
            double offset = -1;

            if ((location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM) || (location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Stairs stairs && stairs.getHalf() == Half.BOTTOM) || (location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Bed)) {
                offset = -1.5;
            }

            if ((location.clone().getBlock().getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM) || (location.clone().getBlock().getBlockData() instanceof Stairs stairs && stairs.getHalf() == Half.BOTTOM) || (location.clone().getBlock().getBlockData() instanceof Bed)) {
                offset = -0.5;
            }

            Pig entity = (Pig) player.getWorld().spawnEntity(location.clone().add(0, offset - 0.5, 0), EntityType.PIG);

            entity.setAI(false);
            entity.setGravity(false);
            entity.setCollidable(false);
            entity.setInvulnerable(true);
            entity.setSilent(true);
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);

            MetadataUtil.setMetadata(entity, "isSeat", true);
            MetadataUtil.setMetadata(entity, "rider", player.getUniqueId().toString());

            MetadataUtil.setMetadata(player, "sitting", true);
            MetadataUtil.setMetadata(player, "seat", entity.getUniqueId().toString());

            entity.setInvisible(true);

            entity.addPassenger(player);
        } else {
            if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())).remove();
                }

                Location returnLocation = Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingFromLocation.toBukkitLocation();
                returnLocation.setPitch(player.getLocation().getPitch());
                returnLocation.setYaw(player.getLocation().getYaw());
                player.teleport(returnLocation, TeleportCause.PLUGIN);

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
        if (MetadataUtil.hasMetadata(event.getPlayer(), "sitting") && MetadataUtil.getMetadata(event.getPlayer(), "sitting").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "seat").asString())) != null) {
                Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "seat").asString())).remove();
            }

            MetadataUtil.removeMetadata(event.getPlayer(), "seat");
            MetadataUtil.removeMetadata(event.getPlayer(), "sitting");
        }
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
        if (MetadataUtil.hasMetadata(event.getPlayer(), "sitting") && MetadataUtil.getMetadata(event.getPlayer(), "sitting").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "seat").asString())) != null) {
                Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "seat").asString())).remove();
            }

            MetadataUtil.removeMetadata(event.getPlayer(), "seat");
            MetadataUtil.removeMetadata(event.getPlayer(), "sitting");
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
        Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
        Main.getInstance().getPluginData().save();

        onSitStand(event.getPlayer());
    }
}
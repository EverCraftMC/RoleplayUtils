package io.github.kale_ko.spigot_morphs.listeners;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.util.bukkit.MetadataUtil;

public class PlayerMoveListener extends Listener {
    public PlayerMoveListener() {
        super();

        Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> {
            for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                onPlayerMove(player);
            }
        }, 2, 2);
    }

    public void onPlayerMove(Player player) {
        if (MetadataUtil.hasMetadata(player, "morphed") && MetadataUtil.getMetadata(player, "morphed").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())) != null) {
                Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())).teleport(player.getLocation(), TeleportCause.PLUGIN);
            }
        }

        if (MetadataUtil.hasMetadata(player, "sitting") && MetadataUtil.getMetadata(player, "sitting").asBoolean()) {
            if (player.getWorld().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())) != null) {
                player.getWorld().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "seat").asString())).setRotation(player.getLocation().getYaw(), 0);
            }
        }

        if (MetadataUtil.hasMetadata(player, "laying") && MetadataUtil.getMetadata(player, "laying").asBoolean()) {
            if (player.getWorld().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "lay").asString())) != null) {
                ((CraftWorld) player.getWorld()).getHandle().getEntity(MetadataUtil.getMetadata(player, "lay").asInt()).setYHeadRot(player.getLocation().getYaw());
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        onPlayerMove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerTeleportEvent event) {
        onPlayerMove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerChangedWorldEvent event) {
        onPlayerMove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            onPlayerMove(player);
        }
    }

    @EventHandler
    public void onPlayerMove(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            onPlayerMove(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerToggleFlightEvent event) {
        if (MetadataUtil.hasMetadata(event.getPlayer(), "morphed") && MetadataUtil.getMetadata(event.getPlayer(), "morphed").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())) != null) {
                Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())).setGravity(!event.getPlayer().isFlying());
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerGameModeChangeEvent event) {
        if (MetadataUtil.hasMetadata(event.getPlayer(), "morphed") && MetadataUtil.getMetadata(event.getPlayer(), "morphed").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())) != null) {
                if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())).setGravity(true);
                }
            }
        }
    }
}
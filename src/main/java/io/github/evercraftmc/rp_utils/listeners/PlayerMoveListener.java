package io.github.evercraftmc.rp_utils.listeners;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
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
import io.github.evercraftmc.rp_utils.Main;
import io.github.evercraftmc.rp_utils.util.bukkit.MetadataUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.BedBlock;

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

        if (SitListener.seatEntities.containsKey(player.getUniqueId().toString())) {
            SitListener.seatEntities.get(player.getUniqueId().toString()).setRotation(player.getLocation().getYaw(), 0);
        }

        if (SitListener.layEntities.containsKey(player.getUniqueId().toString())) {
            ServerPlayer entityPlayer = SitListener.layEntities.get(player.getUniqueId().toString());

            float yaw = player.getLocation().getYaw();

            BlockFace bedDir = BlockFace.WEST;
            if (entityPlayer.getLevel().getChunk(entityPlayer.getSleepingPos().get()).getBlockState(entityPlayer.getSleepingPos().get()).getBlock() instanceof BedBlock bed) {
                Direction direction = entityPlayer.getLevel().getChunk(entityPlayer.getSleepingPos().get()).getBlockState(entityPlayer.getSleepingPos().get()).getValue(BedBlock.FACING);

                if (direction == Direction.NORTH) {
                    bedDir = BlockFace.NORTH;
                } else if (direction == Direction.SOUTH) {
                    bedDir = BlockFace.SOUTH;
                } else if (direction == Direction.EAST) {
                    bedDir = BlockFace.EAST;
                } else if (direction == Direction.WEST) {
                    bedDir = BlockFace.WEST;
                }
            }

            if (bedDir == BlockFace.NORTH) {
                yaw += 180;
            } else if (bedDir == BlockFace.EAST) {
                yaw += 90;
            } else if (bedDir == BlockFace.WEST) {
                yaw -= 90;
            }

            if (yaw < -180) {
                yaw += 360;
            }

            if (yaw > 180) {
                yaw -= 360;
            }

            yaw = Math.max(yaw, -70);
            yaw = Math.min(yaw, 70);

            entityPlayer.setYHeadRot(yaw);

            for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                connection.send(new ClientboundRotateHeadPacket(entityPlayer, (byte) ((int) (entityPlayer.getYHeadRot() * 256.0F / 360.0F))));
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
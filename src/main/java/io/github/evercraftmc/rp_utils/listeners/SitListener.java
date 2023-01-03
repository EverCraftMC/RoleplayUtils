package io.github.evercraftmc.rp_utils.listeners;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.evercraftmc.rp_utils.Data;
import io.github.evercraftmc.rp_utils.Main;
import io.github.evercraftmc.rp_utils.util.types.SerializableLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Pose;

public class SitListener extends Listener {
    protected static class SkinCacheObject {
        public String texture;
        public String signature;

        protected SkinCacheObject(String texture, String signature) {
            this.texture = texture;
            this.signature = signature;
        }
    }

    public static final Map<String, Pig> seatEntities = new HashMap<String, Pig>();
    public static final Map<String, ServerPlayer> layEntities = new HashMap<String, ServerPlayer>();

    protected static final Map<String, SkinCacheObject> skinsCache = new HashMap<String, SkinCacheObject>();

    public static void onSitStand(Player player) {
        if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
            if (seatEntities.containsKey(player.getUniqueId().toString())) {
                if (seatEntities.get(player.getUniqueId().toString()).isValid()) {
                    seatEntities.get(player.getUniqueId().toString()).remove();
                }

                seatEntities.remove(player.getUniqueId().toString());
            } else {
                Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingFromLocation = SerializableLocation.fromBukkitLocation(player.getLocation());
            }

            Location location = Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation.toBukkitLocation();
            double offset = -1;
            double layOffset = 0;

            if ((location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM) || (location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Stairs stairs && stairs.getHalf() == Half.BOTTOM) || (location.clone().add(0, -1, 0).getBlock().getBlockData() instanceof Bed)) {
                offset = -1.5;
            }

            if ((location.clone().getBlock().getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM) || (location.clone().getBlock().getBlockData() instanceof Stairs stairs && stairs.getHalf() == Half.BOTTOM) || (location.clone().getBlock().getBlockData() instanceof Bed)) {
                offset = -0.5;
            }

            Location layLocation = location.clone();

            if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType == Data.SittingType.LAYING) {
                if (location.clone().getBlock().getBlockData() instanceof Bed bed) {
                    offset = -0.6875;
                    layOffset = 0.1875;

                    if (bed.getPart() == Bed.Part.FOOT) {
                        location.add(bed.getFacing().getDirection());
                        layLocation = location.clone();
                    }
                } else {
                    offset = -1.375;
                    layOffset = -0.375;

                    location.add(new Vector(-0.75, 0, 0));
                    layLocation.subtract(new Vector(-1, 0, 0));
                }
            }

            Pig entity = (Pig) player.getWorld().spawnEntity(location.clone().add(0, offset - 0.5, 0), EntityType.PIG);

            entity.setAI(false);
            entity.setGravity(false);
            entity.setCollidable(false);
            entity.setInvulnerable(true);
            entity.setInvisible(true);
            entity.setSilent(true);
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);

            entity.addPassenger(player);

            seatEntities.put(player.getUniqueId().toString(), entity);

            if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType == Data.SittingType.LAYING) {
                MinecraftServer server = ((CraftServer) Main.getInstance().getServer()).getServer();
                ServerLevel world = ((CraftWorld) Main.getInstance().getServer().getWorlds().get(0)).getHandle();

                String skinTexture = "ewogICJ0aW1lc3RhbXAiIDogMTY3MjM2MjE3MTEzOSwKICAicHJvZmlsZUlkIiA6ICJjMDZmODkwNjRjOGE0OTExOWMyOWVhMWRiZDFhYWI4MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3RldmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0YWY3MTg0NTVkNGFhYjUyOGU3YTYxZjg2ZmEyNWU2YTM2OWQxNzY4ZGNiMTNmN2RmMzE5YTcxM2ViODEwYiIKICAgIH0KICB9Cn0=";
                String skinSignature = "Jkbn8FXYnur3Ov11rXDTlInSwyOJ9/tuJ/ASQANLQTFWYvjVOkJzBF+2A5hmYi3TJNoFY1J89BiTmZDpwf3QkdppXfdPnFcRVy2alaE81bC/0SzjFOpxzwnVmVisfnShcmZtiLPXjldb3ZyPNx6DMATBRP0UhqKJw1ibuIajlgyMXN1BT1Y9avv1emQtx6fa9Ubnkhx2Cq27lZCOFXjJwrQqFWmYsXO2hEIAQFbZc9KFdkcnHiTyZWliSIy2iMFI6yV5Hdoq3fa+vnUfY5qd42TGPu2ocIBrZqDqY+QfIrjp6hJgteAeUr9brJ5yqIfmsAg9itru8C/1XZ3PIVqO8dVGYbLfP1LJ0W0/mOQct6p0ypA92JG8hzgnHBVNaHkteG9NQ7hlluKadDs6MCtk6hjMY//3oHs4TGBgCEQWBksotA8x8JT3w3itog4kNy24KY7wRKt5fNUqJuJykGWh205FAwv1mpv17HjO5PF8awbvUUmv7n1tSnYQLuC7yAxzfa4Xakwkc53OE5eqeaJH/Pmdm6aDOjjmU04P6poXlKDxXdK/Z/rZS2eCQYNvjZ2HnQY/auTCj1oYKjbT/au6qmQaK8vlwjzunFpfP3yBT/4hR6uVCuaNGFRmAApqIYjotEGwcny/kF/gnwP+VGSIToHCXOOh4ej1pA/4+JnyPGw=";

                if (!skinsCache.containsKey(player.getUniqueId().toString())) {
                    try {
                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-", "") + "?unsigned=false")).GET().build(), BodyHandlers.ofString());
                        JsonElement body = JsonParser.parseString(response.body());
                        JsonObject skin = body.getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                        skinTexture = skin.get("value").getAsString();
                        skinSignature = skin.get("signature").getAsString();

                        skinsCache.put(player.getUniqueId().toString(), new SkinCacheObject(skinTexture, skinSignature));
                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    skinTexture = skinsCache.get(player.getUniqueId().toString()).texture;
                    skinSignature = skinsCache.get(player.getUniqueId().toString()).signature;
                }

                ServerPlayer entityPlayer = new ServerPlayer(server, world, new GameProfile(UUID.randomUUID(), player.getName()));

                entityPlayer.getGameProfile().getProperties().put("textures", new Property("textures", skinTexture, skinSignature));
                entityPlayer.getEntityData().set(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION, ((CraftPlayer) player).getHandle().getEntityData().get(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION));

                entityPlayer.setPos(layLocation.getX(), layLocation.getY() + layOffset, layLocation.getZ());
                entityPlayer.setRot(0, 0);

                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entityPlayer));
                    connection.send(new ClientboundAddPlayerPacket(entityPlayer));
                    connection.send(new ClientboundSetEntityDataPacket(entityPlayer.getId(), entityPlayer.getEntityData().getNonDefaultValues()));
                }

                player.setInvisible(true);

                entityPlayer.setPose(Pose.SLEEPING);
                entityPlayer.setSleepingPos(new BlockPos(layLocation.getX(), layLocation.getY() + layOffset, layLocation.getZ()));
                entityPlayer.setPos(layLocation.getX(), layLocation.getY() + layOffset, layLocation.getZ());

                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundSetEntityDataPacket(entityPlayer.getId(), entityPlayer.getEntityData().getNonDefaultValues()));
                    connection.send(new ClientboundTeleportEntityPacket(entityPlayer));
                    connection.send(new ClientboundRotateHeadPacket(entityPlayer, (byte) ((int) (entityPlayer.getYHeadRot() * 256.0F / 360.0F))));
                }

                layEntities.put(player.getUniqueId().toString(), entityPlayer);
            }
        } else {
            if (seatEntities.containsKey(player.getUniqueId().toString())) {
                if (seatEntities.get(player.getUniqueId().toString()).isValid()) {
                    seatEntities.get(player.getUniqueId().toString()).remove();
                }

                seatEntities.remove(player.getUniqueId().toString());

                Location returnLocation = Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingFromLocation.toBukkitLocation();
                returnLocation.setPitch(player.getLocation().getPitch());
                returnLocation.setYaw(player.getLocation().getYaw());
                player.teleport(returnLocation, TeleportCause.PLUGIN);
            }

            if (layEntities.containsKey(player.getUniqueId().toString())) {
                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundRemoveEntitiesPacket(layEntities.get(player.getUniqueId().toString()).getId()));
                }

                layEntities.remove(player.getUniqueId().toString());

                player.setInvisible(false);
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
        if (seatEntities.containsKey(event.getPlayer().getUniqueId().toString())) {
            if (seatEntities.get(event.getPlayer().getUniqueId().toString()).isValid()) {
                seatEntities.get(event.getPlayer().getUniqueId().toString()).remove();
            }

            seatEntities.remove(event.getPlayer().getUniqueId().toString());
        }

        if (layEntities.containsKey(event.getPlayer().getUniqueId().toString())) {
            for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                connection.send(new ClientboundRemoveEntitiesPacket(layEntities.get(event.getPlayer().getUniqueId().toString()).getId()));
            }

            layEntities.remove(event.getPlayer().getUniqueId().toString());

            event.getPlayer().setInvisible(false);
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player && event.getDismounted() instanceof Pig) {
            if (seatEntities.containsKey(player.getUniqueId().toString()) && event.getDismounted().getUniqueId().equals(seatEntities.get(player.getUniqueId().toString()).getUniqueId())) {
                if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting) {
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isSitting = false;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingType = null;
                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingLocation = null;
                    Main.getInstance().getPluginData().save();

                    onSitStand(player);

                    Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).sittingFromLocation = null;
                    Main.getInstance().getPluginData().save();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != TeleportCause.PLUGIN && event.getCause() != TeleportCause.DISMOUNT) {
            if (Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting) {
                Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
                Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingType = null;
                Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
                Main.getInstance().getPluginData().save();

                onSitStand(event.getPlayer());

                Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingFromLocation = null;
                Main.getInstance().getPluginData().save();
            }
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        if (Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting) {
            Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).isSitting = false;
            Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingType = null;
            Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingLocation = null;
            Main.getInstance().getPluginData().save();

            onSitStand(event.getPlayer());

            Main.getInstance().getPluginData().getParsed().players.get(event.getPlayer().getUniqueId().toString()).sittingFromLocation = null;
            Main.getInstance().getPluginData().save();
        }
    }
}
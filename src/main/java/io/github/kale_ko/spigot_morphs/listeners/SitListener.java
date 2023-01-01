package io.github.kale_ko.spigot_morphs.listeners;

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
import io.github.kale_ko.spigot_morphs.Data;
import io.github.kale_ko.spigot_morphs.Main;
import io.github.kale_ko.spigot_morphs.util.bukkit.MetadataUtil;
import io.github.kale_ko.spigot_morphs.util.types.SerializableLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
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

    protected static Map<String, SkinCacheObject> skinsCache = new HashMap<String, SkinCacheObject>();

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
            entity.setSilent(true);
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);

            MetadataUtil.setMetadata(entity, "isSeat", true);
            MetadataUtil.setMetadata(entity, "rider", player.getUniqueId().toString());

            MetadataUtil.setMetadata(player, "sitting", true);
            MetadataUtil.setMetadata(player, "seat", entity.getUniqueId().toString());

            entity.setInvisible(true);

            entity.addPassenger(player);

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
                entityPlayer.setRot(layLocation.getYaw(), layLocation.getPitch());
                entityPlayer.setYHeadRot(layLocation.getYaw());

                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entityPlayer));
                    connection.send(new ClientboundAddPlayerPacket(entityPlayer));
                    connection.send(new ClientboundSetEntityDataPacket(entityPlayer.getId(), entityPlayer.getEntityData().getNonDefaultValues()));
                }

                player.setInvisible(true);

                MetadataUtil.setMetadata(player, "laying", true);
                MetadataUtil.setMetadata(player, "lay", entityPlayer.getId());

                entityPlayer.setPose(Pose.SLEEPING);
                entityPlayer.setSleepingPos(new BlockPos(layLocation.getX(), layLocation.getY() + layOffset, layLocation.getZ()));
                entityPlayer.setPos(layLocation.getX(), layLocation.getY() + layOffset, layLocation.getZ());

                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundSetEntityDataPacket(entityPlayer.getId(), entityPlayer.getEntityData().getNonDefaultValues()));
                    connection.send(new ClientboundTeleportEntityPacket(entityPlayer));
                }
            }
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

            if (MetadataUtil.hasMetadata(player, "laying") && MetadataUtil.getMetadata(player, "laying").asBoolean()) {
                for (Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {
                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player2).getHandle().connection;
                    connection.send(new ClientboundRemoveEntitiesPacket(MetadataUtil.getMetadata(player, "lay").asInt()));
                }

                player.setInvisible(false);

                MetadataUtil.removeMetadata(player, "lay");
                MetadataUtil.removeMetadata(player, "laying");
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
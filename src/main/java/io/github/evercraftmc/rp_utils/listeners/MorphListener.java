package io.github.evercraftmc.rp_utils.listeners;

import java.util.UUID;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import io.github.evercraftmc.rp_utils.Data;
import io.github.evercraftmc.rp_utils.Main;
import io.github.evercraftmc.rp_utils.util.bukkit.MetadataUtil;
import io.github.evercraftmc.rp_utils.util.formatting.ComponentFormatter;

public class MorphListener extends Listener {
    public static void onMorphChange(Player player) {
        if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isMorphed) {
            if (MetadataUtil.hasMetadata(player, "morphed") && MetadataUtil.getMetadata(player, "morphed").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())).remove();
                }

                MetadataUtil.removeMetadata(player, "morph");
                MetadataUtil.removeMetadata(player, "morphed");
            }

            Entity entity = player.getWorld().spawnEntity(player.getLocation(), Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).currentMorph);

            entity.customName(ComponentFormatter.stringToComponent(player.getName()));
            entity.setCustomNameVisible(true);
            entity.setPersistent(true);

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setAI(false);
                livingEntity.setCollidable(false);
                livingEntity.setRemoveWhenFarAway(false);

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                player.setHealth(livingEntity.getHealth());
                player.setAbsorptionAmount(livingEntity.getAbsorptionAmount());
            }

            MetadataUtil.setMetadata(entity, "morphed", true);
            MetadataUtil.setMetadata(entity, "morph", player.getUniqueId().toString());

            player.setInvisible(true);
            player.setCollidable(false);

            MetadataUtil.setMetadata(player, "morphed", true);
            MetadataUtil.setMetadata(player, "morph", entity.getUniqueId().toString());
        } else {
            if (MetadataUtil.hasMetadata(player, "morphed") && MetadataUtil.getMetadata(player, "morphed").asBoolean()) {
                if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())) != null) {
                    Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(player, "morph").asString())).remove();
                }

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

                player.setInvisible(false);
                player.setCollidable(true);

                MetadataUtil.removeMetadata(player, "morph");
                MetadataUtil.removeMetadata(player, "morphed");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Main.getInstance().getPluginData().getParsed().players.containsKey(event.getPlayer().getUniqueId().toString())) {
            Main.getInstance().getPluginData().getParsed().players.put(event.getPlayer().getUniqueId().toString(), new Data.Player());
            Main.getInstance().getPluginData().save();
        }

        onMorphChange(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (MetadataUtil.hasMetadata(event.getPlayer(), "morphed") && MetadataUtil.getMetadata(event.getPlayer(), "morphed").asBoolean()) {
            if (Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())) != null) {
                Main.getInstance().getServer().getEntity(UUID.fromString(MetadataUtil.getMetadata(event.getPlayer(), "morph").asString())).remove();
            }

            MetadataUtil.removeMetadata(event.getPlayer(), "morph");
            MetadataUtil.removeMetadata(event.getPlayer(), "morphed");
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        onMorphChange(event.getPlayer());
    }
}
package io.github.evercraftmc.rp_utils.listeners;

import java.util.HashMap;
import java.util.Map;
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
import io.github.evercraftmc.rp_utils.util.formatting.ComponentFormatter;

public class MorphListener extends Listener {
    public static final Map<String, Entity> morphEntities = new HashMap<String, Entity>();

    public static void onMorphChange(Player player) {
        if (Main.getInstance().getPluginData().getParsed().players.get(player.getUniqueId().toString()).isMorphed) {
            if (morphEntities.containsKey(player.getUniqueId().toString())) {
                morphEntities.get(player.getUniqueId().toString()).remove();
                morphEntities.remove(player.getUniqueId().toString());
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

            player.setInvisible(true);
            player.setCollidable(false);

            morphEntities.put(player.getUniqueId().toString(), entity);
        } else {
            if (morphEntities.containsKey(player.getUniqueId().toString())) {
                morphEntities.get(player.getUniqueId().toString()).remove();
                morphEntities.remove(player.getUniqueId().toString());

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

                player.setInvisible(false);
                player.setCollidable(true);
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
        if (morphEntities.containsKey(event.getPlayer().getUniqueId().toString())) {
            morphEntities.get(event.getPlayer().getUniqueId().toString()).remove();
            morphEntities.remove(event.getPlayer().getUniqueId().toString());
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        onMorphChange(event.getPlayer());
    }
}
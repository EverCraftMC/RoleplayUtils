package io.github.evercraftmc.roleplayutils.listeners;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.evercraftmc.roleplayutils.Data;
import io.github.evercraftmc.roleplayutils.Main;
import io.github.evercraftmc.roleplayutils.util.formatting.ComponentFormatter;
import net.minecraft.nbt.TagParser;

public class MorphListener extends Listener {
    public static final Map<String, Entity> morphEntities = new HashMap<String, Entity>();

    public static void onMorphChange(Player player) {
        if (Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).isMorphed) {
            if (morphEntities.containsKey(player.getUniqueId().toString())) {
                Entity morphEntity = morphEntities.remove(player.getUniqueId().toString());
                if (morphEntity.isValid()) {
                    morphEntity.remove();
                }
            }

            Entity entity = player.getWorld().spawnEntity(player.getLocation(), Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorph);

            entity.customName(ComponentFormatter.stringToComponent(player.getName()));
            entity.setCustomNameVisible(true);

            if (Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorphNbt != null) {
                try {
                    ((CraftEntity) entity).getHandle().load(TagParser.parseTag(Main.getInstance().getPluginData().get().players.get(player.getUniqueId().toString()).currentMorphNbt));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }

            entity.setPersistent(true);

            entity.setInvulnerable(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);
            entity.setGravity(!player.isFlying());

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setAI(false);
                livingEntity.setCollidable(false);
                livingEntity.setRemoveWhenFarAway(false);

                if (player.getGameMode() == GameMode.SPECTATOR) {
                    livingEntity.setInvisible(true);
                    livingEntity.setSilent(true);
                }

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                player.setHealth(livingEntity.getHealth());
                player.setAbsorptionAmount(livingEntity.getAbsorptionAmount());
            }

            player.setInvisible(true);
            player.setCollidable(false);

            morphEntities.put(player.getUniqueId().toString(), entity);
        } else {
            if (morphEntities.containsKey(player.getUniqueId().toString())) {
                Entity morphEntity = morphEntities.remove(player.getUniqueId().toString());
                if (morphEntity.isValid()) {
                    morphEntity.remove();
                }

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());

                player.setInvisible(false);
                player.setCollidable(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Main.getInstance().getPluginData().get().players.containsKey(event.getPlayer().getUniqueId().toString())) {
            Main.getInstance().getPluginData().get().players.put(event.getPlayer().getUniqueId().toString(), new Data.Player());
            try {
                Main.getInstance().getPluginData().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        onMorphChange(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (morphEntities.containsKey(event.getPlayer().getUniqueId().toString())) {
            Entity morphEntity = morphEntities.remove(event.getPlayer().getUniqueId().toString());
            if (morphEntity.isValid()) {
                morphEntity.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerRespawnEvent event) {
        onMorphChange(event.getPlayer());
    }
}
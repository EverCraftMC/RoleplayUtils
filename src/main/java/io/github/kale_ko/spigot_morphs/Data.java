package io.github.kale_ko.spigot_morphs;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;

public class Data {
    public static class Player {
        public Boolean isMorphed = false;
        public EntityType currentMorph = null;
    }

    public Map<String, Player> players = new HashMap<String, Player>();
}
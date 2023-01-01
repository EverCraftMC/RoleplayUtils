package io.github.kale_ko.spigot_morphs;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;
import io.github.kale_ko.spigot_morphs.util.types.SerializableLocation;

public class Data {
    public enum SittingType {
        SITTING, LAYING
    }

    public static class Player {
        public Boolean isMorphed = false;
        public EntityType currentMorph = null;

        public Boolean isSitting = false;
        public SittingType sittingType = null;
        public SerializableLocation sittingLocation = null;
        public SerializableLocation sittingFromLocation = null;
    }

    public Map<String, Player> players = new HashMap<String, Player>();
}
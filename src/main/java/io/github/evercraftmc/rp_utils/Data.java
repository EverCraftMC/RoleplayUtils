package io.github.evercraftmc.rp_utils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;
import io.github.evercraftmc.rp_utils.util.types.SerializableLocation;

public class Data {
    public enum SittingType {
        SITTING, LAYING
    }

    public static class Player {
        public Boolean isMorphed = false;
        public EntityType currentMorph = null;
        public String currentMorphNbt = null;

        public Boolean isSitting = false;
        public SittingType sittingType = null;
        public SerializableLocation sittingLocation = null;
        public SerializableLocation sittingFromLocation = null;
    }

    public Map<String, Player> players = new HashMap<String, Player>();
}
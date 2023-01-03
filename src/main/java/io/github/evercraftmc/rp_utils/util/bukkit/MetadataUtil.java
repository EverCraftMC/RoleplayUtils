package io.github.evercraftmc.rp_utils.util.bukkit;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import io.github.evercraftmc.rp_utils.Main;

public class MetadataUtil {
    public static Boolean hasMetadata(Metadatable object, String key) {
        return getMetadata(object, key) != null;
    }

    public static MetadataValue getMetadata(Metadatable object, String key) {
        for (MetadataValue value : object.getMetadata(key)) {
            if (value.getOwningPlugin() == Main.getInstance()) {
                return value;
            }
        }

        return null;
    }

    public static void setMetadata(Metadatable object, String key, Object value) {
        if (hasMetadata(object, key)) {
            removeMetadata(object, key);
        }

        object.setMetadata(key, new FixedMetadataValue(Main.getInstance(), value));
    }

    public static void removeMetadata(Metadatable object, String key) {
        if (hasMetadata(object, key)) {
            object.removeMetadata(key, Main.getInstance());
        }
    }
}
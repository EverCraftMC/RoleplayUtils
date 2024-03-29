package io.github.evercraftmc.roleplayutils.util.types;

import org.bukkit.Location;
import io.github.evercraftmc.roleplayutils.Main;

public class SerializableLocation {
    private String world;

    private Double x;
    private Double y;
    private Double z;

    private Float yaw;
    private Float pitch;

    public SerializableLocation() {
        this(null, 0.0, 0.0, 0.0, 0.0f, 0.0f);
    }

    public SerializableLocation(String world, Double x, Double y, Double z, Float yaw, Float pitch) {
        this.world = world;

        this.x = x;
        this.y = y;
        this.z = z;

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorld() {
        return this.world;
    }

    public Double getX() {
        return this.x;
    }

    public Double getY() {
        return this.y;
    }

    public Double getZ() {
        return this.z;
    }

    public Float getYaw() {
        return this.yaw;
    }

    public Float getPitch() {
        return this.pitch;
    }

    public Location toBukkitLocation() {
        return new Location(Main.getInstance().getServer().getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    public static SerializableLocation fromBukkitLocation(Location location) {
        return new SerializableLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
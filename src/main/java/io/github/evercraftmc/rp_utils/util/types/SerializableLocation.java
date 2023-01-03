package io.github.evercraftmc.rp_utils.util.types;

import org.bukkit.Location;
import io.github.evercraftmc.rp_utils.Main;

public class SerializableLocation {
    private String world = "world";

    private Double x = 0.0;
    private Double y = 0.0;
    private Double z = 0.0;

    private Float yaw = 0f;
    private Float pitch = 0f;

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

    @Override
    public String toString() {
        return this.getWorld() + "," + this.getX() + "," + this.getY() + "," + this.getZ() + "," + this.getYaw() + "," + this.getPitch();
    }

    public static SerializableLocation fromString(String string) {
        String[] split = string.split(",");

        return new SerializableLocation(split[0], Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public Location toBukkitLocation() {
        return new Location(Main.getInstance().getServer().getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    public static SerializableLocation fromBukkitLocation(Location location) {
        return new SerializableLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
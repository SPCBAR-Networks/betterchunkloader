package ca.shadownode.betterchunkloader.sponge.data;

import ca.shadownode.betterchunkloader.sponge.BetterChunkLoader;
import java.sql.Timestamp;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.flowpowered.math.vector.Vector3i;
import java.time.LocalDateTime;
import java.util.Optional;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.NONE)
public final class ChunkLoader {

    private UUID uuid;
    private UUID world;
    private UUID owner;

    private Location<World> location;
    private Vector3i chunk;
    private Integer radius;

    private Timestamp creation;
    private Boolean isAlwaysOn;

    public ChunkLoader(UUID uuid, UUID world, UUID owner, Location<World> location, Vector3i chunk, Integer radius, Timestamp creation, Boolean isAlwaysOn) {
        this.uuid = uuid;
        this.world = world;
        this.owner = owner;
        this.location = location;
        this.chunk = chunk;
        this.radius = radius;
        this.creation = creation;
        this.isAlwaysOn = isAlwaysOn;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @XmlAttribute(name = "uuid")
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getWorld() {
        return world;
    }

    @XmlAttribute(name = "world")
    public void setWorld(UUID world) {
        this.world = world;
    }

    public UUID getOwner() {
        return owner;
    }

    @XmlAttribute(name = "owner")
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location<World> getLocation() {
        return location;
    }

    @XmlAttribute(name = "location")
    public void setLocation(Location<World> location) {
        this.location = location;
    }

    public Vector3i getChunk() {
        return chunk;
    }

    public void setChunk(Vector3i chunk) {
        this.chunk = chunk;
    }

    public Integer getRadius() {
        return radius;
    }

    @XmlAttribute(name = "r")
    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Timestamp getCreation() {
        return creation;
    }

    @XmlAttribute(name = "creation")
    public void setCreation(Timestamp creation) {
        this.creation = creation;
    }

    public Boolean isAlwaysOn() {
        return isAlwaysOn;
    }

    @XmlAttribute(name = "alwaysOn")
    public void setAlwaysOn(Boolean isAlwaysOn) {
        this.isAlwaysOn = isAlwaysOn;
    }

    public Boolean isExpired() {
        Optional<PlayerData> playerData = BetterChunkLoader.getInstance().getDataStore().getOrCreatePlayerData(getOwner());
        if (playerData.isPresent()) {
            LocalDateTime limit = playerData.get().getLastOnline().toLocalDateTime().plusDays(3);
            LocalDateTime current = LocalDateTime.now();
            return current.isAfter(limit);
        }
        return true;
    }

    public Boolean isLoadable() {
        return blockCheck() && !isExpired();
    }

    public Integer getChunks() {
        return Double.valueOf(Math.pow((2 * radius) + 1, 2)).intValue();
    }

    public Boolean canEdit(Player player) {
        if (player.getUniqueId() == getOwner()) {
            return true;
        } else {
            return player.hasPermission("betterchunkloader.chunkloader") || player.hasPermission("betterchunkloader.chunkloader.edit");
        }
    }

    public Boolean canCreate(Player player) {
        return player.hasPermission("betterchunkloader.chunkloader") || player.hasPermission("betterchunkloader.chunkloader.create") || player.hasPermission("betterchunkloader.chunkloader.create." + (isAlwaysOn() ? "offline" : "online"));
    }

    public Boolean blockCheck() {
        if (this.location.getBlock() == null) {
            return false;
        }
        if (isAlwaysOn) {
            return this.location.getBlock().getType().equals(BlockTypes.DIAMOND_BLOCK);
        } else {
            return this.location.getBlock().getType().equals(BlockTypes.IRON_BLOCK);
        }
    }

    @Override
    public String toString() {
        return this.world + ":" + this.location.getX() + "," + this.location.getZ() + (this.isAlwaysOn ? "y" : "n") + " - " + this.getChunks() + " - " + this.location.toString();
    }

    public Boolean contains(Vector3i vector) {
        return location.getX() - radius <= vector.getX() && vector.getX() <= location.getX() + radius && location.getZ() - radius <= vector.getZ() && vector.getZ() <= location.getZ() + radius;
    }

    public Boolean contains(int chunkX, int chunkZ) {
        return location.getX() - radius <= chunkX && chunkX <= location.getX() + radius && location.getZ() - radius <= chunkZ && chunkZ <= location.getZ() + radius;
    }

}

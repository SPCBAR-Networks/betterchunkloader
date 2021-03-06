package ca.shadownode.betterchunkloader.sponge.data;

import ca.shadownode.betterchunkloader.sponge.BetterChunkLoader;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PlayerData {

    private String name;
    private UUID uuid;
    private Long lastOnline;
    private Integer onlineChunksAmount;
    private Integer alwaysOnChunksAmount;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        this.lastOnline = System.currentTimeMillis();
        this.onlineChunksAmount = BetterChunkLoader.getInstance().getConfig().getCore().chunkLoader.online.defaultOnline;
        this.alwaysOnChunksAmount = BetterChunkLoader.getInstance().getConfig().getCore().chunkLoader.alwaysOn.defaultAlwaysOn;
    }

    public PlayerData(String name, UUID uuid, Long lastOnline, Integer onlineChunksAmount, Integer alwaysOnChunksAmount) {
        this.name = name;
        this.uuid = uuid;
        this.lastOnline = lastOnline;
        this.onlineChunksAmount = onlineChunksAmount;
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
    }
    
    public String getName() {
        return name;
    }
    
    @XmlAttribute(name = "username")
    public void setName(String username) {
        this.name = username;
    }

    public UUID getUnqiueId() {
        return uuid;
    }

    @XmlAttribute(name = "uuid")
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }
    
    public Long getLastOnline() {
        return lastOnline;
    }
    
    @XmlAttribute(name = "lastOnline")
    public void setLastOnline(Long lastOnline) {
        this.lastOnline = lastOnline;
    }
    
    public int getOnlineChunksAmount() {
        return onlineChunksAmount;
    }

    @XmlAttribute(name = "onlineAmount")
    public void setOnlineChunksAmount(Integer onlineChunksAmount) {
        this.onlineChunksAmount = onlineChunksAmount;
    }
    
    @XmlAttribute(name = "onlineAmount")
    public void addOnlineChunksAmount(Integer onlineChunksAmount) {
        this.onlineChunksAmount = this.onlineChunksAmount + onlineChunksAmount;
    }

    public int getAlwaysOnChunksAmount() {
        return alwaysOnChunksAmount;
    }

    @XmlAttribute(name = "alwaysOnAmount")
    public void setAlwaysOnChunksAmount(Integer alwaysOnChunksAmount) {
        this.alwaysOnChunksAmount = alwaysOnChunksAmount;
    }
    
    @XmlAttribute(name = "alwaysOnAmount")
    public void addAlwaysOnChunksAmount(Integer alwaysOnChunksAmount) {
        this.alwaysOnChunksAmount = this.alwaysOnChunksAmount + alwaysOnChunksAmount;
    }
}

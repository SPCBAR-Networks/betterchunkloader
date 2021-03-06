package ca.shadownode.betterchunkloader.sponge.events;

import ca.shadownode.betterchunkloader.sponge.BetterChunkLoader;
import ca.shadownode.betterchunkloader.sponge.data.ChunkLoader;
import ca.shadownode.betterchunkloader.sponge.data.PlayerData;
import ca.shadownode.betterchunkloader.sponge.utils.Utilities;
import java.util.HashMap;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;

public class WorldListener {

    private final BetterChunkLoader plugin;

    public WorldListener(BetterChunkLoader plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        plugin.getDataStore().getChunkLoaders(event.getTargetWorld()).stream().filter((cl) -> (cl.isLoadable())).forEachOrdered((cl) -> {
            plugin.getChunkManager().loadChunkLoader(cl);
        });
    }

    @Listener
    public void onWorldUnLoad(UnloadWorldEvent event) {
        plugin.getDataStore().getChunkLoaders(event.getTargetWorld()).stream().filter((cl) -> (cl.isLoadable())).forEachOrdered((cl) -> {
            plugin.getChunkManager().unloadChunkLoader(cl);
        });
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        BlockSnapshot block = event.getTransactions().get(0).getOriginal();

        if (block == null || !ChunkLoader.ONLINE_TYPE.isPresent() || !ChunkLoader.ALWAYSON_TYPE.isPresent()) {
            return;
        }

        if (!block.getState().getType().equals(ChunkLoader.ONLINE_TYPE.get()) && !block.getState().getType().equals(ChunkLoader.ALWAYSON_TYPE.get())) {
            return;
        }

        Optional<ChunkLoader> chunkLoader = plugin.getDataStore().getChunkLoaderAt(block.getLocation().get());
        if (chunkLoader.isPresent()) {

            Player player = event.getCause().last(Player.class).get();
            Optional<PlayerData> playerData = plugin.getDataStore().getPlayerData(chunkLoader.get().getOwner());
            if (playerData.isPresent()) {

                HashMap<String, String> args = new HashMap<>();
                args.put("player", player.getName());
                args.put("playerUUID", player.getUniqueId().toString());
                args.put("ownerName", playerData.get().getName());
                args.put("owner", playerData.get().getUnqiueId().toString());
                args.put("type", chunkLoader.get().isAlwaysOn() ? "Always On" : "Online Only");
                args.put("location", Utilities.getReadableLocation(chunkLoader.get().getWorld(), chunkLoader.get().getLocation()));
                args.put("chunks", String.valueOf(chunkLoader.get().getChunks()));

                if (plugin.getDataStore().removeChunkLoader(chunkLoader.get())) {
                    plugin.getChunkManager().unloadChunkLoader(chunkLoader.get());
                    if (chunkLoader.get().isAlwaysOn()) {
                        playerData.get().addAlwaysOnChunksAmount(chunkLoader.get().getChunks());
                    } else {
                        playerData.get().addOnlineChunksAmount(chunkLoader.get().getChunks());
                    }
                    plugin.getDataStore().updatePlayerData(playerData.get());
                    player.sendMessage(Utilities.parseMessage(plugin.getConfig().getMessages().prefix + plugin.getConfig().getMessages().chunkLoader.removeSuccess, args));

                    Optional<Player> owner = Sponge.getServer().getPlayer(chunkLoader.get().getOwner());
                    if (owner.isPresent() && player != owner.get()) {
                        player.sendMessage(Utilities.parseMessage(plugin.getConfig().getMessages().prefix + plugin.getConfig().getMessages().chunkLoader.ownerNotify, args));
                    }
                     plugin.getLogger().info(player.getName() + " broke " + owner.get().getName() + "'s chunk loader at " + Utilities.getReadableLocation(chunkLoader.get().getWorld(), chunkLoader.get().getLocation()));

                }else{
                    player.sendMessage(Utilities.parseMessage(plugin.getConfig().getMessages().prefix + plugin.getConfig().getMessages().chunkLoader.removeFailure, args));
                }
            }
        }
    }

}

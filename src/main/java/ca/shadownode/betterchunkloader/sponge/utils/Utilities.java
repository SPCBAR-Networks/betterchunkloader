package ca.shadownode.betterchunkloader.sponge.utils;

import com.flowpowered.math.vector.Vector3i;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.api.Sponge;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

public class Utilities {

    public static Text parseMessage(String message) {
        return parseMessage(message, new HashMap<>());
    }

    public static Text parseMessage(String message, HashMap<String, String> args) {
        for (Map.Entry<String, String> arg : args.entrySet()) {
            message = message.replace("{" + arg.getKey() + "}", arg.getValue());
        }
        Text textMessage = TextSerializers.FORMATTING_CODE.deserialize(message);
        List<String> urls = extractUrls(message);

        if (!urls.isEmpty()) {
            for (String url : urls) {
                String msgBefore = StringUtils.substringBefore(message, url);
                String msgAfter = StringUtils.substringAfter(message, url);
                if (msgBefore == null) {
                    msgBefore = "";
                } else if (msgAfter == null) {
                    msgAfter = "";
                }
                try {
                    textMessage = Text.of(
                            TextSerializers.FORMATTING_CODE.deserialize(msgBefore),
                            TextActions.openUrl(new URL(url)),
                            Text.of(TextColors.GREEN, url),
                            TextSerializers.FORMATTING_CODE.deserialize(msgAfter));
                } catch (MalformedURLException e) {
                    return Text.of(message);
                }
            }
        }
        return textMessage;
    }

    public static List<Text> parseMessageList(List<String> messages) {
        return parseMessageList(messages, new HashMap<>());
    }

    public static List<Text> parseMessageList(List<String> messages, HashMap<String, String> args) {
        List<Text> texts = new ArrayList<>();
        messages.forEach(s -> texts.add(parseMessage(s, args)));
        return texts;
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher;
        try {
            urlMatcher = pattern.matcher(text);
            while (urlMatcher.find()) {
                containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
            }
            return containedUrls;
        } catch (Exception ignored) {
            return containedUrls;
        }
    }

    public static String getReadableLocation(UUID worldUUID, Vector3i location) {
        StringBuilder builder = new StringBuilder();
        Optional<World> world = Sponge.getServer().getWorld(worldUUID);
        if(world.isPresent()) {
            builder.append(world.get().getName());
        }else{
            builder.append("UNKNOWN");
        }
        builder.append(" X: ").append(location.getX());
        builder.append(" Y: ").append(location.getY());
        builder.append(" Z: ").append(location.getZ());
        return builder.toString();
    }

    public static long getPlayerDataLastModified(UUID playerUUID) {
        String name = Sponge.getServer().getDefaultWorldName();
        Path path = Sponge.getGame().getGameDirectory().resolve(name);
        File playerData = new File(path.toString(), "playerdata" + File.separator + playerUUID.toString() + ".dat");
        if (playerData.exists()) {
            return playerData.lastModified();
        }
        return 0;
    }

    public static Optional<String> getPlayerName(UUID playerUUID) {
        GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();
        Optional<GameProfile> profile = profileManager.getCache().getOrLookupById(playerUUID);
        if (profile.isPresent()) {
            return profile.get().getName();
        }
        return Optional.empty();
    }

    public static Optional<UUID> getPlayerUUID(String playerName) {
        GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();
        Optional<GameProfile> profile = profileManager.getCache().getOrLookupByName(playerName);
        if (profile.isPresent()) {
            return Optional.ofNullable(profile.get().getUniqueId());
        }
        return Optional.empty();
    }

    // Test Code to check which chunks are in use.
    /*
     * for(Chunk c :chunks) {
     * 
     * world.getLocation(c.getBlockMax()).setBlockType(BlockTypes.BEDROCK,
     * Cause.source(Sponge.getPluginManager().fromInstance(this).get()).
     * build()); } log.info(chunks.size()+"");
     */
}

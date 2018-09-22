package org.minigame.data;

import com.google.common.reflect.TypeToken;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningGame;
import org.minigame.running.midjoinable.MidJoinableGame;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.UUID;

public class MinigameKeys {

    private MinigameKeys(){}

    public static final int CURRENT_CONTEXT_VERSION = 1;

    public static final Key<MapValue<UUID, MidJoinableGame<? extends MinigameMap>>> MINIGAME_INVITES;

    static {
        MINIGAME_INVITES = Key.builder()
                .type(new TypeToken<MapValue<UUID, MidJoinableGame<? extends MinigameMap>>>(){})
                .id(buildID("invites"))
                .name("Invites")
                .query(DataQuery.of(".", "invites"))
                .build();
    }

    @Deprecated
    public static void init(){}

    private static String buildID(String id){
        return MinigamePlugin.PLUGIN_ID + ":" + id;
    }
}

package org.minigame.running;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.spongepowered.api.entity.EntitySnapshot;

import java.util.Map;
import java.util.UUID;

public interface RunningGameBuilder<T extends RunningLiveGame<? extends PlayableMap>> {

    public T createGame(ReadyToPlayMap map, MapGamemode<? extends GamemodeType> type, Map<UUID, EntitySnapshot> snapshots);
}

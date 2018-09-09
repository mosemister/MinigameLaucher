package org.minigame.running.midjoinable;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.minigame.utils.snapshot.EquipableEntitySnapshot;
import org.minigame.utils.snapshot.PlayerSnapshot;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public interface MidJoinableGame<M extends MinigameMap> extends RunningGame<M> {

    public default void register(Player entity){
        register(entity.getUniqueId(), PlayerSnapshot.of(entity).get());
    }

    public default void register(UUID uuid, EntitySnapshot snapshot){
        getSnapshots().put(uuid, snapshot);
    }


}

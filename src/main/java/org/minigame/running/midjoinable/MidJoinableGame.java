package org.minigame.running.midjoinable;

import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.running.RunningGame;
import org.minigame.running.RunningLiveGame;
import org.minigame.utils.snapshot.PlayerSnapshot;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Iterator;
import java.util.UUID;

public interface MidJoinableGame<M extends MinigameMap> extends RunningGame<M> {

    public default void register(Player entity){
        register(entity.getUniqueId(), PlayerSnapshot.of(entity).get());
    }

    public default void register(UUID uuid, EntitySnapshot snapshot){
        getSnapshots().put(uuid, snapshot);
    }

    public interface LiveMidJoinableGame<M extends PlayableMap> extends MidJoinableGame<M>, RunningLiveGame<M> {

        public default boolean spawn(Player player){
            Iterator<MinigameProp> userSpawn = this.getMapGamemode().getProps(PropType.USER_SPAWN).iterator();
            if(userSpawn.hasNext()){
                SpawnProp spawn = (SpawnProp) userSpawn.next();
                Location<World> loc = getMap().getLocPos1().copy().add(spawn.getPosition());
                player.setLocation(loc);
                return true;
            }
            return false;
        }

    }

}

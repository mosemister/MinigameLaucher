package org.minigame.map.gamemode;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.MinigameRequirement;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.requirement.spawn.SpawnRequirement;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.running.lobby.RunningLobby;
import org.minigame.utils.UniquieId;

import java.util.Set;
import java.util.stream.Collectors;

public interface MapGamemode <G extends GamemodeType> extends UniquieId {

    Set<MinigameProp> getProps();
    UnplayableMap getMap();
    G getGamemode();
    boolean isSuitable(RunningLobby lobby);

    default <T extends MinigameProp> Set<T> getProps(MinigameRequirement<T> requirement){
        return (Set<T>)getProps().stream().filter(t -> requirement.getPropClass().isInstance(t)).filter(t -> requirement.isProp((T)t)).collect(Collectors.toSet());
    }

    default <T extends MinigameProp> Set<T> getProps(PropType<T> type){
        return (Set<T>) getProps().stream().filter(p -> type.getPropClass().isInstance(p)).collect(Collectors.toSet());
    }

    default boolean meetsRequirements(){
        return getGamemode().getRequirements().stream().allMatch(r -> meetsRequirement(r));
    }

    default <T extends MinigameProp> boolean meetsRequirement(MinigameRequirement<T> requirement){
        Set<MinigameProp> set = getProps().stream()
                .filter(p -> requirement.getPropClass().isInstance(p))
                .filter(p -> requirement.isProp((T)p))
                .collect(Collectors.toSet());
        if(requirement instanceof SpawnRequirement){
            SpawnRequirement spawnR = (SpawnRequirement)requirement;
            set = set.stream().filter(r -> spawnR.getSpawnType().equals(((SpawnProp)r).getEntityType())).collect(Collectors.toSet());
        }
        if(requirement.getAmountRequired() < set.size()){
            return false;
        }
        return true;
    }

    default String getIdName(){
        return getMap().getName().toPlain() + "-" + getGamemode().getIdName();
    }
}

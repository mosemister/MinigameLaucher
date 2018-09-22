package org.minigame.gamemode;

import com.flowpowered.math.vector.Vector3d;
import org.minigame.exception.GamemodeDoesNotSupportSpawnProp;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.MinigameRequirement;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.utils.UniquieId;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface GamemodeType extends UniquieId {

    Set<MinigameRequirement<? extends MinigameProp>> getRequirements();

    <S extends SpawnProp> S createSpawn(Vector3d pos, Vector3d rotation, PropType<S> class1) throws GamemodeDoesNotSupportSpawnProp;

    default Optional<MapGamemode> getMapDetails(UnplayableMap map){
        return getSupportedMapDetails().stream().filter(mgm -> mgm.getMap().equals(map)).findFirst();
    }

    default Set<MapGamemode> getSupportedMapDetails(){
        return MinigamePlugin.getUniquieSet(MapGamemode.class).stream().filter(mgm -> {
            GamemodeType type = mgm.getGamemode();
            if (type.equals(this)) {
                return true;
            }
            return false;
        }).collect(Collectors.toSet());
    }

}

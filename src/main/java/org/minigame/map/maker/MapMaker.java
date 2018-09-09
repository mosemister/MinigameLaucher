package org.minigame.map.maker;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.exception.GamemodeDoesNotSupportSpawnProp;
import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.MinigameRequirement;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.requirement.spawn.SpawnRequirement;
import org.minigame.map.truemap.PositionableMap;
import org.minigame.map.truemap.unplayable.UnreadyMap;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface MapMaker extends PositionableMap, UnreadyMap {

    public GamemodeType getCurrentGamemode();

    public Map<GamemodeType, Boolean> getSupportedGamemodesHashMap();

    public Text getName();

    public boolean updateSupportedList();

    public boolean registerProp(MinigameProp prop);
    public boolean unregisterProp(MinigameProp prop);

    default <S extends SpawnProp> S registerProp(Vector3d vector3d, Vector3d rotation, PropType<S> type) throws GamemodeDoesNotSupportSpawnProp {
        return getCurrentGamemode().createSpawn(vector3d, rotation, type);
    }

    Set<MinigameProp> getProps();

    MapMaker setPos1(Vector3i pos);

    MapMaker setPos2(Vector3i pos);

    default MapMaker setPos(int pos, Vector3i vec){
        switch(pos){
            case 0: return setPos1(vec);
            case 1: return setPos2(vec);
            default: new IndexOutOfBoundsException();
        }
        return null;
    }

    default Set<GamemodeType> getAllSupportedGamemodes(){
        return getSupportedGamemodesHashMap().keySet();
    }

    @Override
    @Deprecated
    default Set<MapGamemode> getSupportedGamemodes() {
        return MinigamePlugin.getUniquie(MapGamemode.class).stream()
                .filter(f -> getSupportedGamemodesHashMap().entrySet().stream()
                        .filter(e -> !e.getValue())
                        .anyMatch(e -> e.getKey().equals(f.getGamemode())))
                .collect(Collectors.toSet());
    }

    default Set<GamemodeType> getEnabledSupportedGamemodes(){
        Set<GamemodeType> set = new HashSet<>();
        getSupportedGamemodesHashMap().entrySet().stream().filter(e -> e.getValue()).forEach(e -> set.add(e.getKey()));
        return set;
    }

    default boolean currentMeetsRequirements(){
        return getCurrentGamemode().getRequirements().stream().allMatch(r -> meetsRequirement(r));
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
}

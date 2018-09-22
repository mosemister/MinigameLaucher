package org.minigame.map.requirement.spawn;

import com.flowpowered.math.vector.Vector3d;
import org.minigame.map.maker.MapMaker;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.running.mapmaking.RunningMapMaker;
import org.minigame.utils.SpongeMapUtils;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

public interface SpawnProp extends MinigameProp {

    Vector3d getRotation();
    void setRotation(Vector3d vector);
    EntityType getEntityType();
    Optional<Entity> getMapMakerEntity();
    SpawnProp setMapMakerEntity(Entity entity);
    Map<Key<? extends BaseValue<? extends Object>>, Object> getKeyValues();

    default Entity createEntityForMapMaker(MapMaker map){
        Location<World> loc = map.getPlusLoc(getPosition());
        Entity entity = loc.createEntity(getEntityType());
        entity.setRotation(getRotation());
        getKeyValues().entrySet().stream().forEach(e -> SpongeMapUtils.applyKeyValuePair(entity, e.getKey(), e.getValue()));
        getMapMakerEntity().ifPresent(e -> e.remove());
        setMapMakerEntity(entity);
        return entity;
    }

}

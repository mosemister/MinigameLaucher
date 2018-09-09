package org.minigame.gamemode.lobby.requirements;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.requirement.spawn.types.UserSpawnProp;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerLobbySpawnProp implements UserSpawnProp {

    Vector3d rotation;
    Vector3d pos;
    Entity entity;

    public PlayerLobbySpawnProp(Vector3d pos, Vector3d rotation){
        this.rotation = rotation;
        this.pos = pos;
    }

    @Override
    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(Vector3d vector) {
        this.rotation = vector;
    }

    @Override
    public EntityType getEntityType() {
        return EntityTypes.PLAYER;
    }

    @Override
    public Optional<Entity> getMapMakerEntity() {
        return Optional.ofNullable(this.entity);
    }

    @Override
    public SpawnProp setMapMakerEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Map<Key<? extends BaseValue<? extends Object>>, Object> getKeyValues() {
        Map<Key<? extends BaseValue<? extends Object>>, Object> map = new HashMap<>();
        map.put(Keys.AI_ENABLED, false);
        return map;
    }

    @Override
    public Vector3d getPosition() {
        return this.pos;
    }

    @Override
    public void setPosition(Vector3d pos) {
        this.pos = pos;
    }
}

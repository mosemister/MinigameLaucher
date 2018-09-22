package org.minigame.map.requirement.spawn;

import org.minigame.map.requirement.MinigameRequirement;
import org.spongepowered.api.entity.EntityType;

public interface SpawnRequirement <S extends SpawnProp> extends MinigameRequirement<S> {

    EntityType getSpawnType();

}

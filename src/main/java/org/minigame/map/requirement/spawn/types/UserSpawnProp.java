package org.minigame.map.requirement.spawn.types;

import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.spawn.SpawnProp;

public interface UserSpawnProp extends SpawnProp {

    public interface TeamUserSpawnSpawn extends UserSpawnProp {

        public int getTeam();

    }
}

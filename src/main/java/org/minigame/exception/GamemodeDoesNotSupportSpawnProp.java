package org.minigame.exception;

import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;

import java.io.IOException;

public class GamemodeDoesNotSupportSpawnProp extends IOException implements MinigameException {

    public GamemodeDoesNotSupportSpawnProp(PropType<? extends MinigameProp> prop){
        this(prop.getPropClass());
    }

    public GamemodeDoesNotSupportSpawnProp(Class<? extends MinigameProp> class1){
        super("Gamemode does not support " + class1.getSimpleName());
    }

}

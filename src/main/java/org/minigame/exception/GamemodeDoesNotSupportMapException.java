package org.minigame.exception;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.truemap.MinigameMap;

import java.io.IOException;

public class GamemodeDoesNotSupportMapException extends IOException implements MinigameException {

    public GamemodeDoesNotSupportMapException(GamemodeType type, MinigameMap map){
        super("Gamemode (" + type.getId() + ") does not support Map (" + map.getName().toPlain() + ")");
    }
}

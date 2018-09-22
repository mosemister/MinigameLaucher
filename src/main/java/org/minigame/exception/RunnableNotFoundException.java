package org.minigame.exception;

import org.minigame.gamemode.GamemodeType;

import java.io.IOException;

public class RunnableNotFoundException extends IOException {

    public RunnableNotFoundException(GamemodeType type) {
        super("Count not find a registered RunningGameBuilder for gamemode " + type.getId());
    }
}

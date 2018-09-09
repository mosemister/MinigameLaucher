package org.minigame.running;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;

public interface RunningLiveGame <T extends MinigameMap> extends RunningGame<T> {
    public MapGamemode getMapGamemode();

    public default GamemodeType getGamemode(){
        return getMapGamemode().getGamemode();
    }
}

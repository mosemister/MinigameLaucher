package org.minigame.map.truemap.playable;

import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningLiveGame;

public interface PlayingMap extends PlayableMap {

    public RunningLiveGame<? extends MinigameMap> getGame();

    public default MapGamemode getGamemode(){
        return getGame().getMapGamemode();
    }
}

package org.minigame.map.truemap.playable;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningLiveGame;

import java.util.Optional;

public interface ReadyToPlayMap extends PlayableMap {

    public Optional<PlayingMap> generatePlayingMap(RunningLiveGame<? extends MinigameMap> game);
}

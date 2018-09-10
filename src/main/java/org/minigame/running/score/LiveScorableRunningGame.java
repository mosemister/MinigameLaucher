package org.minigame.running.score;

import org.minigame.map.gamemode.type.HighscoreMapGamemode;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.running.RunningLiveGame;

public interface LiveScorableRunningGame<M extends PlayableMap> extends ScorableRunningGame<M>, RunningLiveGame<M> {

    @Override
    public HighscoreMapGamemode getMapGamemode();
}

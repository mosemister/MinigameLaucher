package org.minigame.running.score;

import org.minigame.map.gamemode.type.HighscoreMapGamemode;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningLiveGame;

public interface LiveScorableRunningGame<M extends MinigameMap> extends ScorableRunningGame<M>, RunningLiveGame<M> {

    @Override
    public HighscoreMapGamemode getMapGamemode();
}

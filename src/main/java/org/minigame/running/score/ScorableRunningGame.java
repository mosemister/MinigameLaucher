package org.minigame.running.score;

import org.minigame.map.truemap.MinigameMap;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;

public interface ScorableRunningGame<M extends MinigameMap> extends RunningScoreboardGame<M> {

    public ScorableRunningGame setScore(Text text, int score);
    public int getScore(Text text);

    default ScorableRunningGame addToScore(Text text, int addTo){
        return setScore(text, getScore(text) + addTo);
    }

    default ScorableRunningGame removeFromScore(Text text, int removeFrom){
        return setScore(text, getScore(text) + removeFrom);
    }

}

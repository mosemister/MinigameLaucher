package org.minigame.running.score;

import org.minigame.map.truemap.MinigameMap;
import org.minigame.running.RunningGame;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;

public interface RunningScoreboardGame<T extends MinigameMap> extends RunningGame<T> {

    public Scoreboard updateOrCreateScoreboard(Player board);

}

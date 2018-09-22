package org.minigame.map.truemap.playable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.PositionableMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.running.RunningLiveGame;

public class AbstractPlayingMap implements PlayingMap {

    Vector3i pos1;
    Vector3i pos2;
    RunningLiveGame<? extends MinigameMap> game;

    public AbstractPlayingMap(RunningLiveGame<? extends MinigameMap> game, PositionableMap map){
        this(game, map.getPos1(), map.getPos2());
    }

    public AbstractPlayingMap(RunningLiveGame<? extends MinigameMap> game, Vector3i pos1, Vector3i pos2){
        this.game = game;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public RunningLiveGame<? extends MinigameMap> getGame() {
        return this.game;
    }

    @Override
    public UnplayableMap getUnplayable() {
        return getGame().getMapGamemode().getMap();
    }

    @Override
    public Vector3i getPos1() {
        return this.pos1;
    }

    @Override
    public Vector3i getPos2() {
        return this.pos2;
    }
}

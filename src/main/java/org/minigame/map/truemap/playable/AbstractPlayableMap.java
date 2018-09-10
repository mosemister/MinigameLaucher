package org.minigame.map.truemap.playable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.running.RunningLiveGame;

import java.util.Optional;

public class AbstractPlayableMap implements ReadyToPlayMap {

    UnplayableMap map;
    Vector3i pos1;
    Vector3i pos2;

    public AbstractPlayableMap(UnplayableMap map, Vector3i pos1, Vector3i pos2){
        this.map = map;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public UnplayableMap getUnplayable() {
        return this.map;
    }

    @Override
    public Vector3i getPos1() {
        return this.pos1;
    }

    @Override
    public Vector3i getPos2() {
        return this.pos2;
    }

    @Override
    public Optional<PlayingMap> generatePlayingMap(RunningLiveGame<? extends MinigameMap> game) {
        game.getMapGamemode().getProps().stream().filter(p -> p instanceof MinigameProp.VisualProp).forEach(p -> ((MinigameProp.VisualProp)p).generate(getLocPos1().copy().add(((MinigameProp.VisualProp) p).getPosition())));
        return Optional.of(new AbstractPlayingMap(game, this));
    }
}

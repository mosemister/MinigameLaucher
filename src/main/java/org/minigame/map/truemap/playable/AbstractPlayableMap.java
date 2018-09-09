package org.minigame.map.truemap.playable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.spongepowered.api.text.Text;

public class AbstractPlayableMap implements PlayableMap {

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

}

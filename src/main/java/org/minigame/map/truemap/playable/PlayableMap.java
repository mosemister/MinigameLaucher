package org.minigame.map.truemap.playable;

import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.truemap.PositionableMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.spongepowered.api.text.Text;

import java.util.Set;

public interface PlayableMap extends PositionableMap {

    UnplayableMap getUnplayable();

    @Override
    default Text getName(){
        return getUnplayable().getName();
    }

    @Override
    default Set<MapGamemode> getSupportedGamemodes(){
        return getUnplayable().getSupportedGamemodes();
    }
}

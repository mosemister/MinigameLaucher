package org.minigame.map.truemap;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.gamemode.MapGamemode;
import org.spongepowered.api.text.Text;

import java.util.Set;

public interface MinigameMap {

    public Text getName();
    public Set<MapGamemode> getSupportedGamemodes();

    default boolean isSupportedGamemode(GamemodeType mode){
        return getSupportedGamemodes().stream().anyMatch(g -> g.getGamemode().equals(mode));
    }
}

package org.minigame.map.gamemode;

import org.minigame.gamemode.GamemodeType;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class AbstractMapGamemode <G extends GamemodeType> implements MapGamemode<G> {

    UnplayableMap map;
    G gamemode;
    PluginContainer container;

    public AbstractMapGamemode(PluginContainer container, UnplayableMap map, G type){
        this.map = map;
        this.gamemode = type;
        this.container = container;
    }

    @Override
    public UnplayableMap getMap() {
        return this.map;
    }

    @Override
    public G getGamemode() {
        return this.gamemode;
    }

    @Override
    public PluginContainer getPlugin() {
        return this.container;
    }
}

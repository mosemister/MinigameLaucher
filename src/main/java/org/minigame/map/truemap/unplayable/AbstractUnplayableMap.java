package org.minigame.map.truemap.unplayable;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public abstract class AbstractUnplayableMap implements UnplayableMap {

    PluginContainer container;
    Text name;

    public AbstractUnplayableMap(PluginContainer container, Text text){
        this.container = container;
        this.name = text;
    }

    @Override
    public PluginContainer getPlugin() {
        return container;
    }

    @Override
    public Text getName() {
        return name;
    }
}

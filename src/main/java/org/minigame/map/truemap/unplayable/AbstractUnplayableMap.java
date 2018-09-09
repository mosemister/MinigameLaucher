package org.minigame.map.truemap.unplayable;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

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

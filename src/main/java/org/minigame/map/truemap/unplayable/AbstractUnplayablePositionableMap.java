package org.minigame.map.truemap.unplayable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public class AbstractUnplayablePositionableMap implements UnplayablePositionableMap {

    Vector3i pos1;
    Vector3i pos2;
    Text name;

    public AbstractUnplayablePositionableMap(Text name, Vector3i pos1, Vector3i pos2){
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.name = name;
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public Vector3i getPos1(){
        return this.pos1;
    }

    @Override
    public Vector3i getPos2(){
        return this.pos2;
    }
}

package org.minigame.map.truemap.unplayable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.utils.UniquieId;
import org.spongepowered.api.block.BlockState;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface UnplayableMap extends UnreadyMap, UniquieId.UniquieColoredId {

    Map<Vector3i, BlockState> getDefaultMapSnapshot();

    default Vector3i getMinimumSize(){
        int x = 0;
        int y = 0;
        int z = 0;
        for (Vector3i vector : getDefaultMapSnapshot().keySet()){
            if(vector.getX() > x){
                x = vector.getX();
            }
            if(vector.getY() > y){
                y = vector.getX();
            }
            if(vector.getZ() > z){
                z = vector.getZ();
            }
        }
        return new Vector3i(x, y, z);
    }

    @Override
    default Set<MapGamemode> getSupportedGamemodes(){
        return MinigamePlugin.getUniquie(MapGamemode.class).stream().filter(mgm -> mgm.getMap().equals(this)).collect(Collectors.toSet());
    }

}

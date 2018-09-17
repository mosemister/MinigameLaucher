package org.minigame.map.truemap.unplayable;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.PositionableMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface UnplayablePositionableMap extends UnplayableMap, PositionableMap {

    File getFile();

    @Override
    default Map<Vector3i, BlockState> getDefaultMapSnapshot(){
        Map<Vector3i, BlockState> map = new HashMap<>();
        World world = getWorld();
        Vector3i pos1 = getPos1();
        Vector3i pos2 = getPos2();
        for(int x = pos1.getX(); x < pos2.getX(); x++){
            for(int y = pos1.getY(); y < pos2.getY(); y++){
                for(int z = pos1.getZ(); z < pos2.getZ(); z++){
                    Location<World> loc = world.getLocation(x, y, z);
                    BlockState state = loc.getBlock();
                    state = state.withExtendedProperties(loc);
                    map.put(new Vector3i(x, y, z), state);
                }
            }
        }
        return map;
    }

}

package org.minigame.map.builder;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.map.truemap.unplayable.UnplayablePositionableMap;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class MapBuilder {

    protected abstract void onBuilt(PlayableMap map, Object plugin);

    private Location<World> location;
    protected int maxStackedSize = 100;
    protected int delay = 1;
    protected TimeUnit delayUnit = TimeUnit.MICROSECONDS;
    private UnplayableMap mapToCopy;

    public MapBuilder(Location<World> loc, UnplayableMap map){
        this.location = loc;
        this.mapToCopy = map;
    }

    public Location<World> startingPosition(){
        return this.location;
    }

    public UnplayableMap getMapToCopy(){
        return this.mapToCopy;
    }

    private List<Map<Location<World>, BlockState>> createStore(World world, UnplayablePositionableMap map){
        List<Map<Location<World>, BlockState>> stores = new ArrayList<>();
        Location<World> startingPos = world.getLocation(map.getPos1());
        Location<World> endingPos = world.getLocation(map.getPos2());
        Map<Location<World>, BlockState> map1 = new HashMap<>();
        for(int x = startingPos.getBlockX(); x < endingPos.getBlockX(); x++){
            for(int y = startingPos.getBlockY(); y < endingPos.getBlockY(); y++){
                for(int z = startingPos.getBlockZ(); z < endingPos.getBlockZ(); z++){
                    map1.put(world.getLocation(x, y, z), startingPos.copy().add(x, y, z).getBlock());
                    if(map1.size() == maxStackedSize){
                        stores.add(map1);
                        map1 = new HashMap<>();
                    }
                }
            }
        }
        stores.add(map1);
        return stores;
    }

    private List<Map<Location<World>, BlockState>> createStore(UnplayableMap map){
        List<Map<Location<World>, BlockState>> stores = new ArrayList<>();
        Map<Vector3i, BlockState> blocks = map.getDefaultMapSnapshot();
        Map<Location<World>, BlockState> map1 = new HashMap<>();
        for(Map.Entry<Vector3i, BlockState> entry : blocks.entrySet()){
            Location<World> loc = this.location.copy().add(entry.getKey());
            map1.put(loc, entry.getValue());
            if(map1.size() == maxStackedSize){
                stores.add(map1);
                map1 = new HashMap<>();
            }
        }
        stores.add(map1);
        return stores;
    }

    public void clear(Object plugin){
        //TODO
    }

    public void build(Object plugin){
        List<Map<Location<World>, BlockState>> stores = null;
        World world = MinigamePlugin.getPlugin().getMinigamesWorld();
        if(this.mapToCopy instanceof UnplayablePositionableMap){
            stores = createStore(world, (UnplayablePositionableMap)this.mapToCopy);
        }else{
            stores = createStore(this.mapToCopy);
        }

        //STORED

        for(Map<Location<World>, BlockState> map : stores){
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> map.entrySet().stream()
                            .forEach(e -> e.getKey().setBlock(e.getValue(), BlockChangeFlags.NONE)))
                    .delay(delay, delayUnit).submit(plugin);
        }

    }
}

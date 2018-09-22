package org.minigame.map.builder;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.playable.AbstractPlayableMap;
import org.minigame.map.truemap.playable.PlayableMap;
import org.minigame.map.truemap.playable.ReadyToPlayMap;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.map.truemap.unplayable.UnplayablePositionableMap;
import org.minigame.plugin.MinigamePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class MapBuilder {

    protected abstract void onBuilt(ReadyToPlayMap map, Object plugin);
    protected abstract void onClear();
    protected abstract void onStackFinish(int totalStack, int current, Map<Location<World>, BlockState> written);

    protected Location<World> location;
    protected int maxStackedSize = 100;
    protected int delay = 1;
    protected TimeUnit delayUnit = TimeUnit.MICROSECONDS;
    protected UnplayableMap mapToCopy;

    public MapBuilder(PlayableMap map) {
        this(map.getLocPos1(), map.getUnplayable());
    }

    public MapBuilder(Location<World> loc, UnplayableMap map) {
        this.location = loc;
        this.mapToCopy = map;
    }

    public Location<World> startingPosition() {
        return this.location;
    }

    public UnplayableMap getMapToCopy() {
        return this.mapToCopy;
    }

    private List<Map<Location<World>, BlockState>> createStore(World world, UnplayablePositionableMap map) {
        List<Map<Location<World>, BlockState>> stores = new ArrayList<>();
        Location<World> startingPos = world.getLocation(map.getPos1());
        Location<World> endingPos = world.getLocation(map.getPos2());
        Map<Location<World>, BlockState> map1 = new HashMap<>();
        for (int x = startingPos.getBlockX(); x < endingPos.getBlockX(); x++) {
            for (int y = startingPos.getBlockY(); y < endingPos.getBlockY(); y++) {
                for (int z = startingPos.getBlockZ(); z < endingPos.getBlockZ(); z++) {
                    map1.put(world.getLocation(x, y, z), startingPos.copy().add(x, y, z).getBlock());
                    if (map1.size() == maxStackedSize) {
                        stores.add(map1);
                        map1 = new HashMap<>();
                    }
                }
            }
        }
        stores.add(map1);
        return stores;
    }

    private List<Map<Location<World>, BlockState>> createStore(UnplayableMap map) {
        List<Map<Location<World>, BlockState>> stores = new ArrayList<>();
        Map<Vector3i, BlockState> blocks = map.getDefaultMapSnapshot();
        Map<Location<World>, BlockState> map1 = new HashMap<>();
        for (Map.Entry<Vector3i, BlockState> entry : blocks.entrySet()) {
            Location<World> loc = this.location.copy().add(entry.getKey());
            map1.put(loc, entry.getValue());
            if (map1.size() == maxStackedSize) {
                stores.add(map1);
                map1 = new HashMap<>();
            }
        }
        stores.add(map1);
        return stores;
    }

    public void clear(Object plugin) {
        World world = MinigamePlugin.getPlugin().getMinigamesWorld();
        Vector3i pos1 = location.getBlockPosition();
        Vector3i pos2 = pos1.clone().add(this.mapToCopy.getMinimumSize());
        for (int x1 = pos1.getX(); x1 < pos2.getX(); x1++) {
            final int x = x1;
            for (int y1 = pos1.getY(); y1 < pos2.getY(); y1++) {
                final int y = y1;
                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                    for (int z = pos1.getZ(); z < pos2.getZ(); z++) {
                        world.getLocation(x, y, z).setBlockType(BlockTypes.AIR, BlockChangeFlags.NONE);
                    }
                    if(((x + 1) == pos2.getX()) && ((y + 1) == pos2.getY())){
                        this.onClear();
                    }
                }).delay(this.delay * ((x - pos1.getX()) + (y - pos1.getY())), this.delayUnit).submit(plugin);
            }
        }

    }

    public void build(Object plugin) {
        List<Map<Location<World>, BlockState>> stores = null;
        World world = MinigamePlugin.getPlugin().getMinigamesWorld();
        if (this.mapToCopy instanceof UnplayablePositionableMap) {
            stores = createStore(world, (UnplayablePositionableMap) this.mapToCopy);
        } else {
            stores = createStore(this.mapToCopy);
        }
        Vector3i pos1 = location.getBlockPosition();
        Vector3i pos2 = pos1.clone();

        //STORED

        int stored = stores.size();
        for (int A = 0; A < stored; A++) {
            final int AFin = A;
            Map<Location<World>, BlockState> map = stores.get(A);
            for (Location<World> loc : map.keySet()) {
                if (loc.getBlockX() > pos2.getX()) {
                    pos2 = new Vector3i(loc.getBlockX(), pos2.getY(), pos2.getZ());
                }
                if (loc.getBlockY() > pos2.getY()) {
                    pos2 = new Vector3i(pos2.getX(), loc.getBlockY(), pos2.getZ());
                }
                if (loc.getBlockZ() > pos2.getZ()) {
                    pos2 = new Vector3i(pos2.getX(), pos2.getY(), loc.getBlockZ());
                }
            }
            final Vector3i pos2Final = pos2;
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        /*map.entrySet().stream()
                                .forEach(e -> e.getKey().setBlock(e.getValue(), BlockChangeFlags.NONE));*/
                        for(Map.Entry<Location<World>, BlockState> entry : map.entrySet()){
                            Location<World> loc = entry.getKey();
                            BlockState state = entry.getValue();
                            loc.setBlock(state, BlockChangeFlags.NONE);
                        }
                        this.onStackFinish(stored, AFin, map);
                        if ((AFin + 1) == stored) {
                            AbstractPlayableMap playable = new AbstractPlayableMap(this.mapToCopy, pos1, pos2Final);
                            this.onBuilt(playable, plugin);
                        }
                    })
                    .delay(delay * A, delayUnit)
                    .submit(plugin);
        }
    }
}

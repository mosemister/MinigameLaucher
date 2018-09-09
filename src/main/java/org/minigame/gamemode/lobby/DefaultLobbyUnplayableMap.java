package org.minigame.gamemode.lobby;

import com.flowpowered.math.vector.Vector3i;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.map.truemap.unplayable.AbstractUnplayableMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class DefaultLobbyUnplayableMap extends AbstractUnplayableMap {

    public DefaultLobbyUnplayableMap() {
        super(MinigamePlugin.getPlugin().getContainer(), Text.builder("DefaultMinigameLobby").color(TextColors.AQUA).build());
    }

    @Override
    public Map<Vector3i, BlockState> getDefaultMapSnapshot() {
        Map<Vector3i, BlockState> map = new HashMap<>();
        for(int x = 0; x < 16; x++){
            for(int y = 0; y < 16; y++){
                map.put(new Vector3i(x, y, 16), BlockTypes.BARRIER.getDefaultState());
                map.put(new Vector3i(x, y, 0), BlockTypes.BARRIER.getDefaultState());
            }
        }
        for(int z = 0; z < 16; z++){
            for(int y = 0; y < 16; y++){
                map.put(new Vector3i(16, y, z), BlockTypes.BARRIER.getDefaultState());
                map.put(new Vector3i(0, y, z), BlockTypes.BARRIER.getDefaultState());
            }
        }
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                map.put(new Vector3i(x, 16, z), BlockTypes.BARRIER.getDefaultState());
                map.put(new Vector3i(x, 0, z), BlockTypes.GLASS.getDefaultState());
            }
        }
        return map;
    }
}

package org.minigame.utils;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Optional;

public class MinigameWorldGenerator {

    public static final String DEFAULT_MINIGAME_WORLD_NAME = "Minigame";
    public static Vector3i MINIGAME_MAP_CREATOR_STARTING_POSITION = new Vector3i(0, 0, 0);
    public static Vector3i MINIGAME_MAP_PLAYING_STARTING_POSITION = new Vector3i(0, 0, 100000);

    public static World loadOrCreateWorld(String name) throws IOException {
        return loadWorld(name).orElse(createWorld(name));
    }

    public static Optional<World> loadWorld(String name){
        Optional<WorldProperties> opProp = Sponge.getServer().getWorldProperties(name);
        if(opProp.isPresent()){
            return Sponge.getServer().loadWorld(opProp.get());
        }
        return Optional.empty();
    }

    public static World createWorld(String name) throws IOException {
        WorldProperties properties = Sponge.getServer().createWorldProperties(name, WorldArchetypes.THE_VOID);
        properties.setCommandsAllowed(true);
        properties.setGameMode(GameModes.SURVIVAL);
        properties.setKeepSpawnLoaded(false);
        properties.setLoadOnStartup(true);
        properties.setEnabled(true);
        return Sponge.getServer().loadWorld(properties).get();
    }
}

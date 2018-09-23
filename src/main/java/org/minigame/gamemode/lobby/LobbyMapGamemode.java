package org.minigame.gamemode.lobby;

import com.flowpowered.math.vector.Vector3d;
import org.minigame.gamemode.lobby.requirements.PlayerLobbySpawnProp;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.gamemode.outofboundhandlers.OutOfBoundsHandler;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.DefaultRegisters;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.lobby.RunningLobby;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LobbyMapGamemode implements MapGamemode<LobbyType> {

    @Override
    public Set<MinigameProp> getProps() {
        Set<MinigameProp> set = new HashSet<>();
        set.add(new PlayerLobbySpawnProp(new Vector3d(8, 2, 8), new Vector3d(0, 0, 0)));
        return set;
    }

    @Override
    public UnplayableMap getMap() {
        return DefaultRegisters.DEFAULT_LOBBY_MAP;
    }

    @Override
    public LobbyType getGamemode() {
        //return MinigamePlugin.<GamemodeType>getUniquie(MinigamePlugin.PLUGIN_ID + ":lobby").get();
        /*Optional<GamemodeType> opType = MinigamePlugin.getUniquie(MinigamePlugin.PLUGIN_ID + ":lobby");
        if(opType.isPresent()){
            return (LobbyType) opType.get();
        }
        System.out.println("ID was wrong: Here are the ids");
        MinigamePlugin.getUniquieSet(GamemodeType.class).stream().forEach(g -> System.err.println("\t - " + g.getIdName()));
        return (LobbyType) opType.get();*/
        return DefaultRegisters.LOBBY_GAMEMODE;
    }

    @Override
    public Optional<OutOfBoundsHandler> getOutOfBoundsHandler() {
        return Optional.of(DefaultRegisters.CLOSEST_SPAWN_LOCATION_OUT_OF_BOUND_HANDLER);
    }

    @Override
    public boolean isSuitable(RunningLobby lobby) {
        //TODO
        return false;
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }
}

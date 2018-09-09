package org.minigame.gamemode.lobby;

import com.flowpowered.math.vector.Vector3d;
import org.minigame.exception.GamemodeDoesNotSupportSpawnProp;
import org.minigame.gamemode.lobby.requirements.PlayerLobbySpawnProp;
import org.minigame.map.requirement.MinigameProp;
import org.minigame.map.requirement.PropType;
import org.minigame.map.requirement.spawn.SpawnProp;
import org.minigame.map.requirement.spawn.types.UserSpawnProp;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.gamemode.GamemodeType;
import org.minigame.gamemode.lobby.requirements.PlayerLobbySpawnRequirement;
import org.minigame.map.gamemode.MapGamemode;
import org.minigame.map.requirement.MinigameRequirement;
import org.minigame.map.truemap.unplayable.UnplayableMap;
import org.minigame.plugin.command.commands.MapMakerCommand;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LobbyType implements GamemodeType {

    @Override
    public Set<MinigameRequirement<? extends MinigameProp>> getRequirements() {
        Set<MinigameRequirement<? extends MinigameProp>> set = new HashSet<>();
        set.add(new PlayerLobbySpawnRequirement());
        return set;
    }

    @Override
    public <S extends SpawnProp> S createSpawn(Vector3d pos, Vector3d rotation, PropType<S> class1) throws GamemodeDoesNotSupportSpawnProp {
        if(class1.equals(PropType.USER_SPAWN)){
            return (S)new PlayerLobbySpawnProp(pos, rotation);
        }
        throw new GamemodeDoesNotSupportSpawnProp(class1);
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public String getIdName() {
        return "Lobby";
    }
}

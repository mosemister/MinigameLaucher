package org.minigame.gamemode.lobby.requirements;

import org.minigame.plugin.MinigamePlugin;
import org.minigame.map.requirement.spawn.SpawnRequirement;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.plugin.PluginContainer;

public class PlayerLobbySpawnRequirement implements SpawnRequirement<PlayerLobbySpawnProp> {
    @Override
    public EntityType getSpawnType() {
        return EntityTypes.PLAYER;
    }

    @Override
    public int getAmountRequired() {
        return 1;
    }

    @Override
    public boolean isProp(PlayerLobbySpawnProp prop) {
        return true;
    }

    @Override
    public Class<PlayerLobbySpawnProp> getPropClass() {
        return PlayerLobbySpawnProp.class;
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public String getIdName() {
        return "lobby-player-spawn";
    }
}

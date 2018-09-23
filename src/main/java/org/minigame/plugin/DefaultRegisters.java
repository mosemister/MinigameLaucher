package org.minigame.plugin;

import org.minigame.gamemode.lobby.DefaultLobbyUnplayableMap;
import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.gamemode.lobby.LobbyType;
import org.minigame.gamemode.lobby.requirements.PlayerLobbySpawnRequirement;
import org.minigame.map.gamemode.outofboundhandlers.ClosestSpawnLocationOutOfBoundHandler;
import org.minigame.team.splitter.OrderedTeamSplitter;

public interface DefaultRegisters {

    OrderedTeamSplitter ORDERED_TEAM_SPLITTER = new OrderedTeamSplitter();
    LobbyType LOBBY_GAMEMODE = new LobbyType();
    PlayerLobbySpawnRequirement LOBBY_SPAWN_REQUIREMENT = new PlayerLobbySpawnRequirement();
    DefaultLobbyUnplayableMap DEFAULT_LOBBY_MAP = new DefaultLobbyUnplayableMap();
    LobbyMapGamemode DEFAULT_LOBBY_MAP_GAMEMODE = new LobbyMapGamemode();
    ClosestSpawnLocationOutOfBoundHandler CLOSEST_SPAWN_LOCATION_OUT_OF_BOUND_HANDLER = new ClosestSpawnLocationOutOfBoundHandler();

}

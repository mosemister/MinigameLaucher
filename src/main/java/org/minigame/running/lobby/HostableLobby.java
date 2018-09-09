package org.minigame.running.lobby;

import org.minigame.gamemode.lobby.LobbyMapGamemode;
import org.minigame.map.truemap.playable.PlayingMap;
import org.minigame.running.midjoinable.InvitableGame;

import java.util.*;

public class HostableLobby extends RunningLobby implements InvitableGame<PlayingMap> {

    protected Set<UUID> hosts = new HashSet<>();

    public HostableLobby(PlayingMap map, LobbyMapGamemode gamemode, UUID uuids){
        this(map, gamemode, Arrays.asList(uuids));
    }

    public HostableLobby(PlayingMap map, LobbyMapGamemode gamemode, Collection<UUID> uuids) {
        super(map, gamemode);
        hosts.addAll(hosts);
    }

    @Override
    public void addHosts(UUID... uuids) {
        for(UUID uuid : uuids){
            this.hosts.add(uuid);
        }
    }

    @Override
    public Set<UUID> getHosts() {
        return this.hosts;
    }
}

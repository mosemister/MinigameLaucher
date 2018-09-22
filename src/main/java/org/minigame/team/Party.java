package org.minigame.team;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Party implements PlayerGroup{

    Set<Player> players = new HashSet<>();

    public Party(Player... party){
        this(Arrays.asList(party));
    }

    public Party(Collection<Player> party){
        this.players.addAll(party);
    }

    @Override
    public PlayerGroup removePlayers(Player... players) {
        for(Player player : players){
            this.players.remove(player);
        }
        return this;
    }

    @Override
    public PlayerGroup addPlayers(Player... players) {
        for(Player player : players){
            this.players.add(player);
        }
        return this;
    }

    @Override
    public Set<Player> getPlayers() {
        return this.players;
    }
}

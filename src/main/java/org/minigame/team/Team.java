package org.minigame.team;

import org.spongepowered.api.entity.living.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Team implements PlayerGroup.PartyGroup {

    Set<Party> parties = new HashSet<>();

    public Team(Party... collection){
        this(Arrays.asList(collection));
    }

    public Team(Collection<Party> collection){
        this.parties.addAll(collection);
    }

    @Override
    public PartyGroup addParties(Party... parties) {
        this.parties.addAll(Arrays.asList(parties));
        return this;
    }

    @Override
    public Set<Party> getParties(){
        return this.parties;
    }

    @Override
    public PlayerGroup removePlayers(Player... players) {
        for(Player player : players) {
            Optional<Party> opParty = parties.stream().filter(p -> p.getPlayers().contains(player)).findFirst();
            if(opParty.isPresent()){
                this.parties.remove(opParty.get());
            }
        }
        return this;
    }

    @Override
    public PlayerGroup addPlayers(Player... players) {
        for(Player player : players){
            this.parties.add(new Party(player));
        }
        return this;
    }

    @Override
    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        parties.stream().forEach(p -> players.addAll(p.getPlayers()));
        return players;
    }
}

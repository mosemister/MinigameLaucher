package org.minigame.team;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Iterator;
import java.util.Set;

public interface PlayerGroup extends Iterable<Player>{

    PlayerGroup removePlayers(Player... players);
    PlayerGroup addPlayers(Player... players);
    public Set<Player> getPlayers();

    @Override
    default Iterator<Player> iterator() {
        return getPlayers().iterator();
    }

    interface PartyGroup extends PlayerGroup{

        PartyGroup addParties(Party... parties);
        Set<Party> getParties();
    }
}

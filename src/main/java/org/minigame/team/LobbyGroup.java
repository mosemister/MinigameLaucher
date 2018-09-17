package org.minigame.team;

import org.minigame.team.splitter.TeamSplitter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Set;

public class LobbyGroup implements PlayerGroup.PartyGroup {

    Team team = new Team();

    public Set<Team> splitPlayersIntoTeamsByMaxTeamSize(int maxTeamSize, TeamSplitter splitter) {
        return splitter.splitPlayersBySize(this.team, maxTeamSize);
    }

    public Set<Team> splitPlayersIntoTeamsByAmountOfTeams(int maxTeams, TeamSplitter splitter) {
        return splitter.splitTeamsByTeamCount(this.team, maxTeams);
    }

    @Override
    public PlayerGroup removePlayers(Player... players) {
        return team.removePlayers(players);
    }

    @Override
    public PlayerGroup addPlayers(Player... players) {
        return team.addPlayers(players);
    }

    @Override
    public Set<Player> getPlayers() {
        return team.getPlayers();
    }

    @Override
    public PartyGroup addParties(Party... parties) {
        return team.addParties(parties);
    }

    @Override
    public Set<Party> getParties() {
        return team.getParties();
    }
}

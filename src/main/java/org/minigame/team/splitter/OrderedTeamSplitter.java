package org.minigame.team.splitter;

import org.minigame.plugin.MinigamePlugin;
import org.minigame.team.Party;
import org.minigame.team.PlayerGroup;
import org.minigame.team.Team;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderedTeamSplitter implements TeamSplitter {
    @Override
    public Set<Team> splitPlayersBySize(PlayerGroup.PartyGroup parties, int teamMaxSize) {
        int teamCount = parties.getParties().size() / teamMaxSize;
        return splitTeams(parties, teamCount, teamMaxSize);
    }

    @Override
    public Set<Team> splitTeamsByTeamCount(PlayerGroup.PartyGroup parties, int teamCount) {
        int playersPerTeam = (parties.getParties().size() / teamCount);
        return splitTeams(parties, teamCount, playersPerTeam);
    }

    private Set<Team> splitTeams(PlayerGroup.PartyGroup parties, int teamCount, int playersPerTeam){
        Team[] teams = new Team[teamCount];
        for(int a = 0; a < teamCount; a++){
            teams[a] = new Team();
        }
        Set<Player> playersToAdd = parties.getPlayers();
        Set<Party> realParties = parties.getParties().stream().filter(p -> p.getPlayers().size() > 1).collect(Collectors.toSet());
        while(!realParties.isEmpty()){
            boolean timeout = true;
            for(Party party : realParties){
                for(Team team : teams){
                    if(team.getPlayers().size() <= (playersPerTeam - party.getPlayers().size())){
                        team.addParties(party);
                        realParties.remove(party);
                        playersToAdd.removeAll(party.getPlayers());
                        timeout = false;
                    }
                }
            }
            if(timeout){
                break;
            }
        }
        while(!playersToAdd.isEmpty()){
            boolean timeout = true;
            for(Team team : teams){
                if((team.getPlayers().size() < playersPerTeam)){
                    Player player = playersToAdd.iterator().next();
                    team.addPlayers(player);
                    playersToAdd.remove(player);
                    timeout = false;
                }
            }
            if(timeout){
                for(Team team : teams){
                    Player player = playersToAdd.iterator().next();
                    team.addPlayers(player);
                    playersToAdd.remove(player);
                }
            }
        }
        return new HashSet<>(Arrays.asList(teams));
    }

    @Override
    public PluginContainer getPlugin() {
        return MinigamePlugin.getPlugin().getContainer();
    }

    @Override
    public String getIdName() {
        return "OrderedTeamSplitter";
    }
}
